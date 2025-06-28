package kr.open.library.logcat.formatter

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.vo.LogxType

/**
 * 스레드 ID 포함 로그 포맷터
 * SRP: 스레드 ID 포맷팅에만 집중
 */
class ThreadIdLogFormatter(private val config: LogxConfig) : LogFormatter {
    
    override fun format(tag: String, message: Any?, logType: LogxType, stackInfo: String): FormattedLog? {
        if (!shouldFormat(logType)) return null
        
        val formattedTag = createFormattedTag(tag)
        val formattedMessage = createThreadIdMessage(message, stackInfo)
        
        return FormattedLog(formattedTag, formattedMessage, LogxType.THREAD_ID)
    }
    
    private fun shouldFormat(logType: LogxType): Boolean {
        return config.isDebug && 
               logType == LogxType.THREAD_ID &&
               config.debugLogTypeList.contains(LogxType.THREAD_ID)
    }
    
    private fun createFormattedTag(tag: String): String {
        return "${config.appName} [$tag] [T_ID] :"
    }
    
    private fun createThreadIdMessage(message: Any?, stackInfo: String): String {
        val threadId = Thread.currentThread().id
        return "[${threadId}]${stackInfo}${message ?: ""}"
    }
}