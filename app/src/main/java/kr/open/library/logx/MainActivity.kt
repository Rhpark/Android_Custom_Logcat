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


        val jsonType = "{'contacts': [{'contact_type': 'individual',\n" +
                "   'is_green_match': True,\n" +
                "   'is_signatory': True,\n" +
                "   'match_score': 101,\n" +
                "   'persons': [{'addresses': [{'city': 'NEW YORK',\n" +
                "       'line1': '31 W 34TH ST',\n" +
                "       'postal_code': '10001',\n" +
                "       'state_code': 'NY'}],\n" +
                "     'display': 'Ishay Oved',\n" +
                "     'emails': [],\n" +
                "     'first_name': 'Ishay',\n" +
                "     'id': '8e6d391d-0fa8-5e9d-9880-fd1069c4d190',\n" +
                "     'jobs': [],\n" +
                "     'last_name': 'Oved',\n" +
                "     'phones': [],\n" +
                "     'urls': []}]},\n" +
                "  {'company': {'addresses': [{'city': 'NEW YORK',\n" +
                "      'country_code': 'USA',\n" +
                "      'line1': '1185 6TH AVE FL 10',\n" +
                "      'postal_code': '10036',\n" +
                "      'state_code': 'NY'}],\n" +
                "    'emails': [],\n" +
                "    'id': '2987b8f5-9d94-5858-b065-1fc62b315e80',\n" +
                "    'match_score': 53,\n" +
                "    'name': '31 WEST 34TH STREET LLC',\n" +
                "    'phones': [],\n" +
                "    'urls': []},\n" +
                "   'contact_type': 'company',\n" +
                "   'is_green_match': True,\n" +
                "   'is_signatory': False,\n" +
                "   'match_score': 53,\n" +
                "   'persons': []}],\n" +
                " 'owner_update_time': '2021-08-26',\n" +
                " 'property_id': '2c1820dc-2a57-5532-8022-0a8840e32da7'}"

        Logx.j(jsonType)
        Logx.j(tag, jsonType)
        jsonType.logxJ()
        jsonType.logxJ(tag)
    }
}