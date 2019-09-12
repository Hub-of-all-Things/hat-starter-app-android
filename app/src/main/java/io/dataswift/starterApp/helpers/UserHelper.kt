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

package io.dataswift.starterApp.helpers

import android.content.Context
import android.content.Intent
import android.os.Build
import com.hubofallthings.notables.encryption.KeyStoreWrapper
import com.nimbusds.jwt.JWTParser
import io.dataswift.starterApp.activity.DashboardActivity
import io.dataswift.starterApp.activity.MainActivity
import io.dataswift.starterApp.services.EncryptionServices
import io.dataswift.starterApp.services.MyPreferences
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.SecretKey

class UserHelper(private val context: Context) {
    private val mPreference = MyPreferences(context)

    fun getUserDomain(): String {
        return mPreference.getUserDomain() ?: ""
    }

    private fun getLoginStatus(): Boolean {
        return mPreference.getLoginStatus()
    }

    fun getToken(): String? {
        if (getLoginStatus()) {
            val encryptedToken = mPreference.getToken()
            val masterKey = getMasterKey()
            if (!encryptedToken.isNullOrEmpty() && encryptedToken.length > 5 && masterKey != null) {
                return EncryptionServices(context).decrypt(encryptedToken, null)
            } else {
                goForLogin()
            }
        }
        return null
    }

    private fun goForLogin() {
        mPreference.setLoginStatus(false)
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(intent)
    }

    fun login() {
        tokenExpiration()
    }

    // check if token is expired
    private fun tokenExpiration() {
        val token = getDecryptToken()
        if (! token.isNullOrEmpty()) {
            try {
                val parsedToken = JWTParser.parse(token)
                val tokenExpDate = parsedToken.jwtClaimsSet.expirationTime
                parsedToken.jwtClaimsSet.getClaim("iss")

                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.getDefault())
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val result = formatter.format(Date())
                val currentDate = formatter.parse(result)
                // check if token expired
                if (currentDate < tokenExpDate) {
                    val intent = Intent(context, DashboardActivity::class.java)
                    context.startActivity(intent)
                }
            } catch (e: Exception) {
            }
        }
    }

    // encrypt the newToken and store it to Preference
    private fun encryptToken(token: String?) {
        val mEncryptionServices = EncryptionServices(context)
        mEncryptionServices.createMasterKey(null)
        val encryptedToken = mEncryptionServices.encrypt(token, null)
        mPreference.setToken(encryptedToken)
    }

    // get decrypted token
    private fun getDecryptToken(): String? {
        return getToken()
    }

    fun getMasterKey(): SecretKey? {
        val DEFAULT_KEY_STORE_NAME = EncryptionServices.DEFAULT_KEY_STORE_NAME
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return KeyStoreWrapper(
                context,
                DEFAULT_KEY_STORE_NAME
            ).getAndroidKeyStoreSymmetricKey(EncryptionServices.MASTER_KEY)
        } else {
            return KeyStoreWrapper(
                context,
                DEFAULT_KEY_STORE_NAME
            ).getDefaultKeyStoreSymmetricKey(EncryptionServices.MASTER_KEY, "")
        }
    }
}