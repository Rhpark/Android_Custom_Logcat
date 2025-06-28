package kr.open.library.logcat.formatter

import kr.open.library.logcat.vo.LogxType

/**
 * 로그 포맷팅을 담당하는 인터페이스
 * OCP 원칙을 준수하여 새로운 포맷터를 쉽게 추가할 수 있도록 함
 */
interface LogFormatter {
    /**
     * 로그 메시지를 포맷팅 (메모리 효율적인 방식)
     * @param tag 로그 태그
     * @param message 로그 메시지
     * @param logType 로그 타입
     * @param stackInfo 스택 정보 (옵션)
     * @return 포맷팅된 로그 메시지, null이면 필터링됨
     */
    fun format(tag: String, message: Any?, logType: LogxType, stackInfo: String = ""): FormattedLog?
    
    /**
     * StringBuilder를 재사용하는 효율적인 포맷팅 (옵션)
     * 메모리 압박이 심한 환경에서 사용
     */
    fun formatTo(
        stringBuilder: StringBuilder,
        tag: String, 
        message: Any?, 
        logType: LogxType, 
        stackInfo: String = ""
    ): Boolean {
        val formatted = format(tag, message, logType, stackInfo)
        return if (formatted != null) {
            stringBuilder.clear()
            stringBuilder.append(formatted.message)
            true
        } else {
            false
        }
    }
}

/**
 * 메모리 효율적인 포맷팅된 로그 데이터
 * - String interning으로 메모리 최적화
 * - value class 사용 가능하도록 준비
 */
@JvmInline
value class FormattedLog private constructor(private val data: String) {
    
    constructor(tag: String, message: String, logType: LogxType) : this(
        buildString {
            append(tag.intern())  // String interning으로 메모리 절약
            append('\u0001')      // 구분자
            append(message)
            append('\u0001')
            append(logType.logTypeString)
        }
    )
    
    val tag: String get() = data.substringBefore('\u0001')
    val message: String get() {
        val firstSep = data.indexOf('\u0001')
        val secondSep = data.lastIndexOf('\u0001')
        return data.substring(firstSep + 1, secondSep)
    }
    val logType: LogxType get() {
        val typeString = data.substringAfterLast('\u0001')
        return LogxType.values().first { it.logTypeString == typeString }
    }
    
    override fun toString(): String = "FormattedLog(tag='$tag', message='$message', logType=$logType)"
}

/**
 * StringBuilder 풀 - 메모리 재사용으로 GC 압박 감소
 */
object StringBuilderPool {
    private val pool = ThreadLocal.withInitial { StringBuilder(256) }
    
    inline fun <T> use(block: (StringBuilder) -> T): T {
        val sb = pool.get()
        try {
            sb.clear()
            return block(sb)
        } finally {
            if (sb.length > 1024) {
                // 너무 큰 StringBuilder는 풀에서 제거
                pool.set(StringBuilder(256))
            }
        }
    }
}

/**
 * 메모리 효율적인 기본 포맷터 추상 클래스
 */
abstract class EfficientLogFormatter : LogFormatter {
    
    /**
     * StringBuilder 재사용으로 메모리 효율성 향상
     */
    protected fun buildFormattedMessage(
        tag: String,
        message: Any?,
        logType: LogxType,
        stackInfo: String,
        builder: (StringBuilder) -> Unit
    ): FormattedLog {
        return StringBuilderPool.use { sb ->
            builder(sb)
            FormattedLog(tag, sb.toString(), logType)
        }
    }
}