package io.dataswift.starterApp.services

/**
 * Copyright (C) 2018-2019 DataSwift Ltd
 *
 * SPDX-License-Identifier: MPL2
 *
 * This file is part of the Hub of All Things project (HAT).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/
 */

import android.content.Context

class MyPreferences(private val context: Context) {
    private val PREFERENCE_NAME = "MyPreferences"
    private val PREFERENCE_TOKEN = "token"
    private val PREFERENCE_LOGIN = "login"
    private val PREFERENCE_USERDOMAIN = "userdomain"

    private val pref = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE)

    fun deletePreference() {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
    fun getToken(): String? {
        return pref.getString(PREFERENCE_TOKEN, "")
    }
    fun setToken(token: String?) {
        val editor = pref.edit()
        editor.putString(PREFERENCE_TOKEN,token)
        editor.apply()
    }
    fun getUserDomain(): String? {
        return pref.getString(PREFERENCE_USERDOMAIN, "")
    }
    fun setUserDomain(userDomain: String?) {
        val editor = pref.edit()
        editor.putString(PREFERENCE_USERDOMAIN,userDomain)
        editor.apply()
    }

    fun getLoginStatus(): Boolean {
        return pref.getBoolean(PREFERENCE_LOGIN, false)
    }
    fun setLoginStatus(status: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(PREFERENCE_LOGIN, status)
        editor.apply()
    }
}
