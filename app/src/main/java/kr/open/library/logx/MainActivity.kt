package kr.open.library.logx

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kr.open.library.logcat.Logx
import kr.open.library.logcat.config.LogxStorageType
import kr.open.library.logcat.extensions.logxD
import kr.open.library.logcat.extensions.logxE
import kr.open.library.logcat.extensions.logxI
import kr.open.library.logcat.extensions.logxJ
import kr.open.library.logcat.extensions.logxP

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    // 권한 요청 런처
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Logx.d("권한", "외부 저장소 권한 승인됨")
            applyStorageSettings()
        } else {
            Logx.w("권한", "외부 저장소 권한 거부됨 - 내부 저장소로 대체")
            setStorageType(LogxStorageType.INTERNAL)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Logx 초기화
        Logx.init(this)

        // SharedPreferences 초기화
        prefs = getSharedPreferences("logx_settings", MODE_PRIVATE)

        // 저장된 설정 로드 및 적용
        loadAndApplyStorageSettings()

        setContentView(R.layout.activity_main)

        // UI 버튼 설정
        setupButtons()

        // 앱 시작 환영 메시지
        Logx.i("앱 시작", "Logx 저장소 설정 테스트 앱이 시작되었습니다")
        Logx.i("사용법", "버튼을 눌러서 다양한 저장소 설정을 테스트해보세요")

        // JSON 로그 테스트 (이상 여부 확인)
        testJsonLogs()
    }

    /**
     * UI 버튼들 설정
     */
    private fun setupButtons() {
        // 내부 저장소 버튼
        findViewById<android.widget.Button>(R.id.btnInternalStorage).setOnClickListener {
            Logx.i("버튼 클릭", "내부 저장소 버튼 클릭됨")
            useInternalStorage()
        }

        // 앱 전용 외부 저장소 버튼
        findViewById<android.widget.Button>(R.id.btnAppExternalStorage).setOnClickListener {
            Logx.i("버튼 클릭", "앱 전용 외부 저장소 버튼 클릭됨")
            useAppExternalStorage()
        }

        // 공용 외부 저장소 버튼
        findViewById<android.widget.Button>(R.id.btnPublicExternalStorage).setOnClickListener {
            Logx.i("버튼 클릭", "공용 외부 저장소 버튼 클릭됨")
            usePublicExternalStorage()
        }

        // 저장소 정보 보기 버튼
        findViewById<android.widget.Button>(R.id.btnShowStorageInfo).setOnClickListener {
            Logx.i("버튼 클릭", "저장소 정보 보기 버튼 클릭됨")
            showCurrentStorageInfo()
        }

        // 테스트 로그 작성 버튼
        findViewById<android.widget.Button>(R.id.btnTestLog).setOnClickListener {
            Logx.i("버튼 클릭", "테스트 로그 작성 버튼 클릭됨")
            writeTestLogs()
        }
    }

    /**
     * 테스트 로그들 작성
     */
    private fun writeTestLogs() {
        val timestamp = System.currentTimeMillis()
        Logx.d()
        Logx.i("TEST", "=== 테스트 로그 시작 (타임스탬프: $timestamp) ===")

        // 다양한 로그 레벨 테스트
        Logx.v("VERBOSE", "상세 로그 메시지")
        Logx.d("DEBUG", "디버그 로그 메시지")
        Logx.i("INFO", "정보 로그 메시지")
        Logx.w("WARNING", "경고 로그 메시지")
        Logx.e("ERROR", "오류 로그 메시지")

        // JSON 로그 테스트
        val jsonData = """
        {"timestamp": $timestamp, "storage_test": true,
            "message": "저장소 테스트용 JSON 데이터"
        }
        """.trimIndent()
        Logx.j("JSON_TEST", jsonData)

        // 부모 메서드 정보 테스트
        Logx.p("PARENT", "부모 메서드 정보 포함 로그")

        Logx.i("테스트", "=== 테스트 로그 완료 ===")
        Logx.i("확인", "파일 저장 여부는 파일 관리자에서 확인하세요")
    }

    /**
     * JSON 로그 이상 여부 테스트
     */
    private fun testJsonLogs() {
        Logx.i("JSON 테스트", "=== JSON 포맷 로그 이상 여부 확인 ===")

        // 1. 기본 JSON 테스트
        val simpleJson = """{"name": "test", "value": 123}"""
        Logx.j("SIMPLE", simpleJson)

        // 2. 복잡한 JSON 테스트 (경로 포함)
        val complexJson = """
        {
            "app_info": {
                "name": "Logx Test",
                "version": "1.0.0",
                "paths": {
                    "internal": "/data/data/kr.open.library.logx/files/logs",
                    "external": "/storage/emulated/0/Android/data/kr.open.library.logx/files/logs",
                    "public": "/storage/emulated/0/Android/data/kr.open.library.logx/files/Documents/Logx"
                }
            },
            "test_data": {
                "timestamp": ${System.currentTimeMillis()},
                "special_chars": "한글 테스트 \"quotes\" and \\backslash\\",
                "array": [1, 2, 3, "string", true],
                "nested": {
                    "level1": {
                        "level2": "deep value"
                    }
                }
            }
        }
        """.trimIndent()
        Logx.j("COMPLEX", complexJson)

        // 3. 특수 문자가 포함된 JSON 테스트
        val specialJson = """
        {
            "file_path": "C:\\Users\\Test\\Documents\\file.txt",
            "url": "https://example.com/api?param=value&other=123",
            "message": "Line1\nLine2\tTabbed",
            "unicode": "🚀 Unicode 테스트 ✅",
            "quotes": "He said \"Hello\" and she replied 'Hi'",
            "escaped": "This has \\\"escaped\\\" quotes"
        }
        """.trimIndent()
        Logx.j("SPECIAL", specialJson)

        Logx.i("JSON 테스트", "JSON 테스트 완료 - Logcat에서 포맷 확인하세요")
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



    // ========================================
    // 실제 저장소 설정 관리 메서드들
    // ========================================

    /**
     * 저장된 설정을 로드하고 적용
     */
    private fun loadAndApplyStorageSettings() {
        val savedType = prefs.getString("storage_type", LogxStorageType.APP_EXTERNAL.name)
        val storageType = try {
            LogxStorageType.valueOf(savedType ?: LogxStorageType.APP_EXTERNAL.name)
        } catch (e: Exception) {
            LogxStorageType.APP_EXTERNAL
        }

        Logx.d("설정 로드", "저장된 저장소 타입: $storageType")
        setStorageType(storageType)
    }

    /**
     * 저장소 타입 설정 (권한 처리 포함)
     */
    fun setStorageType(storageType: LogxStorageType) {
        Logx.i("저장소 설정", "저장소 타입을 $storageType 으로 변경")

        // 권한이 필요한 경우 먼저 권한 확인
        if (storageType == LogxStorageType.PUBLIC_EXTERNAL && requiresStoragePermission()) {
            requestStoragePermissionIfNeeded(storageType)
            return
        }

        // 권한이 필요없거나 이미 권한이 있는 경우 바로 적용
        applyStorageType(storageType)
        saveStorageSettings(storageType)
    }

    /**
     * 실제 저장소 타입 적용
     */
    private fun applyStorageType(storageType: LogxStorageType) {
        when (storageType) {
            LogxStorageType.INTERNAL -> {
                Logx.setInternalStorage()
                Logx.i("저장소 적용", "내부 저장소로 설정됨")
            }
            LogxStorageType.APP_EXTERNAL -> {
                Logx.setAppExternalStorage()
                Logx.i("저장소 적용", "앱 전용 외부 저장소로 설정됨")
            }
            LogxStorageType.PUBLIC_EXTERNAL -> {
                Logx.setPublicExternalStorage()
                Logx.i("저장소 적용", "공용 외부 저장소로 설정됨")
            }
        }

        // 파일 저장 활성화
        Logx.setSaveToFile(true)

        // 현재 저장소 정보 로그
        val storageInfo = Logx.getStorageInfo()
        storageInfo.forEach { (type, path) ->
            if (type == storageType) {
                Logx.i("활성 저장소", "$type: $path")
            }
        }
    }

    /**
     * 설정을 SharedPreferences에 저장
     */
    private fun saveStorageSettings(storageType: LogxStorageType) {
        prefs.edit()
            .putString("storage_type", storageType.name)
            .apply()
        Logx.d("설정 저장", "저장소 타입 $storageType 이 저장됨")
    }

    /**
     * 저장된 설정 로드 (외부에서 호출 가능)
     */
    private fun applyStorageSettings() {
        val savedType = prefs.getString("storage_type", LogxStorageType.APP_EXTERNAL.name)
        val storageType = LogxStorageType.valueOf(savedType ?: LogxStorageType.APP_EXTERNAL.name)
        applyStorageType(storageType)
    }

    /**
     * 권한이 필요한지 확인
     */
    private fun requiresStoragePermission(): Boolean {
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
    }

    /**
     * 권한 요청
     */
    private fun requestStoragePermissionIfNeeded(storageType: LogxStorageType) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                Logx.w("권한 요청", "외부 저장소 권한이 필요합니다")
                storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                // 이미 권한이 있음
                applyStorageType(storageType)
                saveStorageSettings(storageType)
            }
        } else {
            // API 29+ 에서는 권한 불필요
            applyStorageType(storageType)
            saveStorageSettings(storageType)
        }
    }

    // ========================================
    // 사용자 편의 메서드들 (외부에서 호출 가능)
    // ========================================

    /**
     * 내부 저장소로 설정
     */
    fun useInternalStorage() {
        setStorageType(LogxStorageType.INTERNAL)
    }

    /**
     * 앱 전용 외부 저장소로 설정 (권한 불필요)
     */
    fun useAppExternalStorage() {
        setStorageType(LogxStorageType.APP_EXTERNAL)
    }

    /**
     * 공용 외부 저장소로 설정 (권한 필요할 수 있음)
     */
    fun usePublicExternalStorage() {
        setStorageType(LogxStorageType.PUBLIC_EXTERNAL)
    }

    /**
     * 현재 저장소 정보 출력
     */
    fun showCurrentStorageInfo() {
        val storageInfo = Logx.getStorageInfo()
        Logx.i("저장소 정보", "=== 사용 가능한 저장소 ===")
        storageInfo.forEach { (type, path) ->
            val accessible = when (type) {
                LogxStorageType.INTERNAL -> "사용자 접근 불가"
                LogxStorageType.APP_EXTERNAL -> "파일관리자로 접근 가능"
                LogxStorageType.PUBLIC_EXTERNAL -> "파일관리자로 쉽게 접근"
            }
            Logx.i("저장소", "$type: $path ($accessible)")
        }
    }
}