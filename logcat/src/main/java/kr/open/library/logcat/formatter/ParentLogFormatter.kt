package kr.open.library.logcat.formatter

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.domain.LogxStackTrace
import kr.open.library.logcat.formatter.base.LogxFormattedData
import kr.open.library.logcat.formatter.base.LogxBaseFormatter
import kr.open.library.logcat.formatter.base.LogxFormatterImp
import kr.open.library.logcat.repo.vo.LogxType

/**
 * logcat PARENT 전용 포맷터 부분 설정 및 반환
 */
class ParentLogFormatter(
    private val config: LogxConfig,
    private val stackTrace: LogxStackTrace,
    private val isExtensions: Boolean = false
) : LogxBaseFormatter(config), LogxFormatterImp{

    override fun shouldLogType(logType: LogxType): Boolean = logType == LogxType.PARENT

    override fun formatMessage(message: Any?, stackInfo: String): String = "┖${stackInfo}${message ?: ""}"
    
    /**
     * 부모 메서드 정보를 먼저 로그로 출력하기 위한 메서드
     */
    fun formatParentInfo(tag: String): LogxFormattedData? {
        if (!shouldFormat(LogxType.PARENT)) return null
        
        val parentInfo = getParentInfo()
        val formattedTag = createFormattedTag(tag, LogxType.PARENT)
        
        return LogxFormattedData(
            tag = formattedTag,
            message = "┎${parentInfo.getMsgFrontParent()}",
            logType = LogxType.PARENT
        )
    }
    
    private fun getParentInfo() = if (isExtensions) {
        stackTrace.getParentExtensionsStackTrace()
    } else {
        stackTrace.getParentStackTrace()
    }
}