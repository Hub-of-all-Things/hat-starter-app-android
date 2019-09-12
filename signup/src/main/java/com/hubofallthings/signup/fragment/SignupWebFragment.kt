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

package com.hubofallthings.signup.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.hubofallthings.signup.R
import com.hubofallthings.signup.activity.SignupActivity
import com.hubofallthings.signup.helpers.HATNetworkHelper
import com.hubofallthings.signup.objects.SignupNextStepState
import com.hubofallthings.signup.services.SignupWebServices

class SignupWebFragment : Fragment() {
    private val TAG = SignupWebFragment::class.java.simpleName
    lateinit var mHATNetworkHelper: HATNetworkHelper
    lateinit var mWebServices: SignupWebServices

    val mUserAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.2228.0 Safari/537.36"

    companion object {
        fun newInstance() = SignupWebFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.signup_web_activity, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mWebServices = SignupWebServices()
        val mWebView = activity?.findViewById<WebView>(R.id.signupWebView)
        val backBtn = activity?.findViewById<ImageView>(R.id.back_button_create_account_web)

        val webSettings = mWebView?.settings
        webSettings?.userAgentString = mUserAgent
        webSettings?.setJavaScriptEnabled(true)

        val signup = SignupActivity.mCreateObject

        mWebView?.clearCache(true)

        mWebView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // here you can assign parameter "url" to some field in class

                val url2 = Uri.parse(url)

                val paramNames = url2.queryParameterNames
                for (key in paramNames) {
                    val value = url2.getQueryParameter(key)

                    if (key == "token" && value != "") {
                        signup.nextStep = SignupNextStepState.Success
                        signup.token = value
                        nextStep()
                    }

                    if (key == "error" && value != "") {
                        signup.nextStep = SignupNextStepState.Failed
                        nextStep()
                    }
                }
                // return true if you want to block redirection, false otherwise
                return true
            }
        }

        mWebView?.loadUrl(mWebServices.getHatSignupURL())

        (activity as SignupActivity).setOnBackClickListener(object :
            SignupActivity.OnBackClickListener {
            override fun onBackClick(): Boolean {
                backButton()
                return true
            }
        })

        backBtn?.setOnClickListener {
            backButton()
        }
    }

    private fun backButton() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.exit_to_right,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            ?.replace(R.id.container, SignupHatNameFragment.newInstance())
            ?.commitNow()
    }

    fun nextStep() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_right,
                R.anim.exit_to_left
            )
            ?.replace(R.id.container, SignupDone.newInstance())
            ?.commitNow()
    }
}