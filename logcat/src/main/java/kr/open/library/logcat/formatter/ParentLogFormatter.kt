package kr.open.library.logcat.formatter

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.domain.LogxStackTrace
import kr.open.library.logcat.vo.LogxType

/**
 * 부모 메서드 정보 포함 로그 포맷터
 * SRP: 부모 메서드 정보 포맷팅에만 집중
 */
class ParentLogFormatter(
    private val config: LogxConfig,
    private val stackTrace: LogxStackTrace,
    private val isExtensions: Boolean = false
) : LogFormatter {
    
    override fun format(tag: String, message: Any?, logType: LogxType, stackInfo: String): FormattedLog? {
        if (!shouldFormat(logType)) return null
        
        val parentInfo = getParentInfo()
        val formattedTag = createFormattedTag(tag)
        
        // 부모 정보를 먼저 출력하고, 실제 메시지를 그 다음에 출력
        return FormattedLog(
            tag = formattedTag,
            message = "┖${stackInfo}${message ?: ""}",
            logType = LogxType.PARENT
        )
    }
    
    /**
     * 부모 메서드 정보를 먼저 로그로 출력하기 위한 메서드
     */
    fun formatParentInfo(tag: String): FormattedLog? {
        if (!shouldFormat(LogxType.PARENT)) return null
        
        val parentInfo = getParentInfo()
        val formattedTag = createFormattedTag(tag)
        
        return FormattedLog(
            tag = formattedTag,
            message = "┎${parentInfo.getMsgFrontParent()}",
            logType = LogxType.PARENT
        )
    }
    
    private fun shouldFormat(logType: LogxType): Boolean {
        return config.isDebug && 
               logType == LogxType.PARENT &&
               config.debugLogTypeList.contains(LogxType.PARENT)
    }
    
    private fun createFormattedTag(tag: String): String {
        return "${config.appName} [$tag] [PARENT] :"
    }
    
    private fun getParentInfo() = if (isExtensions) {
        stackTrace.getParentExtensionsStackTrace()
    } else {
        stackTrace.getParentStackTrace()
    }
}