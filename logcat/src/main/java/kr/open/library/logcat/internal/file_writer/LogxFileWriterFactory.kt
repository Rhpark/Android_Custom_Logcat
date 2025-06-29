package kr.open.library.logcat.internal.file_writer

import kr.open.library.logcat.config.LogxConfig
import kr.open.library.logcat.internal.file_writer.base.LogxFileWriterImp

/**
 * LogFileWriter 생성을 담당하는 팩토리
 * SRP: LogFileWriter 인스턴스 생성에만 집중
 */
object LogxFileWriterFactory {
    
    /**
     * 설정에 따라 적절한 LogFileWriter를 생성
     * @param config 로그 설정
     * @return 적절한 LogFileWriter 구현체
     */
    fun create(config: LogxConfig): LogxFileWriterImp {
        return if (config.isDebugSave) {
            LogxFileWriter(config.saveFilePath)
        } else {
            NoOpLogFileWriter()
        }
    }
}