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

package com.hubofallthings.signup.services

import com.hubofallthings.android.hatApi.services.HATSignupService
import com.hubofallthings.android.hatApi.services.SignupValidationError
import com.hubofallthings.android.hatApi.services.SignupValidationSuccess
import com.hubofallthings.signup.BuildConfig
import com.hubofallthings.signup.objects.SignupConfiguration

class SignupServices {
    companion object {
        lateinit var signupConfiguration: SignupConfiguration
    }

    fun validateHAT(
        username: String,
        successfulCallBack: (SignupValidationSuccess) -> Unit,
        failCallBack: (SignupValidationError) -> Unit
    ) {

        HATSignupService().validateHATAddress(
            signupConfiguration.validationBaseUrl,
            username,
            isSandbox(),
            { s -> successfulCallBack(s) },
            { e -> failCallBack(e) })
    }

    fun validateEmailAddress(
        email: String,
        successfulCallBack: (SignupValidationSuccess) -> Unit,
        failCallBack: (SignupValidationError) -> Unit
    ) {

        HATSignupService().validateEmailAddress(
            signupConfiguration.validationBaseUrl,
            email,
            isSandbox(),
            { s -> successfulCallBack(s) },
            { e1 -> failCallBack(e1) })
    }

    private fun isSandbox(): Boolean {
        return true
    }
}