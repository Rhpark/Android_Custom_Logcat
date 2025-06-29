package kr.open.library.logcat.internal.formatter.base

import kr.open.library.logcat.moel.LogxType

/**
 * 포맷팅된 로그 데이터
 */
data class LogxFormattedData(
    val tag: String,
    val message: String,
    val logType: LogxType
)