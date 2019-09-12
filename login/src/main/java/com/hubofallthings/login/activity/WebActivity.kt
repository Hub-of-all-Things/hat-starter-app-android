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
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.hubofallthings.R
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.android.hatApi.services.HATService
import com.hubofallthings.login.services.LoginPreferences
import com.hubofallthings.login.services.WebServices

class WebActivity : AppCompatActivity() {
    lateinit var mPreference: LoginPreferences
    lateinit var mWebServices: WebServices
    private var newUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_activity)

        mPreference = LoginPreferences(this)
        mWebServices = WebServices(this)
        val mWebView = findViewById<WebView>(R.id.webView)

        val webSettings = mWebView.settings
        webSettings.setJavaScriptEnabled(true)
        val userDomain = mPreference.getUserDomain()

        mWebView.clearCache(true)

        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // here you can assign parameter "url" to some field in class
                if (url == "notables://loginfailed") {
                    finish()
                }
                val url2 = Uri.parse(url)
                val paramNames = url2.queryParameterNames
                for (key in paramNames) {
                    val value = url2.getQueryParameter(key)
                    if (value != "") {
                        newUrl = url
                        loginToHAT()
                    }
                }
                // return true if you want to block redirection, false otherwise
                return true
            }
        }
        if (userDomain != null)
            mWebView.loadUrl(mWebServices.getHatLoginURL(userDomain))
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    // login to HAT authorization
    private fun loginToHAT() {
        val appName = WebServices(this).getServiceNameValue()
        HATService().loginToHATAuthorization(appName,
            newUrl,
            { userDomain, newToken -> success(userDomain, newToken) },
            { error1 -> failed(error1) })
    }

    // success result from login to HAT , go to DrawerActivity
    fun success(userDomain: String?, newToken: String?) {
        mWebServices.signInSuccess(userDomain, newToken)
    }

    // failed result from login to HAT , go to LoginActivity
    fun failed(error: HATError) {
        mWebServices.signInFail(error)
    }
}