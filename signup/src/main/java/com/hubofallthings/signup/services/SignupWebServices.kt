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

import com.hubofallthings.signup.activity.SignupActivity

class SignupWebServices {

    fun getHatSignupURL(): String {
        val signup = SignupActivity.mCreateObject
        val userName = signup.userName
        val email = signup.email
        val optins = signup.optins
        val applicationId = getApplicationId()

        return "https://hatters.hubofallthings.com/services/baas/signup?email=$email&hat_name=$userName&application_id=$applicationId&newsletter_optin=$optins&redirect_uri=notables://signup"
    }

    private fun getApplicationId(): String {
        return SignupServices.signupConfiguration.applicationId
    }
}