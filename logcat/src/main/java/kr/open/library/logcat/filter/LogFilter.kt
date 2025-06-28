package kr.open.library.logcat.filter

import kr.open.library.logcat.config.LogxConfig

/**
 * 로그 필터링을 담당하는 인터페이스
 * SRP: 로그 필터링 로직에만 집중
 */
interface LogFilter {
    /**
     * 주어진 태그와 파일명으로 로그를 필터링해야 하는지 확인
     * @param tag 로그 태그
     * @param fileName 파일명
     * @return true면 로그 출력, false면 필터링
     */
    fun shouldLog(tag: String, fileName: String): Boolean
}

/**
 * 기본 로그 필터 구현체
 */
class DefaultLogFilter(private val config: LogxConfig) : LogFilter {
    
    override fun shouldLog(tag: String, fileName: String): Boolean {
        return if (!config.isDebugFilter) {
            true
        } else {
            isTagAllowed(tag) || isFileNameAllowed(fileName)
        }
    }
    
    private fun isTagAllowed(tag: String): Boolean {
        return config.debugFilterList.contains(tag)
    }
    
    private fun isFileNameAllowed(fileName: String): Boolean {
        return config.debugFilterList.contains(fileName)
    }
}