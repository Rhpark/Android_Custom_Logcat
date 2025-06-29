package kr.open.library.logcat.writer

import kr.open.library.logcat.repo.vo.LogxType
import kr.open.library.logcat.writer.base.LogxFileWriterImp

/**
 * 파일 저장을 하지 않는 No-Op 구현체
 * SRP: 파일 저장 비활성화에만 집중
 * Null Object Pattern 적용
 */
class NoOpLogFileWriter : LogxFileWriterImp {
    
    override fun writeLog(logType: LogxType, tag: String, message: String) {
        // 아무것도 하지 않음
    }
    
    override fun cleanup() {
        // 아무것도 하지 않음
    }
}