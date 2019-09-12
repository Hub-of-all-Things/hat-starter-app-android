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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hubofallthings.signup.R
import com.hubofallthings.signup.activity.SignupActivity
import com.hubofallthings.signup.helpers.HATNetworkHelper
import com.hubofallthings.signup.objects.SignupNextStepState
import com.hubofallthings.signup.services.SignupServices

class SignupDone : Fragment() {
    private val TAG = SignupDone::class.java.simpleName
    lateinit var mHATNetworkHelper: HATNetworkHelper
    var snackbar: Snackbar? = null
    lateinit var mSignupServices: SignupServices

    companion object {
        fun newInstance() = SignupDone()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.create_account_loading, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mHATNetworkHelper = HATNetworkHelper(context !!)
        mSignupServices = SignupServices()

        if (savedInstanceState == null) {

            if (SignupActivity.mCreateObject.nextStep == SignupNextStepState.Success) {
                showSuccessDialog()
            }

            if (SignupActivity.mCreateObject.nextStep == SignupNextStepState.Failed) {
                showErrorDialog()
            }
        }
    }

    private fun showSuccessDialog() {
        val fm = activity?.supportFragmentManager
        val successDialogFragment = DialogSignupSuccess.newInstance(SignupActivity.mCreateObject.token)
        successDialogFragment.show(fm, "fragment_success")
    }

    private fun showErrorDialog() {
        val fm = activity?.supportFragmentManager
        val errorDialogFragment = DialogSignupError.newInstance()
        errorDialogFragment.show(fm, "fragment_success")
    }

}