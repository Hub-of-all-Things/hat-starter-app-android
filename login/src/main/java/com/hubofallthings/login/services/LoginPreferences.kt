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

import android.content.Context

class LoginPreferences(private val context: Context) {
    val PREFERENCE_NAME = "LoginPreference"
    val PREFERENCE_HATDOMAIN = "hatdomain"
    val PREFERENCE_USERDOMAIN = "userdomain"
    val PREFERENCE_HATNAME = "hatname"

    val pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getUserDomain(): String? {
        return pref.getString(PREFERENCE_USERDOMAIN, "")
    }

    fun setUserDomain(userDomain: String?) {
        val editor = pref.edit()
        editor.putString(PREFERENCE_USERDOMAIN, userDomain)
        editor.apply()
    }
}