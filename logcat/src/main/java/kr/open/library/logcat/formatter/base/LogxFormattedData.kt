package kr.open.library.logcat.formatter.base

import kr.open.library.logcat.repo.vo.LogxType

/**
 * 포맷팅된 로그 데이터
 */
data class LogxFormattedData(
    val tag: String,
    val message: String,
    val logType: LogxType
)