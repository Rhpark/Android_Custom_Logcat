package kr.open.library.logcat.config

import android.os.Environment
import kr.open.library.logcat.vo.LogxType

/**
 * Logx 라이브러리의 설정을 관리하는 데이터 클래스
 * 불변성과 타입 안전성을 보장
 */
data class LogxConfig(
    val isDebug: Boolean = true,
    val isDebugFilter: Boolean = false,
    val isDebugSave: Boolean = false,
    val saveFilePath: String = getDefaultLogPath(),
    val appName: String = "RhPark",
    val debugFilterList: Set<String> = emptySet(),
    val debugLogTypeList: List<LogxType> = listOf(
        LogxType.VERBOSE,
        LogxType.DEBUG,
        LogxType.INFO,
        LogxType.WARN,
        LogxType.ERROR,
        LogxType.PARENT,
        LogxType.JSON,
        LogxType.THREAD_ID,
    )
) {
    companion object {
        fun builder() = LogxConfigBuilder()
        
        /**
         * API 레벨에 따른 안전한 기본 로그 경로 제공
         */
        private fun getDefaultLogPath(): String {
            return try {
                @Suppress("DEPRECATION")
                Environment.getExternalStorageDirectory().path
            } catch (e: Exception) {
                // Fallback to internal storage
                "/data/data/logs"
            }
        }
    }
}

/**
 * LogxConfig를 생성하기 위한 빌더 패턴
 * 메서드 체이닝으로 편리한 설정 가능
 */
class LogxConfigBuilder {
    private var isDebug: Boolean = true
    private var isDebugFilter: Boolean = false
    private var isDebugSave: Boolean = false
    private var saveFilePath: String = getDefaultLogPath()
    private var appName: String = "RhPark"
    private var debugFilterList: Set<String> = emptySet()
    private var debugLogTypeList: List<LogxType> = listOf(
        LogxType.VERBOSE, LogxType.DEBUG, LogxType.INFO, LogxType.WARN,
        LogxType.ERROR, LogxType.PARENT, LogxType.JSON, LogxType.THREAD_ID
    )

    fun setDebugMode(isDebug: Boolean) = apply { this.isDebug = isDebug }
    fun setDebugFilter(isFilter: Boolean) = apply { this.isDebugFilter = isFilter }
    fun setSaveToFile(isSave: Boolean) = apply { this.isDebugSave = isSave }
    fun setFilePath(path: String) = apply { this.saveFilePath = path }
    fun setAppName(name: String) = apply { this.appName = name }
    fun setDebugFilterList(tags: List<String>) = apply { this.debugFilterList = tags.toSet() }
    fun setDebugLogTypeList(types: List<LogxType>) =
        apply { this.debugLogTypeList = types.toList() }

    fun build() = LogxConfig(
        isDebug = isDebug,
        isDebugFilter = isDebugFilter,
        isDebugSave = isDebugSave,
        saveFilePath = saveFilePath,
        appName = appName,
        debugFilterList = debugFilterList,
        debugLogTypeList = debugLogTypeList
    )
}