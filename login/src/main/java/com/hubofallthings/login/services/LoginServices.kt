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
import android.util.Log
import com.hubofallthings.BuildConfig
import com.hubofallthings.android.hatApi.managers.HATNetworkManager
import com.hubofallthings.android.hatApi.services.HATLoginService
import com.hubofallthings.android.hatApi.services.LoginLookupError
import com.hubofallthings.android.hatApi.services.LoginLookupSuccess
import com.hubofallthings.login.helpers.AuthenticationHelper

class LoginServices(private val activity: Activity) {
    private val baseUrl = AuthenticationHelper.authentication.baseUrl
    val myPreference = LoginPreferences(activity)

    fun setUserDomain(userDomain: String?) {
        myPreference.setUserDomain(userDomain)
    }

    fun getUserDomain(): String? {
        return myPreference.getUserDomain()
    }

    fun resolveUrl(
        username: String,
        successfulCallBack: (LoginLookupSuccess) -> Unit,
        failCallBack: (LoginLookupError) -> Unit
    ) {

        HATLoginService().hatLookup(
            baseUrl,
            username = username,
            sandbox = isSandbox(),
            successfulCallBack = { s -> successfulCallBack(s) },
            failCallBack = { e1 -> failCallBack(e1) }
        )
    }

    fun resolveEmail(
        email: String,
        successfulCallBack: (LoginLookupSuccess) -> Unit,
        failCallBack: (LoginLookupError) -> Unit
    ) {

        HATLoginService().hatLookup(
            baseUrl,
            email = email,
            sandbox = isSandbox(),
            successfulCallBack = { s -> successfulCallBack(s) },
            failCallBack = { e -> failCallBack(e) })
    }

    private fun isSandbox(): Boolean {
        return true
    }
}
