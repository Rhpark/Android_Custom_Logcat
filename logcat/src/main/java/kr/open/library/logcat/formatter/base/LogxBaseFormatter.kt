package kr.open.library.logcat.formatter.base

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.repo.vo.LogxType

/**
 * logcat 기본 포맷터 부분 설정 및 반환 추상 클래스
 * → Parent TAG : appName [TAG] [PARENT] : formatMessage()
 * → ThreadId TAG : appName [TAG] [T_ID] : formatMessage()
 * → Json TAG : appName [TAG] [JSON] : formatMessage()
 * → Else : appName [TAG] : formatMessage()
 */
abstract class LogxBaseFormatter(private val config: LogxConfig) : LogxFormatterImp {

    final override fun format(tag: String, message: Any?, logType: LogxType, stackInfo: String): LogxFormattedData? =
        if (!shouldFormat(logType)) null
        else LogxFormattedData(createFormattedTag(tag, logType), formatMessage(message, stackInfo), logType)


    protected open fun shouldFormat(logType: LogxType): Boolean =
        config.isDebug && shouldLogType(logType) && config.debugLogTypeList.contains(logType)

    protected fun createFormattedTag(tag: String, logType: LogxType): String =
        "${config.appName} [$tag]${getTypeString(logType)}"

    protected fun getTypeString(logType: LogxType): String = when (logType) {
        LogxType.THREAD_ID -> " [T_ID] :"
        LogxType.PARENT -> " [PARENT] :"
        LogxType.JSON -> " [JSON] :"
        else -> " :"
    }

    protected abstract fun shouldLogType(logType: LogxType): Boolean
    protected abstract fun formatMessage(message: Any?, stackInfo: String): String
}