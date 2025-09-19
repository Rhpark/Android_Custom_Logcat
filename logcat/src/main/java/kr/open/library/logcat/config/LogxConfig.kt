package kr.open.library.logcat.config

import android.content.Context
import kr.open.library.logcat.model.LogxType
import java.util.EnumSet

/**
 * Logx 라이브러리의 설정을 관리하는 데이터 클래스
 * 불변성과 타입 안전성을 보장
 */
data class LogxConfig(
    val isDebug: Boolean = true,
    val isDebugFilter: Boolean = false,
    val isDebugSave: Boolean = false,
    val saveFilePath: String = LogxPathUtils.getDefaultLogPath(),
    val appName: String = "RhPark",
    val debugFilterList: Set<String> = emptySet(),
    val debugLogTypeList: EnumSet<LogxType> = EnumSet.allOf(LogxType::class.java)
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
         * Context 기반 최적 설정 생성 (권장)
         */
        fun createDefault(context: Context): LogxConfig {
            return LogxConfig(
                saveFilePath = LogxPathUtils.getScopedStoragePath(context)
            )
        }

        /**
         * Context 없을 때 fallback 설정
         */
        fun createFallback(): LogxConfig {
            return LogxConfig(
                saveFilePath = LogxPathUtils.getDefaultLogPath()
            )
        }
    }
}
