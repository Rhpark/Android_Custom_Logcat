package kr.open.library.logcat.formatter

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.formatter.base.LogxBaseFormatter
import kr.open.library.logcat.formatter.base.LogxFormatterImp
import kr.open.library.logcat.repo.vo.LogxType

/**
 * logcat 기본 포맷터 부분 설정 및 반환
 */
class DefaultLogFormatter(private val config: LogxConfig) :
    LogxBaseFormatter(config), LogxFormatterImp {

    override fun shouldLogType(logType: LogxType): Boolean = true

    override fun formatMessage(message: Any?, stackInfo: String): String = "$stackInfo${message ?: ""}"
}