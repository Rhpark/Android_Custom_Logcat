package kr.open.library.logcat

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.config.LogxConfigManager
import kr.open.library.logcat.data.LogxWriter
import kr.open.library.logcat.vo.LogxType

/**
 * Logx 라이브러리의 메인 클래스 (리팩토링됨)
 * SRP 원칙을 준수하여 설정 관리를 LogxConfigManager로 분리
 *
 * 사용법:
 * Logx.d(), Logx.d(msg), Logx.d(tag, msg)
 *
 * 로그캣 출력 형식:
 * D/AppName ["tag"] : (FileName:NumberLine).Method - msg
 *
 * 특수 기능:
 * - Logx.p() : 부모 메서드 호출 정보 표시
 * - Logx.j() : JSON 포맷팅
 * - Logx.t() : 현재 스레드 ID 표시
 */
object Logx : ILogx {

    private const val DEFAULT_TAG = ""

    private val configManager = LogxConfigManager()
    private val logWriter = LogxWriter(configManager.config)

    init {
        // 설정 변경 시 LogxWriter에 자동 전파
        configManager.addConfigChangeListener(object : LogxConfigManager.ConfigChangeListener {
            override fun onConfigChanged(newConfig: LogxConfig) {
                logWriter.updateConfig(newConfig)
            }
        })
    }

    // 설정 관리 메서드들 - ConfigManager에 위임
    override fun setDebugMode(isDebug: Boolean) {
        configManager.setDebugMode(isDebug)
    }

    override fun setDebugFilter(isFilter: Boolean) {
        configManager.setDebugFilter(isFilter)
    }

    override fun setSaveToFile(isSave: Boolean) {
        configManager.setSaveToFile(isSave)
    }

    override fun setFilePath(path: String) {
        configManager.setFilePath(path)
    }

    override fun setAppName(name: String) {
        configManager.setAppName(name)
    }

    override fun setDebugLogTypeList(types: List<LogxType>) {
        configManager.setDebugLogTypeList(types)
    }

    override fun setDebugFilterList(tags: List<String>) {
        configManager.setDebugFilterList(tags)
    }

    /**
     * 설정을 한 번에 업데이트
     */
    fun updateConfig(newConfig: LogxConfig) {
        configManager.updateConfig(newConfig)
    }
    
    /**
     * DSL을 사용한 설정 업데이트
     * 
     * Usage:
     * ```
     * Logx.configure {
     *     debugMode = true
     *     appName = "MyApp"
     *     fileConfig {
     *         saveToFile = true
     *         filePath = "/custom/path"
     *     }
     *     logTypes {
     *         +LogxType.DEBUG
     *         +LogxType.ERROR
     *     }
     * }
     * ```
     */
    fun configure(block: kr.open.library.logcat.config.LogxConfigBuilder.() -> Unit) {
        val newConfig = kr.open.library.logcat.config.logxConfig(block)
        updateConfig(newConfig)
    }

    // 기본 로깅 메서드들 - 보일러플레이트 제거 및 성능 개선
    @JvmOverloads
    inline fun log(type: LogxType, tag: String = DEFAULT_TAG, msg: Any? = "") = 
        logWriter.write(tag, msg, type)
    
    @JvmOverloads  
    inline fun logExt(type: LogxType, tag: String = DEFAULT_TAG, msg: Any? = "") =
        logWriter.writeExtensions(tag, msg, type)

    // 기존 API 호환성 유지 (inline으로 성능 개선)
    override inline fun v() = log(LogxType.VERBOSE)
    override inline fun v(msg: Any?) = log(LogxType.VERBOSE, msg = msg)
    override inline fun v(tag: String, msg: Any?) = log(LogxType.VERBOSE, tag, msg)
    inline fun v1(msg: Any?) = logExt(LogxType.VERBOSE, msg = msg)
    inline fun v1(tag: String, msg: Any?) = logExt(LogxType.VERBOSE, tag, msg)

    override inline fun d() = log(LogxType.DEBUG)
    override inline fun d(msg: Any?) = log(LogxType.DEBUG, msg = msg)
    override inline fun d(tag: String, msg: Any?) = log(LogxType.DEBUG, tag, msg)
    inline fun d1(msg: Any?) = logExt(LogxType.DEBUG, msg = msg)
    inline fun d1(tag: String, msg: Any?) = logExt(LogxType.DEBUG, tag, msg)

    override inline fun i() = log(LogxType.INFO)
    override inline fun i(msg: Any?) = log(LogxType.INFO, msg = msg)
    override inline fun i(tag: String, msg: Any?) = log(LogxType.INFO, tag, msg)
    inline fun i1(msg: Any?) = logExt(LogxType.INFO, msg = msg)
    inline fun i1(tag: String, msg: Any?) = logExt(LogxType.INFO, tag, msg)

    override inline fun w() = log(LogxType.WARN)
    override inline fun w(msg: Any?) = log(LogxType.WARN, msg = msg)
    override inline fun w(tag: String, msg: Any?) = log(LogxType.WARN, tag, msg)
    inline fun w1(msg: Any?) = logExt(LogxType.WARN, msg = msg)
    inline fun w1(tag: String, msg: Any?) = logExt(LogxType.WARN, tag, msg)

    override inline fun e() = log(LogxType.ERROR)
    override inline fun e(msg: Any?) = log(LogxType.ERROR, msg = msg)
    override inline fun e(tag: String, msg: Any?) = log(LogxType.ERROR, tag, msg)
    inline fun e1(msg: Any?) = logExt(LogxType.ERROR, msg = msg)
    inline fun e1(tag: String, msg: Any?) = logExt(LogxType.ERROR, tag, msg)

    // 확장 기능들 - 간소화 및 인라인 최적화
    @JvmOverloads
    inline fun logParent(tag: String = DEFAULT_TAG, msg: Any? = "") =
        logWriter.writeParent(tag, msg)
    
    @JvmOverloads  
    inline fun logParentExt(tag: String = DEFAULT_TAG, msg: Any? = "") =
        logWriter.writeExtensionsParent(tag, msg)
    
    @JvmOverloads
    inline fun logThread(tag: String = DEFAULT_TAG, msg: Any? = "") =
        logWriter.writeThreadId(tag, msg)
        
    @JvmOverloads
    inline fun logJson(tag: String = DEFAULT_TAG, msg: String) =
        logWriter.writeJson(tag, msg)
        
    @JvmOverloads
    inline fun logJsonExt(tag: String = DEFAULT_TAG, msg: String) =
        logWriter.writeJsonExtensions(tag, msg)

    // 기존 API 호환성 유지
    override inline fun p() = logParent()
    override inline fun p(msg: Any?) = logParent(msg = msg)
    override inline fun p(tag: String, msg: Any?) = logParent(tag, msg)
    inline fun p1(msg: Any?) = logParentExt(msg = msg)
    inline fun p1(tag: String, msg: Any?) = logParentExt(tag, msg)

    override inline fun t() = logThread()
    override inline fun t(msg: Any?) = logThread(msg = msg)
    override inline fun t(tag: String, msg: Any?) = logThread(tag, msg)

    override inline fun j(msg: String) = logJson(msg = msg)
    override inline fun j(tag: String, msg: String) = logJson(tag, msg)
    inline fun j1(msg: String) = logJsonExt(msg = msg)
    inline fun j1(tag: String, msg: String) = logJsonExt(tag, msg)

    // 레거시 호환성을 위한 getter 메서드들
    fun getDebugMode(): Boolean = configManager.config.isDebug
    fun getDebugFilter(): Boolean = configManager.config.isDebugFilter
    fun getSaveToFile(): Boolean = configManager.config.isDebugSave
    fun getFilePath(): String = configManager.config.saveFilePath
    fun getAppName(): String = configManager.config.appName
    fun getDebugFilterList(): Set<String> = configManager.config.debugFilterList
    fun getDebugLogTypeList(): List<LogxType> = configManager.config.debugLogTypeList

    /**
     * 리소스 정리 (앱 종료 시 호출 권장)
     */
    fun cleanup() {
        logWriter.cleanup()
    }
}
