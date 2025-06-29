package kr.open.library.logcat.config

import android.content.Context
import android.os.Build
import android.os.Environment


/**
 * 로그 파일 경로 관련 유틸리티
 */
object LogxPathUtils {

    /**
     * API 레벨에 따른 안전한 기본 로그 경로 제공
     */
    fun getDefaultLogPath(): String {
        return try {
            @Suppress("DEPRECATION")
            Environment.getExternalStorageDirectory().path
        } catch (e: Exception) {
            "/data/data/logs"
        }
    }

    /**
     * 앱 전용 로그 디렉토리 경로 (권한 불필요)
     */
    fun getAppLogPath(context: Context): String {
        return context.getExternalFilesDir("logs")?.absolutePath
            ?: context.filesDir.absolutePath + "/logs"
    }

    /**
     * Scoped Storage 호환 경로
     */
    fun getScopedStoragePath(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getAppLogPath(context)
        } else {
            getDefaultLogPath()
        }
    }
}