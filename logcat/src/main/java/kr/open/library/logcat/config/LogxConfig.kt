package kr.open.library.logcat.config

import android.os.Environment
import kr.open.library.logcat.repo.vo.LogxType

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
    val debugLogTypeList: List<LogxType> = LogxType.entries.toList()
//    val debugLogTypeList: List<LogxType> = listOf(
//        LogxType.VERBOSE,
//        LogxType.DEBUG,
//        LogxType.INFO,
//        LogxType.WARN,
//        LogxType.ERROR,
//        LogxType.PARENT,
//        LogxType.JSON,
//        LogxType.THREAD_ID,
//    )
) {
    companion object {

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
