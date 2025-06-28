package kr.open.library.logcat.writer

import kr.open.library.logcat.config.LogxConfig

/**
 * 파일 작성기 타입
 */
enum class LogFileWriterType {
    /** 즉시 동기 방식 */
    IMMEDIATE,
    /** 비동기 방식 */
    ASYNC,
    /** 버퍼링된 비동기 방식 (권장) */
    BUFFERED_ASYNC,
    /** 아무것도 하지 않음 */
    NO_OP
}

/**
 * 향상된 LogFileWriter 팩토리
 * 다양한 파일 작성 전략을 제공
 */
object LogFileWriterFactory {
    
    /**
     * 설정에 따라 적절한 LogFileWriter를 생성 (기본: 성능 최적화된 버퍼링 방식)
     */
    fun create(config: LogxConfig): LogFileWriter {
        val writerType = if (config.isDebugSave) {
            LogFileWriterType.BUFFERED_ASYNC  // 기본값을 성능 최적화된 방식으로 변경
        } else {
            LogFileWriterType.NO_OP
        }
        return create(config, writerType)
    }
    
    /**
     * 명시적인 타입으로 LogFileWriter 생성
     */
    fun create(config: LogxConfig, type: LogFileWriterType): LogFileWriter {
        return when (type) {
            LogFileWriterType.IMMEDIATE -> ImmediateLogFileWriter(config.saveFilePath)
            LogFileWriterType.ASYNC -> AsyncLogFileWriter(config)
            LogFileWriterType.BUFFERED_ASYNC -> BufferedAsyncLogFileWriter(config)
            LogFileWriterType.NO_OP -> NoOpLogFileWriter()
        }
    }
    
    /**
     * 개발용 설정 (즉시 쓰기, 디버깅에 유용)
     */
    fun createForDevelopment(config: LogxConfig): LogFileWriter {
        return create(config, LogFileWriterType.IMMEDIATE)
    }
    
    /**
     * 프로덕션용 설정 (최적화된 비동기 쓰기)
     */
    fun createForProduction(config: LogxConfig): LogFileWriter {
        return create(config, LogFileWriterType.BUFFERED_ASYNC)
    }
}