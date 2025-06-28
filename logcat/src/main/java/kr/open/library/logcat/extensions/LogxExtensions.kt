package kr.open.library.logcat.extensions

import kr.open.library.logcat.Logx

/**
 * 모든 객체에 대한 로깅 확장 함수
 * 메서드 체이닝으로 간편한 사용 가능
 * inline 최적화로 성능 향상
 */

// 간편한 API - 더 직관적인 네이밍
@JvmName("logDebug")
public inline fun Any.logD(tag: String = ""): Unit = 
    if (tag.isEmpty()) Logx.d1(this) else Logx.d1(tag, this)

@JvmName("logVerbose") 
public inline fun Any.logV(tag: String = ""): Unit = 
    if (tag.isEmpty()) Logx.v1(this) else Logx.v1(tag, this)

@JvmName("logWarning")
public inline fun Any.logW(tag: String = ""): Unit = 
    if (tag.isEmpty()) Logx.w1(this) else Logx.w1(tag, this)

@JvmName("logInfo")
public inline fun Any.logI(tag: String = ""): Unit = 
    if (tag.isEmpty()) Logx.i1(this) else Logx.i1(tag, this)

@JvmName("logError")
public inline fun Any.logE(tag: String = ""): Unit = 
    if (tag.isEmpty()) Logx.e1(this) else Logx.e1(tag, this)

@JvmName("logJson")
public inline fun String.logJ(tag: String = ""): Unit = 
    if (tag.isEmpty()) Logx.j1(this) else Logx.j1(tag, this)

@JvmName("logParent")
public inline fun Any.logP(tag: String = ""): Unit = 
    if (tag.isEmpty()) Logx.p1(this) else Logx.p1(tag, this)

// 기존 API 호환성 유지 (deprecated 마킹으로 마이그레이션 유도)
@Deprecated("Use logD() instead", ReplaceWith("logD()"))
public inline fun Any.logxD(): Unit = Logx.d1(this)

@Deprecated("Use logD(tag) instead", ReplaceWith("logD(tag)"))
public inline fun Any.logxD(tag: String): Unit = Logx.d1(tag, this)

@Deprecated("Use logV() instead", ReplaceWith("logV()"))
public inline fun Any.logxV(): Unit = Logx.v1(this)

@Deprecated("Use logV(tag) instead", ReplaceWith("logV(tag)"))
public inline fun Any.logxV(tag: String): Unit = Logx.v1(tag, this)

@Deprecated("Use logW() instead", ReplaceWith("logW()"))
public inline fun Any.logxW(): Unit = Logx.w1(this)

@Deprecated("Use logW(tag) instead", ReplaceWith("logW(tag)"))
public inline fun Any.logxW(tag: String): Unit = Logx.w1(tag, this)

@Deprecated("Use logI() instead", ReplaceWith("logI()"))
public inline fun Any.logxI(): Unit = Logx.i1(this)

@Deprecated("Use logI(tag) instead", ReplaceWith("logI(tag)"))
public inline fun Any.logxI(tag: String): Unit = Logx.i1(tag, this)

@Deprecated("Use logE() instead", ReplaceWith("logE()"))
public inline fun Any.logxE(): Unit = Logx.e1(this)

@Deprecated("Use logE(tag) instead", ReplaceWith("logE(tag)"))
public inline fun Any.logxE(tag: String): Unit = Logx.e1(tag, this)

@Deprecated("Use logJ() instead", ReplaceWith("logJ()"))
public inline fun String.logxJ(): Unit = Logx.j1(this)

@Deprecated("Use logJ(tag) instead", ReplaceWith("logJ(tag)"))
public inline fun String.logxJ(tag: String): Unit = Logx.j1(tag, this)

@Deprecated("Use logP() instead", ReplaceWith("logP()"))
public inline fun Any.logxP(): Unit = Logx.p1(this)

@Deprecated("Use logP(tag) instead", ReplaceWith("logP(tag)"))
public inline fun Any.logxP(tag: String): Unit = Logx.p1(tag, this)
