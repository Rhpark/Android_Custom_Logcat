package kr.open.library.logcat.config

import kr.open.library.logcat.repo.vo.LogxType


/**
 * 파일 설정 빌더
 */
@LogxConfigDsl
class LogxFileConfigBuilder {
    var saveToFile: Boolean = false
    var filePath: String = LogxPathUtils.getDefaultLogPath()
}

/**
 * 로그 타입 설정 빌더
 */
@LogxConfigDsl
class LogxTypeConfigBuilder {
    private val _types = mutableSetOf<LogxType>()

    val types: List<LogxType>
        get() = _types.toList()

    operator fun LogxType.unaryPlus() {
        _types.add(this)
    }

    operator fun LogxType.unaryMinus() {
        _types.remove(this)
    }

    fun all() {
        _types.addAll(LogxType.values())
    }

    fun basic() {
        _types.addAll(listOf(
            LogxType.VERBOSE, LogxType.DEBUG, LogxType.INFO,
            LogxType.WARN, LogxType.ERROR
        ))
    }

    fun extended() {
        _types.addAll(listOf(
            LogxType.PARENT, LogxType.JSON, LogxType.THREAD_ID
        ))
    }

    init {
        _types.addAll(LogxType.values())
    }
}

/**
 * 필터 설정 빌더
 */
@LogxConfigDsl
class LogxFilterConfigBuilder {
    private val _filters = mutableSetOf<String>()

    val filters: Set<String>
        get() = _filters.toSet()

    operator fun String.unaryPlus() {
        _filters.add(this)
    }

    operator fun String.unaryMinus() {
        _filters.remove(this)
    }

    fun addAll(vararg filters: String) {
        _filters.addAll(filters)
    }

    fun clear() {
        _filters.clear()
    }
}