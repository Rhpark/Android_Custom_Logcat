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
}