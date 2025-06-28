package kr.open.library.logcat.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kr.open.library.logcat.Logx
import kr.open.library.logcat.vo.LogxType
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 로그 엔트리 데이터 클래스
 */
private data class LogEntry(
    val logType: LogxType,
    val tag: String,
    val msg: String,
    val timestamp: String = SimpleDateFormat("yy-MM-dd, HH:mm:ss.SSS", Locale.US)
        .format(Date(System.currentTimeMillis()))
)

/**
 * Required Permission file read / write
 */
internal class LogxFileManager(path:String) {

    private val fm: FileManager by lazy { FileManager(path) }
    private val logFileTitle by lazy { "${currentTimeFormatted()}_Log.txt" }

    fun addWriteLog(logType: LogxType, tag: String, msg: String) {
        fm.addLogEntry(LogEntry(logType, tag, msg))
    }

    private fun currentTimeFormatted():String =
        SimpleDateFormat("yy-MM-dd, HH:mm:ss.SSS",
            Locale.US).format(Date(System.currentTimeMillis()))


    private class FileManager(path: String) {

        val file = File(path)

        private val isActive = AtomicBoolean(true)

        // 채널을 통한 로그 엔트리 버퍼링
        private val logChannel = Channel<LogEntry>(capacity = 1000)

        // SupervisorJob을 사용하여 예외 발생 시에도 다른 작업이 계속 실행되도록 함
        private val logWriterScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        init {
            createDirectoryIfNeeded()
            startLogWriter()
            finishCheck()
        }

        fun addLogEntry(logEntry: LogEntry) {
            if (isActive.get()) {
                logWriterScope.launch {
                    try {
                        logChannel.send(logEntry)
                    } catch (e: Exception) {
                        Log.e("LogxFileManager", "[Error] Failed to add log entry: ${e.message}")
                    }
                }
            }
        }

        private fun createDirectoryIfNeeded() {
            if (file.exists()) return

            try {
                if (file.mkdirs()) {
                    Log.d(Logx.getAppName(), "LogxFileManager, Directory created: ${file.path}")
                } else {
                    Log.e(Logx.getAppName(), "[Error] LogxFileManager, Failed to create directory: ${file.path}")
                }
            } catch (e: Exception) {
                Log.e(Logx.getAppName(), "[Error] LogxFileManager, Exception while creating directory", e)
            }
        }

        private fun startLogWriter() {
            logWriterScope.launch {
                val logEntries = mutableListOf<LogEntry>()

                try {
                    while (isActive.get()) {
                        // 배치로 로그 엔트리들을 처리
                        logEntries.clear()

                        // 첫 번째 엔트리를 기다림 (블로킹)
                        val firstEntry = logChannel.receive()
                        logEntries.add(firstEntry)

                        // 추가 엔트리들을 논블로킹으로 수집 (최대 50개까지)
                        repeat(49) {
                            val entry = logChannel.tryReceive().getOrNull()
                            if (entry != null) {
                                logEntries.add(entry)
                            } else {
                                return@repeat
                            }
                        }

                        // 배치로 파일에 쓰기
                        writeBatchToFile(logEntries)
                    }
                } catch (e: Exception) {
                    Log.e(Logx.getAppName(), "[Error] LogxFileManager, Log writer error: ${e.message}", e)
                } finally {
                    // 남은 로그들 처리
                    flushRemainingLogs()
                }
            }
        }

        private suspend fun writeBatchToFile(logEntries: List<LogEntry>) {
            if (logEntries.isEmpty()) return

            val logFile = File(file, "${logEntries.first().timestamp.split(",")[0]}_Log.txt")

            try {
                createFileIfNeeded(logFile)

                BufferedWriter(FileWriter(logFile, true)).use { writer ->
                    logEntries.forEach { entry ->
                        val logLine =
                            "${entry.timestamp}/${entry.logType.logTypeString}/${entry.tag} : ${entry.msg}"
                        writer.write(logLine)
                        writer.newLine()
                    }
                    writer.flush()
                }
            } catch (e: Exception) {
                Log.e(Logx.getAppName(), "[Error] LogxFileManager, Failed to write batch: ${e.message}", e)
            }
        }

        private fun createFileIfNeeded(file: File) {
            if (file.exists()) return

            try {
                if (file.createNewFile()) {
                    Log.d("LogxFileManager", "Log file created: ${file.path}")
                } else {
                    Log.e(Logx.getAppName(), "[Error] LogxFileManager, Failed to create log file: ${file.path}")
                }
            } catch (e: IOException) {
                Log.e(Logx.getAppName(), "[Error] LogxFileManager, IOException creating file: ${file.path}", e)
            }
        }

        private fun flushRemainingLogs() {
            val remainingLogs = mutableListOf<LogEntry>()

            // 채널에 남은 모든 로그 수집
            while (true) {
                val entry = logChannel.tryReceive().getOrNull() ?: break
                remainingLogs.add(entry)
            }

            if (remainingLogs.isNotEmpty()) {
                try {
                    val logFile = File(file, "${remainingLogs.first().timestamp.split(",")[0]}_Log.txt")
                    createFileIfNeeded(logFile)

                    BufferedWriter(FileWriter(logFile, true)).use { writer ->
                        remainingLogs.forEach { entry ->
                            val logLine = "${entry.timestamp}/${entry.logType.logTypeString}/${entry.tag} : ${entry.msg}"
                            writer.write(logLine)
                            writer.newLine()
                        }
                        writer.flush()
                    }
                    Log.d(Logx.getAppName(), "LogxFileManager, Flushed ${remainingLogs.size} remaining logs")
                } catch (e: Exception) {
                    Log.e(Logx.getAppName(), "[Error] LogxFileManager, Failed to flush remaining logs", e)
                }
            }
        }

        private fun finishCheck() {
            // 앱 종료 시 로그 저장
            Runtime.getRuntime().addShutdownHook(Thread {
                shutdownLogger()
            })

            // 크래시 발생 시 로그 저장
            Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
                shutdownLogger()
                throwable.printStackTrace()
            }
        }

        private fun shutdownLogger() {
            if (isActive.compareAndSet(true, false)) {
                try {
                    logChannel.close()
                    flushRemainingLogs()
                    Log.d(Logx.getAppName(), "LogxFileManager, FileManager shutdown completed")
                } catch (e: Exception) {
                    Log.e(Logx.getAppName(), "[Error] LogxFileManager, Error during shutdown: ${e.message}", e)
                }
            }
        }
    }
}
