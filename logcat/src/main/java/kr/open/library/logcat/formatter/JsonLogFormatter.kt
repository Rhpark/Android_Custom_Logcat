package kr.open.library.logcat.formatter

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.vo.LogxType

/**
 * JSON 전용 로그 포맷터
 * SRP: JSON 포맷팅에만 집중
 */
class JsonLogFormatter(private val config: LogxConfig) : LogFormatter {
    
    override fun format(tag: String, message: Any?, logType: LogxType, stackInfo: String): FormattedLog? {
        if (!shouldFormat(logType)) return null
        
        val formattedTag = createFormattedTag(tag)
        val formattedMessage = formatJsonMessage(message.toString())
        
        return FormattedLog(formattedTag, formattedMessage, LogxType.JSON)
    }
    
    private fun shouldFormat(logType: LogxType): Boolean {
        return config.isDebug && 
               logType == LogxType.JSON &&
               config.debugLogTypeList.contains(LogxType.JSON)
    }
    
    private fun createFormattedTag(tag: String): String {
        return "${config.appName} [$tag] [JSON] :"
    }
    
    /**
     * JSON 문자열을 보기 좋게 포맷팅
     */
    private fun formatJsonMessage(jsonString: String): String {
        val result = StringBuilder()
        var indentLevel = 0
        var inQuotes = false
        
        for (char in jsonString) {
            when (char) {
                '{', '[' -> {
                    result.append(char)
                    if (!inQuotes) {
                        result.append("\n")
                        indentLevel++
                        result.append("  ".repeat(indentLevel))
                    }
                }
                '}', ']' -> {
                    if (!inQuotes) {
                        result.append("\n")
                        indentLevel = maxOf(0, indentLevel - 1)
                        result.append("  ".repeat(indentLevel))
                    }
                    result.append(char)
                }
                ',' -> {
                    result.append(char)
                    if (!inQuotes) {
                        result.append("\n")
                        result.append("  ".repeat(indentLevel))
                    }
                }
                '"' -> {
                    result.append(char)
                    if (result.lastOrNull() != '\\') {
                        inQuotes = !inQuotes
                    }
                }
                else -> result.append(char)
            }
        }
        
        return result.toString()
    }
}