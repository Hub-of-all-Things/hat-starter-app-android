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

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.hubofallthings.signup.R
import com.hubofallthings.signup.activity.SignupActivity
import com.nimbusds.jwt.JWTParser

class DialogSignupSuccess : DialogFragment() {
    private val TAG = DialogSignupSuccess::class.java.simpleName
    companion object {
        fun newInstance(token: String?): DialogSignupSuccess {
            val frag = DialogSignupSuccess()
            val args = Bundle()
            args.putString("token", token)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater = LayoutInflater.from(activity)
        val promptView = layoutInflater.inflate(R.layout.signup_dialog_success, null)
        val builder = AlertDialog.Builder(activity)
        val loginBtn = promptView.findViewById<Button>(R.id.signupSuccessLoginBtn)
        val userDomainTxt = promptView.findViewById<TextView>(R.id.signupSuccessHatDomain)
        val token: String? = SignupActivity.mCreateObject.token
        var userName: String? = null

        try {
            val parsedToken = JWTParser.parse(token)
            userName = parsedToken.jwtClaimsSet.getClaim("iss").toString()

            userDomainTxt.text = userName
        } catch (e: Exception) { }


        loginBtn.setOnClickListener {
            if (activity != null) {
                if (!token.isNullOrEmpty() && !userName.isNullOrEmpty()) {
                    Log.i(TAG, "success")
                    signInSuccess(userName, token)
                } else {
                    Log.i(TAG, "fail")

                    signInFail()
                }
            }
        }

        builder.setView(promptView)
        return builder.create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        activity?.finish()
    }

    private fun signInSuccess(userDomain: String?, newToken: String?) {
        val resultIntent = Intent()
        resultIntent.putExtra("newToken", newToken)
        resultIntent.putExtra("userDomain", userDomain)

        activity?.setResult(RESULT_OK, resultIntent)
        activity?.finish()
    }

    // failed result from signup to HAT , go to Main
    private fun signInFail() {
        activity?.setResult(Activity.RESULT_CANCELED)
        activity?.finish()
    }
}