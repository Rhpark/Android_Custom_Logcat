package kr.open.library.logcat.writer

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.vo.LogxType
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 코루틴 기반 비동기 로그 파일 작성기
 * 메인 스레드를 블로킹하지 않고 백그라운드에서 파일 I/O 수행
 * 
 * 특징:
 * - 논블로킹 I/O
 * - 백프레셔 처리 (Channel 기반)
 * - 버퍼링으로 성능 최적화
 * - 적절한 스코프 관리
 */
class AsyncLogFileWriter(
    private val config: LogxConfig,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) : LogFileWriter {

    private data class LogEntry(
        val logType: LogxType,
        val tag: String,
        val message: String,
        val timestamp: String = currentTimestamp()
    )

    // 백프레셔 방지를 위한 채널 (최대 1000개 항목)
    private val logChannel = Channel<LogEntry>(capacity = 1000)
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    @Volatile
    private var isActive = true
    
    private val processingJob: Job
    
    init {
        // 백그라운드에서 로그 처리
        processingJob = scope.launch {
            logChannel.receiveAsFlow()
                .collect { logEntry ->
                    if (isActive) {
                        try {
                            writeToFile(logEntry)
                        } catch (e: Exception) {
                            // 파일 쓰기 실패 시에도 앱이 크래시되지 않도록
                            android.util.Log.e("AsyncLogFileWriter", "Failed to write log to file", e)
                        }
                    }
                }
        }
    }

    override fun writeLog(logType: LogxType, tag: String, message: String) {
        if (!config.isDebugSave || !isActive) return
        
        val logEntry = LogEntry(logType, tag, message)
        
        // 논블로킹 방식으로 채널에 전송
        scope.launch {
            try {
                logChannel.send(logEntry)
            } catch (e: Exception) {
                // 채널이 닫혔거나 가득 찬 경우 무시 (앱 안정성 우선)
                android.util.Log.w("AsyncLogFileWriter", "Failed to queue log entry: ${e.message}")
            }
        }
    }

    private suspend fun writeToFile(logEntry: LogEntry) = withContext(Dispatchers.IO) {
        try {
            val logFile = getLogFile()
            val formattedLog = formatLogEntry(logEntry)
            
            logFile.appendText(formattedLog + "\n")
            
        } catch (e: IOException) {
            throw LogFileWriteException("Failed to write log to file", e)
        }
    }

    private fun getLogFile(): File {
        val fileName = "logx_${fileNameFormat.format(Date())}.log"
        val logFile = File(config.saveFilePath, fileName)
        
        // 디렉토리가 없으면 생성
        logFile.parentFile?.mkdirs()
        
        return logFile
    }

    private fun formatLogEntry(logEntry: LogEntry): String {
        return "${logEntry.timestamp} [${logEntry.logType.logTypeString}] ${config.appName} [${logEntry.tag}] : ${logEntry.message}"
    }

    override fun cleanup() {
        scope.launch {
            isActive = false
            
            // 남은 로그들을 모두 처리
            logChannel.close()
            
            // 처리 작업 완료 대기
            processingJob.join()
            
            // 스코프 정리
            scope.cancel()
        }
    }

    companion object {
        private fun currentTimestamp(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        }
    }
}

/**
 * 버퍼링된 비동기 로그 파일 작성기
 * 더 나은 성능을 위해 여러 로그를 배치 처리
 */
class BufferedAsyncLogFileWriter(
    private val config: LogxConfig,
    private val bufferSize: Int = 50,
    private val flushIntervalMs: Long = 5000L,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) : LogFileWriter {

    private data class LogEntry(
        val logType: LogxType,
        val tag: String,
        val message: String,
        val timestamp: String = currentTimestamp()
    )

    private val logChannel = Channel<LogEntry>(capacity = Channel.UNLIMITED)
    private val buffer = mutableListOf<LogEntry>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    private val fileNameFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    @Volatile
    private var isActive = true
    
    private val processingJob: Job
    private val flushJob: Job

    init {
        // 로그 수집 및 버퍼링
        processingJob = scope.launch {
            for (logEntry in logChannel) {
                if (!isActive) break
                
                synchronized(buffer) {
                    buffer.add(logEntry)
                    if (buffer.size >= bufferSize) {
                        flushBuffer()
                    }
                }
            }
        }
        
        // 주기적 플러시
        flushJob = scope.launch {
            while (isActive) {
                delay(flushIntervalMs)
                synchronized(buffer) {
                    if (buffer.isNotEmpty()) {
                        flushBuffer()
                    }
                }
            }
        }
    }

    override fun writeLog(logType: LogxType, tag: String, message: String) {
        if (!config.isDebugSave || !isActive) return
        
        val logEntry = LogEntry(logType, tag, message)
        scope.launch {
            logChannel.send(logEntry)
        }
    }

    private suspend fun flushBuffer() = withContext(Dispatchers.IO) {
        if (buffer.isEmpty()) return@withContext
        
        try {
            val logFile = getLogFile()
            val logs = buffer.joinToString("\n") { formatLogEntry(it) } + "\n"
            
            logFile.appendText(logs)
            buffer.clear()
            
        } catch (e: IOException) {
            android.util.Log.e("BufferedAsyncLogFileWriter", "Failed to flush buffer", e)
        }
    }

    private fun getLogFile(): File {
        val fileName = "logx_${fileNameFormat.format(Date())}.log"
        val logFile = File(config.saveFilePath, fileName)
        logFile.parentFile?.mkdirs()
        return logFile
    }

    private fun formatLogEntry(logEntry: LogEntry): String {
        return "${logEntry.timestamp} [${logEntry.logType.logTypeString}] ${config.appName} [${logEntry.tag}] : ${logEntry.message}"
    }

    override fun cleanup() {
        scope.launch {
            isActive = false
            
            // 남은 버퍼 플러시
            synchronized(buffer) {
                if (buffer.isNotEmpty()) {
                    flushBuffer()
                }
            }
            
            logChannel.close()
            processingJob.cancel()
            flushJob.cancel()
            scope.cancel()
        }
    }

    companion object {
        private fun currentTimestamp(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        }
    }
}