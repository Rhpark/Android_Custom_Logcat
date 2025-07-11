package kr.open.library.logcat.internal.formatter

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.internal.formatter.base.LogxBaseFormatter
import kr.open.library.logcat.internal.formatter.base.LogxFormatterImp
import kr.open.library.logcat.moel.LogxType

/**
 * logcat JSON 전용 포맷터 부분 설정 및 반환
 */
class JsonLogFormatter(private val config: LogxConfig) :
    LogxBaseFormatter(config), LogxFormatterImp {

    override fun isIncludeLogType(logType: LogxType): Boolean = logType == LogxType.JSON

    override fun getTagSuffix(): String = " [JSON] :"

    /**
     * JSON 문자열을 보기 좋게 포맷팅
     */
    override fun formatMessage(message: Any?, stackInfo: String): String = formatJsonMessage(message.toString())
    

    private fun formatJsonMessage(jsonString: String): String {
        if (!jsonString.trim().startsWith("{") && !jsonString.trim().startsWith("[")) {
            return jsonString
        }

        return jsonString
            .replace(",", ",\n  ")
            .replace("{", "{\n  ")
            .replace("}", "\n}")
            .replace("[", "[\n  ")
            .replace("]", "\n]")
//
//        val result = StringBuilder()
//        var indentLevel = 0
//        var inQuotes = false
//
//        for (char in jsonString) {
//            when (char) {
//                '{', '[' -> {
//                    result.append(char)
//                    if (!inQuotes) {
//                        result.append("\n")
//                        indentLevel++
//                        result.append("  ".repeat(indentLevel))
//                    }
//                }
//                '}', ']' -> {
//                    if (!inQuotes) {
//                        result.append("\n")
//                        indentLevel = maxOf(0, indentLevel - 1)
//                        result.append("  ".repeat(indentLevel))
//                    }
//                    result.append(char)
//                }
//                ',' -> {
//                    result.append(char)
//                    if (!inQuotes) {
//                        result.append("\n")
//                        result.append("  ".repeat(indentLevel))
//                    }
//                }
//                '"' -> {
//                    result.append(char)
//                    if (result.lastOrNull() != '\\') {
//                        inQuotes = !inQuotes
//                    }
//                }
//                else -> result.append(char)
//            }
//        }
//
//        return result.toString()
    }
}