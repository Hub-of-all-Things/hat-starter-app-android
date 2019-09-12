/*
 * *
 *  * Copyright (C) 2018-2019 DataSwift Ltd
 *  *
 *  * SPDX-License-Identifier: MPL2
 *  *
 *  * This file is part of the Hub of All Things project (HAT).
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/
 *
 */

package com.hubofallthings.login.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hubofallthings.R
import com.hubofallthings.android.hatApi.managers.HATNetworkManager
import com.hubofallthings.android.hatApi.services.LoginLookupError
import com.hubofallthings.android.hatApi.services.LoginLookupSuccess
import com.hubofallthings.login.helpers.AdjustViewWithKeyboard
import com.hubofallthings.login.helpers.AuthenticationHelper
import com.hubofallthings.login.helpers.HATNetworkHelper
import com.hubofallthings.login.services.LoginServices
import com.hubofallthings.login.helpers.MakeLinksHelper
import com.hubofallthings.login.model.AuthenticationModel
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.android.synthetic.main.login_activity_v2_1.back_button_login
import kotlinx.android.synthetic.main.login_activity_v2_1.hatNameEt
import kotlinx.android.synthetic.main.login_activity_v2_1.nextLoginBtn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Created by Myteletsis Eleftherios on 19/06/2019.
 */
class LoginActivity : AppCompatActivity(), View.OnClickListener, CoroutineScope {
    private val loginWeb = 166
    private val OPEN_SIGNUP = 1155533
    private var snackbar: Snackbar? = null
    private lateinit var mLoginServices: LoginServices
    private lateinit var mHATNetworkHelper: HATNetworkHelper
    private var state = LoginActivityStates.Url
    private var hatUrlName = ""
    private var hatUsername = ""
    private var hatEmail = ""

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        AdjustViewWithKeyboard.assistActivity(this)
        mHATNetworkHelper = HATNetworkHelper(this)
        mLoginServices = LoginServices(this)
        nextLoginBtn.setOnClickListener(this)
        back_button_login.setOnClickListener(this)
        loginChangeLoginStateBtn.setOnClickListener(this)

        Log.i("LoginActivity", AuthenticationHelper.authentication.toString())

        if (intent.extras != null) {
            val user = intent.extras?.getString("newUserDomain")
            if (! user.isNullOrEmpty()) {
                goLogin(user)
            }
        }

        hatLinksInit(loginSignupNewAccount)

        hatNameEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                removeErrorBanner()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                hatUsername = s.toString().trim()
            }
        })

        hatEmailEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                removeErrorBanner()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                hatEmail = s.toString().trim()

                hatEmailCheckValues(hatEmail)
            }
        })
    }

    private fun hatNameCheckValues(hatUrl: String) {
        disableNextBtn()
        nextLoginBtn.text = getString(R.string.logging_in)

        mLoginServices.resolveUrl(hatUrl, { result ->
            hatUrlName = "${result.hatName}.${result.hatCluster}"
            hatDomainEt.setText(result.hatCluster)

            launch {
                delay(1000)

                goLogin(hatUrlName)
            }

        }, {
            Log.i("LoginError", it.toString())
            hatUrlName = ""
            hatDomainEt.setText(getString(R.string.yourhatdomain))
            nextLoginBtn.text = getString(R.string.next)

            displayErrorBanner("Sorry this name is not recognised")
            enableNextBtn()
        })
    }

    private fun hatEmailCheckValues(email: String) {
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            enableNextBtn()
        } else {
            disableNextBtn()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.nextLoginBtn -> {
                nextLoginBtn()
            }
            R.id.back_button_login -> {
                setResult(Activity.RESULT_CANCELED).also {
                    finish()
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
                }
            }

            R.id.loginChangeLoginStateBtn -> {
                changeState()
            }
        }
    }

    private fun nextLoginBtn() {
        disableNextBtn()
        nextLoginBtn.text = getString(R.string.logging_in)

        if (state == LoginActivityStates.Email) {
            loginWithEmail()
        } else {
            loginWithUrl()
        }
    }

    private fun loginWithEmail() {
        mLoginServices.resolveEmail(hatEmail.toLowerCase(), { result ->
            hatUrlName = "${result.hatName}.${result.hatCluster}"

            goLogin(hatUrlName)
        }, {
            hatUrlName = ""
            displayErrorBanner("The email is not recognised")
        })
    }

    private fun hatLinksInit(terms: TextView) {
        val createOneSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                setResult(OPEN_SIGNUP).also {
                    finish()
                }
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false // set to false to remove underline
                ds.color = ContextCompat.getColor(applicationContext, R.color.colorButtonEnabled)
            }
        }
        MakeLinksHelper().makeLinks(
            terms,
            arrayOf("Create one"),
            arrayOf(createOneSpan)
        )
    }

    private fun loginWithUrl() {
        if (hatUsername.isNotEmpty() && hatUsername.length > 3 && hatUsername.length < 22) {
            Log.i("LoginError", hatUsername.toString())
            hatNameCheckValues(hatUsername.toLowerCase())
        } else {
            displayErrorBanner("The HAT name is not valid")
        }
    }

    private fun changeState() {
        state = if (state == LoginActivityStates.Url) {
            LoginActivityStates.Email
        } else {
            LoginActivityStates.Url
        }

        removeErrorBanner()

        hatUrlName = ""
        enableNextBtn()
        hatDomainEt.setText(getString(R.string.yourhatdomain))

        when (state) {
            LoginActivityStates.Url -> {
                loginEmailLayout.visibility = View.GONE
                loginUrlLayout.visibility = View.VISIBLE
                loginSubTxt.visibility = View.VISIBLE
                loginChangeLoginStateBtn.text = getString(R.string.log_in_with_email)
                hatNameLogin.text = getString(R.string.log_in_with_hat_url)
            }
            LoginActivityStates.Email -> {
                loginEmailLayout.visibility = View.VISIBLE
                loginUrlLayout.visibility = View.GONE
                loginSubTxt.visibility = View.INVISIBLE
                loginChangeLoginStateBtn.text = getString(R.string.log_in_with_hat_url).toUpperCase()
                hatNameLogin.text = getString(R.string.login_in_with_your_email)
            }
        }
    }

    private fun goLogin(userDomain: String) {
        if (mHATNetworkHelper.isNetworkAvailable()) {
            nextLoginBtn.isEnabled = false
            successfulCallBack(userDomain)
        } else {
            snackbar = Snackbar.make(findViewById(R.id.scrollView2), "No internet connection", Snackbar.LENGTH_SHORT)
            if (snackbar != null) {
                snackbar?.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            loginWeb -> {
                setResult(resultCode, data)
                finish()
            }
        }
    }

    private fun successfulCallBack(userDomain: String) {
        mLoginServices.setUserDomain(userDomain)
        nextLoginBtn.isEnabled = true

        val intent = Intent(this, WebActivity::class.java)
        startActivityForResult(intent, loginWeb)
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }

    private fun displayErrorBanner(errorText: String) {
        enableNextBtn()
        val errorMsg = findViewById<LinearLayout>(R.id.errorIncl)
        val errorTxt = findViewById<TextView>(R.id.errorTxt)
        errorMsg?.visibility = View.VISIBLE
        errorMsg?.alpha = 0.0f
        errorMsg?.animate()?.alpha(1.0f)?.duration = 500
        errorTxt?.text = errorText
    }

    private fun removeErrorBanner() {
        val errorMsg = findViewById<LinearLayout>(R.id.errorIncl)
        errorMsg?.visibility = View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED).also {
            finish()
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
        }
    }

    private fun enableNextBtn() {
        nextLoginBtn.isEnabled = true
        nextLoginBtn.background = ContextCompat.getDrawable(this, R.drawable.button_enabled_rounded)
        nextLoginBtn.text = getString(R.string.next)
    }

    private fun disableNextBtn() {
        nextLoginBtn.isEnabled = false
        nextLoginBtn.background = ContextCompat.getDrawable(this, R.drawable.button_disabled_rounded)
    }
}

enum class LoginActivityStates {
    Url, Email
}