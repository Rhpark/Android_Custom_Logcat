package kr.open.library.logcat.formatter

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.vo.LogxType

/**
 * 메모리 효율적인 기본 로그 포맷터
 * SRP: 기본 로그 포맷팅에만 집중
 * 메모리 최적화: StringBuilder 풀링, String 재사용
 */
class DefaultLogFormatter(private val config: LogxConfig) : EfficientLogFormatter() {
    
    // 자주 사용되는 문자열들을 미리 생성하여 재사용 (메모리 최적화)
    private val appNameWithBracket = "${config.appName} ["
    private val typeStrings = mapOf(
        LogxType.THREAD_ID to " [T_ID] :",
        LogxType.PARENT to " [PARENT] :",
        LogxType.JSON to " [JSON] :",
        LogxType.VERBOSE to " :",
        LogxType.DEBUG to " :",
        LogxType.INFO to " :",
        LogxType.WARN to " :",
        LogxType.ERROR to " :"
    )
    
    override fun format(tag: String, message: Any?, logType: LogxType, stackInfo: String): FormattedLog? {
        if (!shouldFormat(logType)) return null
        
        return buildFormattedMessage(tag, message, logType, stackInfo) { sb ->
            // StringBuilder를 사용한 효율적인 문자열 구성
            sb.append(appNameWithBracket)
                .append(tag)
                .append(']')
                .append(typeStrings[logType] ?: " :")
                .append(' ')
                .append(stackInfo)
                .append(message ?: "")
        }
    }
    
    /**
     * 고성능 버전: StringBuilder를 직접 받아서 사용
     */
    override fun formatTo(
        stringBuilder: StringBuilder,
        tag: String,
        message: Any?,
        logType: LogxType,
        stackInfo: String
    ): Boolean {
        if (!shouldFormat(logType)) return false
        
        stringBuilder.clear()
            .append(appNameWithBracket)
            .append(tag)
            .append(']')
            .append(typeStrings[logType] ?: " :")
            .append(' ')
            .append(stackInfo)
            .append(message ?: "")
            
        return true
    }
    
    private inline fun shouldFormat(logType: LogxType): Boolean {
        return config.isDebug && config.debugLogTypeList.contains(logType)
    }
}