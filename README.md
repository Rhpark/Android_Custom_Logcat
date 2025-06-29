# Android Easy Development Library - Logx (Simple Android Logging Library)

This library helps you make easy and more simple code for Android developers


안드로이드 개발자를 위해

좀 더 간단히 확인 할 수 있거나.

좀 더 간단히 만들 수 있거나.


Android 애플리케이션을 위한 간단한 로깅 라이브러리입니다. 편리한 사용법을 제공합니다.

## 🚀 주요 기능 (Key Features)

### 1. **기본 로깅 (Basic Logging)**

- 표준 Android Log 레벨 지원 (VERBOSE, DEBUG, INFO, WARN, ERROR)
- 자동 클래스명, 메서드명, 라인 번호 표시
- 태그 기반 필터링

- Supports standard Android Log levels: VERBOSE, DEBUG, INFO, WARN, ERROR
- Automatically displays class name, method name, and line number
- Tag-based filtering


### 2. **추가 기능**

- **부모 메서드 추적** (p): 호출 스택 정보 표시
- **JSON 포맷팅** (j): 자동 JSON 들여쓰기
- **스레드 정보** (t): 현재 스레드 ID 표시
- **파일 저장**: 로그를 파일로 자동 저장

- Displays call stack information
- Automatic JSON indentation
- Displays the current thread ID
- Automatically saves logs to a file



## 📦 설치

### Gradle

```
dependencies {
    implementation 'com.github.rhpark:Android_Custom_Logcat:0.9.2'
}
```

```
repositories {
    maven { url 'https://jitpack.io' }
}
```

## 🔧 설정 (Configuration)

### 기본 설정 (Basic Configuration)

```kotlin
// 앱 초기화 시 (Application 클래스에서)
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Case 1: 개별 설정
        Logx.setDebugMode(true)
        Logx.setAppName("MyApp")
        Logx.setSaveToFile(true)
        Logx.setFilePath("/sdcard/MyApp/logs")
        
        // Case 2: Builder 패턴 (권장)
        val config = LogxConfig.builder()
            .setDebugMode(BuildConfig.DEBUG)
            .setAppName("MyApp")
            .setSaveToFile(true)
            .setFilePath(getExternalFilesDir("logs")?.absolutePath ?: "")
            .setDebugFilterList(listOf("Network", "Database"))
            .build()
            
        Logx.updateConfig(config)
    }
}
```

## 📝 사용법

### 1. 기본 로깅

```kotlin
class MainActivity : AppCompatActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      setContentView(R.layout.activity_main)
      simple()
      additionalFeatures()
      filterTest()
      simple()
   }

   private fun simple() {
      val tag = "TAG!!!"
      val msg = "Hello World"
      Logx.i()
      Logx.i(msg)
      Logx.i(tag, msg)
      msg.logxI()
      msg.logxI(tag)

      Logx.d()
      Logx.d(msg)
      Logx.d(tag, msg)
      msg.logxD()
      msg.logxD(tag)

      Logx.e()
      Logx.e(msg)
      Logx.e(tag, msg)
      msg.logxE()
      msg.logxE(tag)
      
      // output example:
      // D/MyApp [] : (MainActivity.kt:25).onCreate - Hello World
      // I/MyApp [TAG!!!] : (MainActivity.kt:26).onCreate - Activity created
   }

   private fun additionalFeatures() {
      val tag = "TAG!!!"
      val msg = "Hello World"

      // Show parent method data
      Logx.p()
      Logx.p(msg)
      Logx.p(tag, msg)
      msg.logxP()
      msg.logxP(tag)

      // output
      // D/MyApp [] [PARENT] : ┎(MainActivity.kt:15) - [com.example.MainActivity.onButtonClick]
      // D/MyApp [] [PARENT] : ┖(MainActivity.kt:10).test - Hello World
      
      val jsonType = "{\n" +
              "      \"name\": \"John Doe\",\n" +
              "      \"age\": 30,\n" +
              "      \"city\": \"New York\"\n" +
              "}"

      //Show JSON Format data
      Logx.j(jsonType)
      Logx.j(tag, jsonType)
      jsonType.logxJ()
      jsonType.logxJ(tag)

      // output
      // D/MyApp [] JSON Format
      //{
      //
      //    "name": "John Doe",
      //
      //    "age": 30,
      //
      //    "city": "New York"
      //
      //}
   }

   private fun filterTest() {
      val tag = "PPP"
      val msg = "Hello World"
      Logx.setDebugFilter(true)
      Logx.setDebugFilterList(listOf(tag))
      Logx.i()
      Logx.i(msg)
      Logx.i(tag, msg)
      msg.logxI()
      msg.logxI(tag)

      Logx.d()
      Logx.d(msg)
      Logx.d(tag, msg)
      msg.logxD()
      msg.logxD(tag)

      Logx.e()
      Logx.e(msg)
      Logx.e(tag, msg)
      msg.logxE()
      msg.logxE(tag)

      Logx.p()
      Logx.p(msg)
      Logx.p(tag, msg)
      msg.logxP()
      msg.logxP(tag)


      val jsonType = "{\n" +
              "      \"name\": \"John Doe\",\n" +
              "      \"age\": 30,\n" +
              "      \"city\": \"New York\"\n" +
              "}"

      Logx.j(jsonType)
      Logx.j(tag, jsonType)
      jsonType.logxJ()
      jsonType.logxJ(tag)
   }
}

```


### 기본 형식 (Basic Format)

```
[로그레벨]/[앱이름] ["태그"] : ([파일명]:[라인번호]).[메서드명] - [메시지]
[LogLevel]/[AppName]  ["Tag"] : ([FileName]:[LineNumber] ) . [ MethodName]  - [Message]

Example :
D/MyApp ["Network"] : (ApiService.kt:45).fetchUser - Request started
```

### 추가 기능 형식 (Special Feature Format)

```
Parent Method (p):
D/MyApp [] [PARENT] : ┎(MainActivity.kt:20) - [com.example.MainActivity.onClick]
D/MyApp [] [PARENT] : ┖(ApiService.kt:10).fetchData - API 호출

JSON (j):
D/MyApp [] [JSON] : (ApiService.kt:15).parseResponse - =========JSON_START========
D/MyApp [] [JSON] : (ApiService.kt:15) {
D/MyApp [] [JSON] : (ApiService.kt:15)   "name": "John",
D/MyApp [] [JSON] : (ApiService.kt:15)   "age": 30
D/MyApp [] [JSON] : (ApiService.kt:15) }
D/MyApp [] [JSON] : (ApiService.kt:15).parseResponse - =========JSON_END==========

Thread (t):
D/MyApp [] [T_ID] : [123](BackgroundTask.kt:25).doWork - 작업 처리 중
```

## 🗂️ 파일 저장 (File Saving)

### 설정 (Configuration)

```kotlin
// 파일 저장 활성화
Logx.setSaveToFile(true)
Logx.setFilePath("/sdcard/MyApp/logs")
```

### 파일 형식

- Filename: `24-01-15_14-30-25_Log.txt`
- Content: `24-01-15, 14:30:25.123/D/MyApp : Log Msg`


### 메모리 관리 (Memory Management)

- 약한 참조 사용으로 메모리 누수 방지
- 앱 종료 시 자동 리소스 해제
- 크래시 시에도 로그 데이터 보존

- Preventing memory leaks using weak references
- Automatic resource release on app termination
- Log data preservation even in case of a crash


## 🔒 권한 (Permissions)

파일 저장 기능 사용 시 필요한 권한:

Permissions required when using the file saving feature


### AndroidManifest.xml

```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

        <!-- Access to external storage on Android 10 and above -->
<application
    android:requestLegacyExternalStorage="true">
</application>
```

### 런타임 권한 (Android 6.0+)

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    requestPermissions(arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ), REQUEST_CODE)
}
```

## 🎨 아키텍처

### 핵심 컴포넌트

```
Logx (메인 API)
├── ILogx (인터페이스)
├── LogxConfig (설정 관리)
├── LogxWriter (로그 처리 & 파일 관리)
├── LogxStackTrace (스택 추적)
...

```

## 🔄 마이그레이션 가이드

### 기존 Android Log에서 마이그레이션

```kotlin
// 기존 코드
Log.d("TAG", "메시지")
Log.e("TAG", "에러 메시지")

// Logx로 변경
Logx.d("TAG", "메시지")
Logx.e("TAG", "에러 메시지")

// 또는 확장 함수 사용
"메시지".logd("TAG")
"에러 메시지".loge("TAG")
```


### 자주 발생하는 문제

1. **로그가 표시되지 않음**
   ```kotlin
   // 디버그 모드 확인
   Logx.setDebugMode(true)
   ```

2. **파일이 저장되지 않음**
   ```kotlin
   // 권한 및 경로 확인
   Logx.setSaveToFile(true)
   Logx.setFilePath(context.getExternalFilesDir("logs")?.absolutePath ?: "")
   ```

3. **성능 이슈**
   ```kotlin
   // 로그 레벨 제한
   Logx.setDebugLogTypeList(listOf(LogxType.ERROR, LogxType.WARN))
   ```

## 📄 라이선스

MIT License
