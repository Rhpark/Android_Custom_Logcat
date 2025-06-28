package kr.open.library.logcat.formatter

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.vo.LogxType

/**
 * 메모리 효율적인 기본 로그 포맷터
 * SRP: 기본 로그 포맷팅에만 집중
 * 메모리 최적화: StringBuilder 풀링, String 재사용
 */
class DefaultLogFormatter(private val config: LogxConfig) : LogFormatter {
    
    
    override fun format(tag: String, message: Any?, logType: LogxType, stackInfo: String): FormattedLog? {
        if (!shouldFormat(logType)) return null
        
        val formattedTag = createFormattedTag(tag, logType)
        val formattedMessage = createFormattedMessage(message, stackInfo)
        
        return FormattedLog(formattedTag, formattedMessage, logType)
    }
    
    private fun createFormattedTag(tag: String, logType: LogxType): String {
        val typeString = getTypeString(logType)
        return "${config.appName} [$tag]$typeString"
    }
    
    private fun createFormattedMessage(message: Any?, stackInfo: String): String {
        return "$stackInfo${message ?: ""}"
    }
    
    private fun getTypeString(logType: LogxType): String = when(logType) {
        LogxType.THREAD_ID -> " [T_ID] :"
        LogxType.PARENT -> " [PARENT] :"
        LogxType.JSON -> " [JSON] :"
        else -> " :"
    }
    
    private inline fun shouldFormat(logType: LogxType): Boolean {
        return config.isDebug && config.debugLogTypeList.contains(logType)
    }
}