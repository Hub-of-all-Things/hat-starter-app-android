package io.dataswift.starterApp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.android.core.Json
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.managers.HATNetworkManager
import com.hubofallthings.android.hatApi.managers.HATParserManager
import com.hubofallthings.android.hatApi.managers.ResultType
import com.hubofallthings.android.hatApi.services.HATAccountService
import io.dataswift.starterApp.R
import io.dataswift.starterApp.helpers.AppConfig
import io.dataswift.starterApp.helpers.UserHelper
import io.dataswift.starterApp.model.HATBundleObject
import io.dataswift.starterApp.services.EncryptionServices
import io.dataswift.starterApp.services.MyPreferences
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mPreference: MyPreferences
    private lateinit var mUser: UserHelper
    private lateinit var mEncryptionServices: EncryptionServices
    private val namespace = AppConfig.namespace
    private val scope = AppConfig.scope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        mPreference = MyPreferences(this)
        mEncryptionServices = EncryptionServices(this)
        mUser = UserHelper(this)

        saveBtn.setOnClickListener(this)
        logoutBtn.setOnClickListener(this)
        getValues()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.saveBtn -> {
                saveValue()
            }
            R.id.logoutBtn -> {
                logout()
            }
        }
    }

    /*
       Gets the value from the editText and store it into the HAT.
    */
    private fun saveValue() {
        val token = mUser.getToken()
        val userDomain = mUser.getUserDomain()

        val mapper = jacksonObjectMapper()
        val body = mapper.writeValueAsString(mapOf("value" to nameEt.text.toString()))

        if (token.isNullOrEmpty()) {
            return
        }

        HATAccountService().createTableValue(
            token,
            userDomain,
            namespace,
            scope,
            body,
            { json, newToken ->
                getValues()
                displayToast("Successful post!")
            }, { res ->
                displayToast("Something went wrong!, ${res.errorMessage}")
            }
        )
    }

    /*
        Get the values from the HAT.
   */
    private fun getValues() {
        val token = mUser.getToken()
        val userDomain = mUser.getUserDomain()

        if (token.isNullOrEmpty()) {
            return
        }

        HATAccountService().getHatTableValues(
            token,
            userDomain,
            namespace,
            scope,
            null,
            { json, newToken ->
                displayValues(json)
            }, { res ->
                displayToast(res.error.toString())
            }
        )
    }

    /*
    Display the result from the get request,
    Parsing the data with the HATParseManager
     */
    private fun displayValues(json: Json) {
        val names = HATParserManager().jsonToObjectList(json.content, HATBundleObject::class.java)

        var displayText = ""
        for (i in names.indices) {
            displayText += names[i].data.value + "\n"
        }
        jsonResultTxt.text = displayText
    }

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun logout() {
        mEncryptionServices.removeMasterKey()
        mPreference.deletePreference()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}
