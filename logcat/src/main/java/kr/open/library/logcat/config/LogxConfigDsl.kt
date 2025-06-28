package kr.open.library.logcat.config

import android.os.Environment
import kr.open.library.logcat.vo.LogxType

/**
 * Kotlin DSL for Logx Configuration
 * 더 직관적이고 타입 안전한 설정 방법 제공
 * 
 * Usage:
 * ```
 * val config = logxConfig {
 *     debugMode = true
 *     appName = "MyApp"
 *     fileConfig {
 *         saveToFile = true
 *         filePath = "/custom/path"
 *     }
 *     logTypes {
 *         +LogxType.DEBUG
 *         +LogxType.ERROR
 *         -LogxType.VERBOSE // 제외
 *     }
 *     filters {
 *         +"MainActivity"
 *         +"Repository"
 *     }
 * }
 * ```
 */

@DslMarker
annotation class LogxConfigDsl

/**
 * DSL 진입점
 */
fun logxConfig(block: LogxConfigBuilder.() -> Unit): LogxConfig {
    return LogxConfigBuilder().apply(block).build()
}

/**
 * 향상된 설정 빌더 (DSL 지원)
 */
@LogxConfigDsl
class LogxConfigBuilder {
    var debugMode: Boolean = true
    var debugFilter: Boolean = false
    var appName: String = "RhPark"
    
    private var fileConfigBlock: FileConfigBuilder.() -> Unit = {}
    private var logTypeConfigBlock: LogTypeConfigBuilder.() -> Unit = {}
    private var filterConfigBlock: FilterConfigBuilder.() -> Unit = {}
    
    /**
     * 파일 저장 설정
     */
    fun fileConfig(block: FileConfigBuilder.() -> Unit) {
        fileConfigBlock = block
    }
    
    /**
     * 로그 타입 설정
     */
    fun logTypes(block: LogTypeConfigBuilder.() -> Unit) {
        logTypeConfigBlock = block
    }
    
    /**
     * 필터 설정
     */
    fun filters(block: FilterConfigBuilder.() -> Unit) {
        filterConfigBlock = block
    }
    
    internal fun build(): LogxConfig {
        val fileConfig = FileConfigBuilder().apply(fileConfigBlock)
        val logTypeConfig = LogTypeConfigBuilder().apply(logTypeConfigBlock)
        val filterConfig = FilterConfigBuilder().apply(filterConfigBlock)
        
        return LogxConfig(
            isDebug = debugMode,
            isDebugFilter = debugFilter,
            isDebugSave = fileConfig.saveToFile,
            saveFilePath = fileConfig.filePath,
            appName = appName,
            debugFilterList = filterConfig.filters,
            debugLogTypeList = logTypeConfig.types
        )
    }
}

/**
 * 파일 설정 빌더
 */
@LogxConfigDsl
class FileConfigBuilder {
    var saveToFile: Boolean = false
    var filePath: String = Environment.getExternalStorageDirectory().path
}

/**
 * 로그 타입 설정 빌더
 */
@LogxConfigDsl
class LogTypeConfigBuilder {
    private val _types = mutableSetOf<LogxType>()
    
    val types: List<LogxType>
        get() = _types.toList()
    
    /**
     * 로그 타입 추가
     */
    operator fun LogxType.unaryPlus() {
        _types.add(this)
    }
    
    /**
     * 로그 타입 제거
     */
    operator fun LogxType.unaryMinus() {
        _types.remove(this)
    }
    
    /**
     * 모든 타입 추가
     */
    fun all() {
        _types.addAll(LogxType.values())
    }
    
    /**
     * 기본 타입들 추가 (VERBOSE, DEBUG, INFO, WARN, ERROR)
     */
    fun basic() {
        _types.addAll(listOf(
            LogxType.VERBOSE, LogxType.DEBUG, LogxType.INFO, 
            LogxType.WARN, LogxType.ERROR
        ))
    }
    
    /**
     * 확장 타입들 추가 (PARENT, JSON, THREAD_ID)
     */
    fun extended() {
        _types.addAll(listOf(
            LogxType.PARENT, LogxType.JSON, LogxType.THREAD_ID
        ))
    }
    
    init {
        // 기본값: 모든 타입
        _types.addAll(LogxType.values())
    }
}

/**
 * 필터 설정 빌더
 */
@LogxConfigDsl
class FilterConfigBuilder {
    private val _filters = mutableSetOf<String>()
    
    val filters: Set<String>
        get() = _filters.toSet()
    
    /**
     * 필터 추가
     */
    operator fun String.unaryPlus() {
        _filters.add(this)
    }
    
    /**
     * 필터 제거
     */
    operator fun String.unaryMinus() {
        _filters.remove(this)
    }
    
    /**
     * 여러 필터 한번에 추가
     */
    fun addAll(vararg filters: String) {
        _filters.addAll(filters)
    }
    
    /**
     * 모든 필터 초기화
     */
    fun clear() {
        _filters.clear()
    }
}

/**
 * 편의 함수들
 */

/**
 * 개발용 설정 (모든 로그 활성화)
 */
fun developmentConfig(appName: String = "RhPark"): LogxConfig = logxConfig {
    debugMode = true
    debugFilter = false
    this.appName = appName
    logTypes { all() }
}

/**
 * 프로덕션용 설정 (에러와 경고만)
 */
fun productionConfig(appName: String = "RhPark"): LogxConfig = logxConfig {
    debugMode = true
    debugFilter = true
    this.appName = appName
    logTypes {
        +LogxType.WARN
        +LogxType.ERROR
    }
}

/**
 * 파일 저장용 설정
 */
fun fileLoggingConfig(
    appName: String = "RhPark",
    filePath: String = Environment.getExternalStorageDirectory().path
): LogxConfig = logxConfig {
    debugMode = true
    this.appName = appName
    fileConfig {
        saveToFile = true
        this.filePath = filePath
    }
    logTypes { all() }
}