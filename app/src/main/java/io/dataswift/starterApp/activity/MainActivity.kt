package io.dataswift.starterApp.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hubofallthings.login.activity.LoginActivity
import com.hubofallthings.login.helpers.AuthenticationHelper
import com.hubofallthings.login.model.AuthenticationModel
import com.hubofallthings.signup.activity.SignupActivity
import com.hubofallthings.signup.objects.SignupConfiguration
import com.hubofallthings.signup.services.SignupServices
import io.dataswift.starterApp.R
import io.dataswift.starterApp.helpers.AppConfig
import io.dataswift.starterApp.helpers.UserHelper
import io.dataswift.starterApp.services.EncryptionServices
import io.dataswift.starterApp.services.MyPreferences
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val LOGIN = 352
    private val SIGNUP = 125
    private val OPEN_SIGNUP = 555
    private lateinit var myPreference: MyPreferences
    private lateinit var mEncryptionServices: EncryptionServices
    private lateinit var myUser: UserHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myPreference = MyPreferences(this)
        mEncryptionServices = EncryptionServices(this)
        loginButton.setOnClickListener(this)
        signupButton.setOnClickListener(this)
        myUser = UserHelper(this)

        val masterKey = myUser.getMasterKey()

        if (myPreference.getLoginStatus() && masterKey != null) {
            myUser.login()
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.loginButton -> {
                openLogin()
            }
            R.id.signupButton -> {
                openSignup()
            }
        }
    }

    /*
        When the result is Activity.RESULT_OK, both Login and Signup modules return a token and a userDomain.

     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOGIN, SIGNUP -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data?.let {
                            val extras = data.extras
                            val token = extras?.getString("newToken")
                            val userDomain = extras?.getString("userDomain")
                            success(userDomain, token)
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                    }
                    OPEN_SIGNUP -> {
                        openSignup()
                    }
                }
            }
        }
    }

    /*
        Open the login module. In order to make it work we have to specify the configuration with our
        applicationId and the baseUrl to make a look up for the hat name or the email.
     */
    private fun openLogin() {
        val auth = AuthenticationModel(
            applicationId = AppConfig.applicationId,
            baseUrl = AppConfig.authBaseUrl
        )

        AuthenticationHelper.authentication = auth
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, LOGIN)
    }

    /*
        Open the Signup module. In order to make it work we have to specify the configuration with our
        applicationId and the baseUrl to make the validation for the hat name and the email.
     */
    private fun openSignup() {
        val signup = SignupConfiguration(
            applicationId = AppConfig.applicationId,
            validationBaseUrl = AppConfig.signupBaseUrl
        )

        SignupServices.signupConfiguration = signup
        val intent = Intent(this, SignupActivity::class.java)
        startActivityForResult(intent, SIGNUP)
    }

    /*
        Success result from the activity result. For both login and signup we have store
        the user domain and the token, and navigate the user to the main dashboard.
    */
    private fun success(userDomain: String?, newToken: String?) {
        encryptToken(newToken)
        val intent = Intent(this, DashboardActivity::class.java)
        myPreference.setUserDomain(userDomain)
        myPreference.setLoginStatus(true)
        startActivity(intent)
        finish()
    }

    // encrypt the newToken and store it to Preference
    private fun encryptToken(token: String?) {
        mEncryptionServices.createMasterKey(null)
        val encryptedToken = mEncryptionServices.encrypt(token, null)
        myPreference.setToken(encryptedToken)
    }
}
