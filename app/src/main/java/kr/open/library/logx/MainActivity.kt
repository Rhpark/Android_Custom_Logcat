package kr.open.library.logx

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kr.open.library.logcat.Logx
import kr.open.library.logcat.extensions.logxD
import kr.open.library.logcat.extensions.logxE
import kr.open.library.logcat.extensions.logxI
import kr.open.library.logcat.extensions.logxJ
import kr.open.library.logcat.extensions.logxP

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Logx.init(this)
        setContentView(R.layout.activity_main)

        // 기본 로그 기능 테스트
        simple()
        additionalFeatures()
        filterTest()

        // 새로운 Context 기반 기능 데모
        contextPathDemo()
        fileSaveDemo()
        apiCompatibilityDemo()

        // 최종 테스트
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
    }

    private fun additionalFeatures() {
        val tag = "TAG!!!"
        val msg = "Hello World"
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

    /**
     * Context 기반 최적 경로 확인 데모
     * 새로운 Scoped Storage 호환 기능 테스트
     */
    private fun contextPathDemo() {
        Logx.d("DEMO_START", "Context Path Demo Started")
        Logx.d("=== Context 기배경로 데모 ===")
        val optimalPath = Logx.getOptimalLogPath()
        Logx.d("최적 로그 경로", optimalPath)
        Logx.i("Context 초기화", "Logx.init(context) 완료")
        Logx.i("경로 특징", "앱 전용 저장소 - 권한 불필요")
    }

    /**
     * 파일 저장 기능 데모
     * Context 기반 안전한 파일 저장 테스트
     */
    private fun fileSaveDemo() {
        Logx.d("DEMO_START", "File Save Demo Started")
        Logx.d("=== 파일 저장 기능 데모 ===")

        // 파일 저장 활성화
        Logx.setSaveToFile(true)
        Logx.i("파일 저장 활성화", "이 로그는 파일에 저장됩니다")

        // 저장 경로 확인
        val currentPath = Logx.getOptimalLogPath()
        Logx.i("저장 경로", currentPath)

        // 다양한 로그 레벨 테스트
        Logx.v("VERBOSE 로그", "상세 디버깅 정보")
        Logx.d("DEBUG 로그", "디버깅 정보")
        Logx.i("INFO 로그", "일반 정보")
        Logx.w("WARNING 로그", "경고 메시지")
        Logx.e("ERROR 로그", "오류 메시지")

        Logx.d("파일 저장", "모든 로그가 안전하게 저장되었습니다")
    }

    /**
     * Android API 호환성 데모
     * Scoped Storage 지원 및 deprecated API 제거 효과 확인
     */
    private fun apiCompatibilityDemo() {
        Logx.d("DEMO_START", "API Compatibility Demo Started")
        Logx.d("=== Android API 호환성 데모 ===")

        // 현재 디바이스 API 레벨 확인
        val apiLevel = android.os.Build.VERSION.SDK_INT
        Logx.i("Android API Level", "API $apiLevel")
        Logx.i("Android Version", android.os.Build.VERSION.RELEASE)

        // Scoped Storage 지원 여부 확인
        val isScopedStorage = apiLevel >= android.os.Build.VERSION_CODES.Q
        Logx.i("Scoped Storage 지원", if (isScopedStorage) "API 29+ - 지원" else "API 28 이하 - 비지원")

        // 경로 호환성 확인
        Logx.i("경로 호환성", "deprecated API 제거로 모든 API에서 안전")
        Logx.i("권한 요구", "없음 - 앱 전용 저장소 사용")

        // 디바이스 정보
        Logx.d("디바이스 정보", "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")
        Logx.d("개선 효과", "Context 기반 초기화로 안정성 향상")
    }
}