package kr.open.library.logcat.internal.formatter

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.internal.formatter.base.LogxBaseFormatter
import kr.open.library.logcat.internal.formatter.base.LogxFormatterImp
import kr.open.library.logcat.moel.LogxType

/**
 * logcat 기본 포맷터 부분 설정 및 반환
 */
class DefaultLogFormatter(private val config: LogxConfig) :
    LogxBaseFormatter(config), LogxFormatterImp {

    override fun isIncludeLogType(logType: LogxType): Boolean = (
        logType == LogxType.VERBOSE || logType == LogxType.DEBUG ||
        logType == LogxType.INFO || logType == LogxType.WARN || logType == LogxType.ERROR
    )

    override fun getTagSuffix(): String = " :"

    override fun formatMessage(message: Any?, stackInfo: String): String = "$stackInfo${message ?: ""}"
}