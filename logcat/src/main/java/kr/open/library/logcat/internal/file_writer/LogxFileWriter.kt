package kr.open.library.logcat.internal.file_writer

import android.util.Log
import kr.open.library.logcat.moel.LogxType
import kr.open.library.logcat.internal.file_writer.base.LogxFileWriterImp
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

/**
 * Logcat Log 파일 저장 구현체
 */
class LogxFileWriter(private val filePath: String) : LogxFileWriterImp {
    
    private val lock = ReentrantReadWriteLock()
    private val dateFormatter = SimpleDateFormat("yy-MM-dd, HH:mm:ss.SSS", Locale.US)
    private val fileNameFormatter = SimpleDateFormat("yy-MM-dd", Locale.US)


    /**
     * 로그 파일 작성 시 발생할 수 있는 예외
     */
    class LogFileWriteException(message: String, cause: Throwable? = null) : Exception(message, cause)

    init {
        createDirectoryIfNeeded()
    }
    
    override fun writeLog(logType: LogxType, tag: String, message: String) {
        lock.write {
            try {
                val timestamp = dateFormatter.format(Date())
                val logFile = createLogFile()
                val logLine = "$timestamp/${logType.logTypeString}/$tag : $message"
                
                writeToFile(logFile, logLine)
                
            } catch (e: Exception) {
                Log.e("ImmediateLogFileWriter", "Failed to write log immediately: ${e.message}", e)
                throw LogFileWriteException("Failed to write log immediately", e)
            }
        }
    }
    
    override fun cleanup() {
        lock.write {
            // 즉시 저장 방식에서는 특별한 정리 작업이 필요하지 않음
            Log.d("ImmediateLogFileWriter", "Cleanup completed")
        }
    }
    
    private fun createDirectoryIfNeeded() {
        val directory = File(filePath)
        if (directory.exists()) return
        
        try {
            if (directory.mkdirs()) {
                Log.d("ImmediateLogFileWriter", "Directory created: $filePath")
            } else {
                Log.e("ImmediateLogFileWriter", "Failed to create directory: $filePath")
                throw LogFileWriteException("Failed to create directory: $filePath")
            }
        } catch (e: Exception) {
            Log.e("ImmediateLogFileWriter", "Exception while creating directory", e)
            throw LogFileWriteException("Exception while creating directory", e)
        }
    }
    
    private fun createLogFile(): File {
        val fileName = "${fileNameFormatter.format(Date())}_Log.txt"
        val logFile = File(filePath, fileName)
        
        if (!logFile.exists()) {
            try {
                if (logFile.createNewFile()) {
                    Log.d("ImmediateLogFileWriter", "Log file created: ${logFile.path}")
                } else {
                    Log.e("ImmediateLogFileWriter", "Failed to create log file: ${logFile.path}")
                    throw LogFileWriteException("Failed to create log file: ${logFile.path}")
                }
            } catch (e: IOException) {
                Log.e("ImmediateLogFileWriter", "IOException creating file: ${logFile.path}", e)
                throw LogFileWriteException("IOException creating file: ${logFile.path}", e)
            }
        }
        
        return logFile
    }
    
    private fun writeToFile(file: File, logLine: String) {
        try {
            BufferedWriter(FileWriter(file, true)).use { writer ->
                writer.write(logLine)
                writer.newLine()
                writer.flush() // 즉시 플러시하여 데이터 손실 방지
            }
        } catch (e: IOException) {
            Log.e("ImmediateLogFileWriter", "Failed to write to file: ${file.path}", e)
            throw LogFileWriteException("Failed to write to file: ${file.path}", e)
        }
    }
}