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

package com.hubofallthings.login.services

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.widget.Toast
import com.hubofallthings.BuildConfig
import com.hubofallthings.android.hatApi.HATError
import com.hubofallthings.login.helpers.AuthenticationHelper

class WebServices(private var activity: Activity) {
    var urlScheme: String = AuthenticationHelper.authentication.applicationId
    var localAuthHost: String = AuthenticationHelper.authentication.applicationId

    // success result from login to HAT , go to DrawerActivity
    fun signInSuccess(userDomain: String?, newToken: String?) {
        val resultIntent = Intent()
        resultIntent.putExtra("newToken", newToken)
        resultIntent.putExtra("userDomain", userDomain)

        activity.setResult(RESULT_OK, resultIntent).also {
            activity.finish()
        }
    }

    // failed result from login to HAT , go to LoginActivity
    fun signInFail(error: HATError) {
        Toast.makeText(activity, error.errorMessage, Toast.LENGTH_LONG).show()
        activity.setResult(RESULT_CANCELED).also { activity.finish() }
    }

    fun getHatLoginURL(userDomain: String): String {
        val serviceName = getServiceNameValue()
        return "https://$userDomain/#/hatlogin?name=$serviceName&redirect=$urlScheme://$localAuthHost&fallback=notables://loginfailed"
    }

    fun getServiceNameValue(): String {
        return AuthenticationHelper.authentication.applicationId
    }
}