package kr.open.library.logcat.config

import android.content.Context
import android.os.Build


/**
 * 로그 파일 경로 관련 유틸리티
 */
object LogxPathUtils {

    /**
     * 안전한 기본 로그 경로 (Context 없을 때 fallback)
     */
    fun getDefaultLogPath(): String {
        return "/data/data/logs"
    }

    /**
     * 앱 전용 로그 디렉토리 경로 (권한 불필요)
     */
    fun getAppLogPath(context: Context): String {
        return context.getExternalFilesDir("logs")?.absolutePath
            ?: context.filesDir.absolutePath + "/logs"
    }

    /**
     * Scoped Storage 호환 경로 (권장 - 모든 API에서 안전)
     */
    fun getScopedStoragePath(context: Context): String {
        return getAppLogPath(context)
    }

    /**
     * API 레벨별 최적화된 로그 경로
     */
    fun getOptimalLogPath(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // API 29+: Scoped Storage
            getAppLogPath(context)
        } else {
            // API 28 이하: 여전히 앱 전용 경로 사용 (권한 불필요)
            getAppLogPath(context)
        }
    }
}