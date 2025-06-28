package kr.open.library.logcat

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.data.LogxWriter
import kr.open.library.logcat.vo.LogxType

/**
 * Logx 라이브러리의 메인 클래스
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

    @Volatile
    private var config = LogxConfig()

    private val logWriter by lazy { LogxWriter(config) }

    // 설정 관리 메서드들
    override fun setDebugMode(isDebug: Boolean) {
        config = config.copy(isDebug = isDebug)
        logWriter.updateConfig(config)
    }

    override fun setDebugFilter(isFilter: Boolean) {
        config = config.copy(isDebugFilter = isFilter)
        logWriter.updateConfig(config)
    }

    override fun setSaveToFile(isSave: Boolean) {
        config = config.copy(isDebugSave = isSave)
        logWriter.updateConfig(config)
    }

    override fun setFilePath(path: String) {
        config = config.copy(saveFilePath = path)
        logWriter.updateConfig(config)
    }

    override fun setAppName(name: String) {
        config = config.copy(appName = name)
        logWriter.updateConfig(config)
    }

    override fun setDebugLogTypeList(types: List<LogxType>) {
        config = config.copy(debugLogTypeList = types.toList())
        logWriter.updateConfig(config)
    }

    override fun setDebugFilterList(tags: List<String>) {
        config = config.copy(debugFilterList = tags.toSet())
        logWriter.updateConfig(config)
    }

    /**
     * 설정을 한 번에 업데이트
     */
    fun updateConfig(newConfig: LogxConfig) {
        config = newConfig
        logWriter.updateConfig(config)
    }

    // 기본 로깅 메서드들 - @JvmStatic 제거하고 인스턴스 메서드로 변경
    override fun v() {
        logWriter.write(DEFAULT_TAG, "", LogxType.VERBOSE)
    }

    override fun v(msg: Any?) {
        logWriter.write(DEFAULT_TAG, msg, LogxType.VERBOSE)
    }

    override fun v(tag: String, msg: Any?) {
        logWriter.write(tag, msg, LogxType.VERBOSE)
    }

    fun v1(msg: Any?) {
        logWriter.writeExtensions(DEFAULT_TAG, msg, LogxType.VERBOSE)
    }

    fun v1(tag: String, msg: Any?) {
        logWriter.writeExtensions(tag, msg, LogxType.VERBOSE)
    }

    override fun d() {
        logWriter.write(DEFAULT_TAG, "", LogxType.DEBUG)
    }

    override fun d(msg: Any?) {
        logWriter.write(DEFAULT_TAG, msg, LogxType.DEBUG)
    }

    override fun d(tag: String, msg: Any?) {
        logWriter.write(tag, msg, LogxType.DEBUG)
    }

    fun d1(msg: Any?) {
        logWriter.writeExtensions(DEFAULT_TAG, msg, LogxType.DEBUG)
    }

    fun d1(tag: String, msg: Any?) {
        logWriter.writeExtensions(tag, msg, LogxType.DEBUG)
    }

    override fun i() {
        logWriter.write(DEFAULT_TAG, "", LogxType.INFO)
    }

    override fun i(msg: Any?) {
        logWriter.write(DEFAULT_TAG, msg, LogxType.INFO)
    }

    override fun i(tag: String, msg: Any?) {
        logWriter.write(tag, msg, LogxType.INFO)
    }

    fun i1(msg: Any?) {
        logWriter.writeExtensions(DEFAULT_TAG, msg, LogxType.INFO)
    }

    fun i1(tag: String, msg: Any?) {
        logWriter.writeExtensions(tag, msg, LogxType.INFO)
    }

    override fun w() {
        logWriter.write(DEFAULT_TAG, "", LogxType.WARN)
    }

    override fun w(msg: Any?) {
        logWriter.write(DEFAULT_TAG, msg, LogxType.WARN)
    }

    override fun w(tag: String, msg: Any?) {
        logWriter.write(tag, msg, LogxType.WARN)
    }

    fun w1(msg: Any?) {
        logWriter.writeExtensions(DEFAULT_TAG, msg, LogxType.WARN)
    }

    fun w1(tag: String, msg: Any?) {
        logWriter.writeExtensions(tag, msg, LogxType.WARN)
    }

    override fun e() {
        logWriter.write(DEFAULT_TAG, "", LogxType.ERROR)
    }

    override fun e(msg: Any?) {
        logWriter.write(DEFAULT_TAG, msg, LogxType.ERROR)
    }

    override fun e(tag: String, msg: Any?) {
        logWriter.write(tag, msg, LogxType.ERROR)
    }

    fun e1(msg: Any?) {
        logWriter.writeExtensions(DEFAULT_TAG, msg, LogxType.ERROR)
    }

    fun e1(tag: String, msg: Any?) {
        logWriter.writeExtensions(tag, msg, LogxType.ERROR)
    }

    // 확장 기능들
    override fun p() {
        logWriter.writeParent(DEFAULT_TAG, "")
    }

    override fun p(msg: Any?) {
        logWriter.writeParent(DEFAULT_TAG, msg)
    }

    override fun p(tag: String, msg: Any?) {
        logWriter.writeParent(tag, msg)
    }

    fun p1(msg: Any?) {
        logWriter.writeExtensionsParent(DEFAULT_TAG, msg)
    }

    fun p1(tag: String, msg: Any?) {
        logWriter.writeExtensionsParent(tag, msg)
    }

    override fun t() {
        logWriter.writeThreadId(DEFAULT_TAG, "")
    }

    override fun t(msg: Any?) {
        logWriter.writeThreadId(DEFAULT_TAG, msg)
    }

    override fun t(tag: String, msg: Any?) {
        logWriter.writeThreadId(tag, msg)
    }

    override fun j(msg: String) {
        logWriter.writeJson(DEFAULT_TAG, msg)
    }

    override fun j(tag: String, msg: String) {
        logWriter.writeJson(tag, msg)
    }

    fun j1(msg: String) {
        logWriter.writeJsonExtensions(DEFAULT_TAG, msg)
    }

    fun j1(tag: String, msg: String) {
        logWriter.writeJsonExtensions(tag, msg)
    }

    // 레거시 호환성을 위한 getter 메서드들
    @JvmName("getIsDebug")
    fun getDebugMode(): Boolean = config.isDebug

    @JvmName("getIsDebugFilter")
    fun getDebugFilter(): Boolean = config.isDebugFilter

    @JvmName("getIsDebugSave")
    fun getSaveToFile(): Boolean = config.isDebugSave

    @JvmName("getSaveFilePath")
    fun getFilePath(): String = config.saveFilePath

    @JvmName("getAppName")
    fun getAppName(): String = config.appName

    @JvmName("getDebugFilterList")
    fun getDebugFilterList(): Set<String> = config.debugFilterList

    @JvmName("getDebugLogTypeList")
    fun getDebugLogTypeList(): List<LogxType> = config.debugLogTypeList
}
