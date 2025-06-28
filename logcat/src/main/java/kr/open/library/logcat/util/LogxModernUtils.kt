package kr.open.library.logcat.util

import kr.open.library.logcat.vo.LogxType

/**
 * 모던 Kotlin 기능을 활용한 로깅 유틸리티
 * - Value classes
 * - Sealed interfaces  
 * - Context receivers (future)
 * - Type-safe builders
 */

/**
 * 타입 안전한 로그 레벨 (Value class 사용)
 */
@JvmInline
value class LogLevel(val priority: Int) : Comparable<LogLevel> {
    override fun compareTo(other: LogLevel): Int = priority.compareTo(other.priority)
    
    companion object {
        val VERBOSE = LogLevel(2)
        val DEBUG = LogLevel(3)
        val INFO = LogLevel(4)
        val WARN = LogLevel(5)
        val ERROR = LogLevel(6)
        val ASSERT = LogLevel(7)
        
        fun fromLogxType(type: LogxType): LogLevel = when (type) {
            LogxType.VERBOSE -> VERBOSE
            LogxType.DEBUG -> DEBUG
            LogxType.INFO -> INFO
            LogxType.WARN -> WARN
            LogxType.ERROR -> ERROR
            LogxType.PARENT -> DEBUG
            LogxType.JSON -> INFO
            LogxType.THREAD_ID -> DEBUG
        }
    }
}

/**
 * 타입 안전한 로그 태그 (Value class 사용)
 */
@JvmInline
value class LogTag(val value: String) {
    init {
        require(value.isNotBlank()) { "Log tag cannot be blank" }
        require(value.length <= 23) { "Log tag cannot exceed 23 characters" }
    }
    
    companion object {
        fun fromClass(clazz: Class<*>): LogTag = LogTag(clazz.simpleName)
        inline fun <reified T> fromClass(): LogTag = fromClass(T::class.java)
    }
}

/**
 * 로그 결과를 나타내는 Sealed interface
 */
sealed interface LogResult {
    /**
     * 로그가 성공적으로 처리됨
     */
    object Success : LogResult
    
    /**
     * 로그가 필터링으로 인해 무시됨
     */
    data class Filtered(val reason: FilterReason) : LogResult
    
    /**
     * 로그 처리 중 오류 발생
     */
    data class Error(val exception: Throwable) : LogResult
}

/**
 * 필터링 이유
 */
enum class FilterReason {
    DEBUG_MODE_DISABLED,
    LOG_TYPE_DISABLED,
    TAG_FILTERED,
    LEVEL_TOO_LOW
}

/**
 * 로그 컨텍스트 정보 (Value class 사용)
 */
@JvmInline
value class LogContext(private val data: String) {
    constructor(
        fileName: String,
        methodName: String,
        lineNumber: Int,
        threadName: String = Thread.currentThread().name
    ) : this("$fileName:$lineNumber#$methodName@$threadName")
    
    val fileName: String get() = data.substringBefore(':')
    val lineNumber: Int get() = data.substringAfter(':').substringBefore('#').toIntOrNull() ?: 0
    val methodName: String get() = data.substringAfter('#').substringBefore('@')
    val threadName: String get() = data.substringAfterLast('@')
    
    override fun toString(): String = "($fileName:$lineNumber).$methodName@$threadName"
}

/**
 * 타입 안전한 로그 엔트리
 */
data class TypeSafeLogEntry(
    val level: LogLevel,
    val tag: LogTag,
    val message: String,
    val context: LogContext,
    val timestamp: Long = System.currentTimeMillis(),
    val throwable: Throwable? = null
) {
    /**
     * 로그 엔트리를 포맷팅
     */
    fun format(appName: String): String = buildString {
        append(appName)
        append(" [")
        append(tag.value)
        append("] : (")
        append(context.fileName)
        append(":")
        append(context.lineNumber)
        append(").")
        append(context.methodName)
        append(" - ")
        append(message)
        
        if (throwable != null) {
            append("\n")
            append(throwable.stackTraceToString())
        }
    }
}

/**
 * 함수형 로거 인터페이스
 */
fun interface LogFunction {
    fun log(entry: TypeSafeLogEntry): LogResult
}

/**
 * DSL을 위한 로그 빌더
 */
class LogEntryBuilder {
    var level: LogLevel = LogLevel.DEBUG
    var tag: LogTag = LogTag("DEFAULT")
    var message: String = ""
    var throwable: Throwable? = null
    
    fun build(context: LogContext): TypeSafeLogEntry {
        return TypeSafeLogEntry(
            level = level,
            tag = tag,
            message = message,
            context = context,
            throwable = throwable
        )
    }
}

/**
 * 현재 스택 프레임에서 LogContext 생성
 */
inline fun currentLogContext(): LogContext {
    val stackTrace = Thread.currentThread().stackTrace
    val frame = stackTrace.getOrNull(2) ?: return LogContext("Unknown", "unknown", 0)
    
    return LogContext(
        fileName = frame.fileName ?: "Unknown",
        methodName = frame.methodName,
        lineNumber = frame.lineNumber
    )
}

/**
 * 타입 안전한 로그 DSL
 */
inline fun buildLogEntry(context: LogContext = currentLogContext(), block: LogEntryBuilder.() -> Unit): TypeSafeLogEntry {
    return LogEntryBuilder().apply(block).build(context)
}

/**
 * 성능 측정을 위한 유틸리티
 */
@JvmInline
value class LogTimer(private val startTimeNanos: Long) {
    companion object {
        fun start(): LogTimer = LogTimer(System.nanoTime())
    }
    
    fun elapsedMillis(): Double = (System.nanoTime() - startTimeNanos) / 1_000_000.0
    fun elapsedMicros(): Double = (System.nanoTime() - startTimeNanos) / 1_000.0
    
    inline fun logElapsed(tag: LogTag, message: String, logFunction: LogFunction) {
        val elapsed = elapsedMillis()
        val logEntry = TypeSafeLogEntry(
            level = LogLevel.DEBUG,
            tag = tag,
            message = "$message (took ${elapsed}ms)",
            context = currentLogContext()
        )
        logFunction.log(logEntry)
    }
}

/**
 * 성능 측정 DSL
 */
inline fun <T> measureLog(
    tag: LogTag,
    message: String,
    logFunction: LogFunction,
    block: () -> T
): T {
    val timer = LogTimer.start()
    return try {
        block()
    } finally {
        timer.logElapsed(tag, message, logFunction)
    }
}