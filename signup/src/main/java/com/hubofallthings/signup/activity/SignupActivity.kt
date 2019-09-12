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

package com.hubofallthings.signup.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hubofallthings.signup.R
import com.hubofallthings.signup.fragment.SignupEmailFragment
import com.hubofallthings.signup.helpers.AdjustViewWithKeyboard
import com.hubofallthings.signup.objects.CreateAccountObject

class SignupActivity : AppCompatActivity() {
    companion object {
        var mCreateObject = CreateAccountObject()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account_activity)
        mCreateObject = CreateAccountObject()
        AdjustViewWithKeyboard.assistActivity(this)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SignupEmailFragment.newInstance())
                .commitNow()
        }
    }

    interface OnBackClickListener {
        fun onBackClick(): Boolean
    }

    private var onBackClickListener: OnBackClickListener? = null

    fun setOnBackClickListener(onBackClickListener: OnBackClickListener) {
        this.onBackClickListener = onBackClickListener
    }

    override fun onBackPressed() {
        if (onBackClickListener != null && onBackClickListener !!.onBackClick()) {
            return
        }
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }
}