package kr.open.library.logcat.data

import android.util.Log
import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.domain.LogxStackTrace
import kr.open.library.logcat.filter.DefaultLogFilter
import kr.open.library.logcat.filter.LogFilter
import kr.open.library.logcat.formatter.*
import kr.open.library.logcat.vo.LogxType
import kr.open.library.logcat.writer.LogFileWriter
import kr.open.library.logcat.writer.LogFileWriterFactory

/**
 * 리팩토링된 LogxWriter
 * SRP 원칙을 준수하여 각 책임을 별도의 클래스로 분리
 * - 포맷팅: LogFormatter 구현체들이 담당
 * - 필터링: LogFilter가 담당
 * - 파일 저장: LogFileWriter가 담당
 */
internal class LogxWriter(private var config: LogxConfig) {

    private val stackTrace = LogxStackTrace()
    private var logFilter: LogFilter = DefaultLogFilter(config)
    private var fileWriter: LogFileWriter = LogFileWriterFactory.create(config)
    
    // 포맷터들
    private var defaultFormatter: DefaultLogFormatter = DefaultLogFormatter(config)
    private var jsonFormatter: JsonLogFormatter = JsonLogFormatter(config)
    private var threadIdFormatter: ThreadIdLogFormatter = ThreadIdLogFormatter(config)
    private var parentFormatter: ParentLogFormatter = ParentLogFormatter(config, stackTrace, false)
    private var parentExtensionsFormatter: ParentLogFormatter = ParentLogFormatter(config, stackTrace, true)

    /**
     * 설정 업데이트 시 모든 의존성 재생성
     */
    fun updateConfig(newConfig: LogxConfig) {
        config = newConfig
        logFilter = DefaultLogFilter(config)
        fileWriter.cleanup()
        fileWriter = LogFileWriterFactory.create(config)
        
        // 포맷터들 재생성
        defaultFormatter = DefaultLogFormatter(config)
        jsonFormatter = JsonLogFormatter(config)
        threadIdFormatter = ThreadIdLogFormatter(config)
        parentFormatter = ParentLogFormatter(config, stackTrace, false)
        parentExtensionsFormatter = ParentLogFormatter(config, stackTrace, true)
    }

    /**
     * 기본 로그 작성 (Extension 함수용)
     */
    fun writeExtensions(tag: String, msg: Any?, type: LogxType) {
        if (!shouldLog(type)) return
        
        try {
            val stackInfo = getExtensionsStackInfo(tag) ?: return
            writeLogWithFormatter(tag, msg, type, stackInfo, defaultFormatter)
        } catch (e: Exception) {
            Log.e("LogxWriter", "Failed to write extensions log: ${e.message}", e)
        }
    }

    /**
     * 기본 로그 작성
     */
    fun write(tag: String, msg: Any?, type: LogxType) {
        if (!shouldLog(type)) return
        
        try {
            val stackInfo = getNormalStackInfo(tag) ?: return
            writeLogWithFormatter(tag, msg, type, stackInfo, defaultFormatter)
        } catch (e: Exception) {
            Log.e("LogxWriter", "Failed to write log: ${e.message}", e)
        }
    }

    /**
     * 스레드 ID 포함 로그 작성
     */
    fun writeThreadId(tag: String, msg: Any?) {
        if (!shouldLog(LogxType.THREAD_ID)) return
        
        try {
            val stackInfo = getNormalStackInfo(tag) ?: return
            writeLogWithFormatter(tag, msg, LogxType.THREAD_ID, stackInfo, threadIdFormatter)
        } catch (e: Exception) {
            Log.e("LogxWriter", "Failed to write thread ID log: ${e.message}", e)
        }
    }

    /**
     * 부모 메서드 정보 포함 로그 작성
     */
    fun writeParent(tag: String, msg: Any?) {
        if (!shouldLog(LogxType.PARENT)) return
        
        try {
            val stackInfo = getNormalStackInfo(tag) ?: return
            writeParentLog(tag, msg, stackInfo, parentFormatter)
        } catch (e: Exception) {
            Log.e("LogxWriter", "Failed to write parent log: ${e.message}", e)
        }
    }

    /**
     * 부모 메서드 정보 포함 로그 작성 (Extension용)
     */
    fun writeExtensionsParent(tag: String, msg: Any?) {
        if (!shouldLog(LogxType.PARENT)) return
        
        try {
            val stackInfo = getExtensionsStackInfo(tag) ?: return
            writeParentLog(tag, msg, stackInfo, parentExtensionsFormatter)
        } catch (e: Exception) {
            Log.e("LogxWriter", "Failed to write extensions parent log: ${e.message}", e)
        }
    }

    /**
     * JSON 로그 작성 (Extension용)
     */
    fun writeJsonExtensions(tag: String, msg: String) {
        if (!shouldLog(LogxType.JSON)) return
        
        try {
            val stackInfo = getExtensionsStackInfo(tag) ?: return
            writeJsonLog(tag, msg, jsonFormatter)
        } catch (e: Exception) {
            Log.e("LogxWriter", "Failed to write JSON extensions log: ${e.message}", e)
        }
    }

    /**
     * JSON 로그 작성
     */
    fun writeJson(tag: String, msg: String) {
        if (!shouldLog(LogxType.JSON)) return
        
        try {
            val stackInfo = getNormalStackInfo(tag) ?: return
            writeJsonLog(tag, msg, jsonFormatter)
        } catch (e: Exception) {
            Log.e("LogxWriter", "Failed to write JSON log: ${e.message}", e)
        }
    }

    private fun writeLogWithFormatter(
        tag: String, 
        msg: Any?, 
        type: LogxType, 
        stackInfo: String, 
        formatter: LogFormatter
    ) {
        val formatted = formatter.format(tag, msg, type, stackInfo) ?: return
        outputLog(formatted)
        saveToFile(formatted)
    }

    private fun writeParentLog(tag: String, msg: Any?, stackInfo: String, formatter: ParentLogFormatter) {
        // 부모 정보를 먼저 출력
        formatter.formatParentInfo(tag)?.let { parentInfo ->
            outputLog(parentInfo)
            saveToFile(parentInfo)
        }
        
        // 실제 메시지 출력
        formatter.format(tag, msg, LogxType.PARENT, stackInfo)?.let { mainLog ->
            outputLog(mainLog)
            saveToFile(mainLog)
        }
    }

    private fun writeJsonLog(tag: String, msg: String, formatter: JsonLogFormatter) {
        // JSON 시작 마커
        val startMarker = formatter.format(tag, "=========JSON_START========", LogxType.JSON) ?: return
        outputLog(startMarker)
        saveToFile(startMarker)
        
        // JSON 내용
        val jsonContent = formatter.format(tag, msg, LogxType.JSON) ?: return
        outputLog(jsonContent)
        saveToFile(jsonContent)
        
        // JSON 종료 마커
        val endMarker = formatter.format(tag, "=========JSON_END==========", LogxType.JSON) ?: return
        outputLog(endMarker)
        saveToFile(endMarker)
    }

    private fun outputLog(formattedLog: FormattedLog) {
        when (formattedLog.logType) {
            LogxType.VERBOSE -> Log.v(formattedLog.tag, formattedLog.message)
            LogxType.INFO -> Log.i(formattedLog.tag, formattedLog.message)
            LogxType.JSON -> Log.i(formattedLog.tag, formattedLog.message)
            LogxType.DEBUG -> Log.d(formattedLog.tag, formattedLog.message)
            LogxType.THREAD_ID -> Log.d(formattedLog.tag, formattedLog.message)
            LogxType.PARENT -> Log.d(formattedLog.tag, formattedLog.message)
            LogxType.WARN -> Log.w(formattedLog.tag, formattedLog.message)
            LogxType.ERROR -> Log.e(formattedLog.tag, formattedLog.message)
        }
    }

    private fun saveToFile(formattedLog: FormattedLog) {
        try {
            fileWriter.writeLog(formattedLog.logType, formattedLog.tag, formattedLog.message)
        } catch (e: Exception) {
            Log.e("LogxWriter", "Failed to save log to file: ${e.message}", e)
        }
    }

    private fun getNormalStackInfo(tag: String): String? {
        val stackInfo = stackTrace.getStackTrace()
        val fileName = stackInfo.fileName.split(".")[0]
        
        return if (logFilter.shouldLog(tag, fileName)) {
            stackInfo.getMsgFrontNormal()
        } else {
            null
        }
    }

    private fun getExtensionsStackInfo(tag: String): String? {
        val stackInfo = stackTrace.getExtensionsStackTrace()
        val fileName = stackInfo.fileName.split(".")[0]
        
        return if (logFilter.shouldLog(tag, fileName)) {
            stackInfo.getMsgFrontNormal()
        } else {
            null
        }
    }

    private fun shouldLog(logType: LogxType): Boolean {
        return config.isDebug && config.debugLogTypeList.contains(logType)
    }

    /**
     * 리소스 정리
     */
    fun cleanup() {
        fileWriter.cleanup()
    }
}
