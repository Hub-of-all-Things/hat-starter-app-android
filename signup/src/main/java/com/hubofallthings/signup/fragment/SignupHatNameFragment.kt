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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hubofallthings.signup.R
import com.hubofallthings.signup.activity.SignupActivity
import com.hubofallthings.signup.helpers.HATNetworkHelper
import com.hubofallthings.signup.helpers.SignupHatNameHelper
import com.hubofallthings.signup.services.SignupServices
import kotlinx.android.synthetic.main.create_account_username.*

class SignupHatNameFragment : Fragment() {
    private val TAG = SignupHatNameFragment::class.java.simpleName
    lateinit var mHATNetworkHelper: HATNetworkHelper
    var snackbar: Snackbar? = null
    lateinit var mSignupServices: SignupServices

    companion object {
        fun newInstance() = SignupHatNameFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(com.hubofallthings.signup.R.layout.create_account_username, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mHATNetworkHelper = HATNetworkHelper(context !!)
        mSignupServices = SignupServices()

        createUsernameEt.addTextChangedListener(getTextWatcher())
        nextCreateUsernameV2Btn.setOnClickListener { nextButton() }
        back_button_create_account_username.setOnClickListener { backButton() }
        (activity as SignupActivity).setOnBackClickListener(object :
            SignupActivity.OnBackClickListener {
            override fun onBackClick(): Boolean {
                backButton()
                return true
            }
        })
        addValues()
        cancelUsernameBtn.setOnClickListener {
            activity?.finish()
            activity?.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
        }
    }

    private fun addValues() {
        createUsernameEt?.setText(SignupActivity.mCreateObject.userName)
    }

    private fun successfulCallBack() {
        if (activity != null) {
            SignupActivity.mCreateObject.userName = createUsernameEt?.text?.toString()?.trim()

            activity?.supportFragmentManager?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )
                ?.replace(R.id.container, SignupWebFragment.newInstance())
                ?.commitNow()
        }
    }

    private fun failCallBack(error: String) {
        enableButton()
        val errorMsg = activity?.findViewById<LinearLayout>(R.id.errorIncl)
        val errorTxt = activity?.findViewById<TextView>(R.id.errorTxt)
        errorMsg?.visibility = View.VISIBLE
        errorMsg?.alpha = 0.0f
        errorMsg?.animate()?.alpha(1.0f)?.duration = 500
        errorTxt?.text = error
        createUsernameEt?.background = resources.getDrawable(R.drawable.edittext_error, null)
        createUsernameEt?.setTextColor(ContextCompat.getColor(context !!, R.color.error_border))
    }

    private fun getTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                checkValues(charSequence.toString())
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
    }

    private fun checkValues(text: String) {
        if (activity != null) {
            val errorMsg = activity?.findViewById<LinearLayout>(R.id.errorIncl)
            errorMsg?.visibility = View.GONE
            createUsernameEt?.background = resources.getDrawable(R.color.white_color, null)
            createUsernameEt?.setTextColor(ContextCompat.getColor(context !!, R.color.createViewDarkBlue))
        }
    }

    private fun nextButton() {
        if (mHATNetworkHelper.isNetworkAvailable()) {
//            disableButton()
            val userNameAddress = createUsernameEt?.text.toString()
            if (checkUsernameValues(userNameAddress)) {
                mSignupServices.validateHAT(
                    userNameAddress.toLowerCase(),
                    { _ -> successfulCallBack() },
                    { _ -> failCallBack("HAT with such username already exists") })
            } else {
                val msg = SignupHatNameHelper().urlDetailedError(userNameAddress)
                snackbar = Snackbar.make(
                    activity !!.findViewById(R.id.container),
                    msg,
                    Snackbar.LENGTH_SHORT
                )
                if (snackbar != null) {
                    snackbar?.show()
                }
            }
        } else {
            snackbar =
                Snackbar.make(activity !!.findViewById(R.id.container), "No internet connection", Snackbar.LENGTH_SHORT)
            if (snackbar != null) {
                snackbar?.show()
            }
        }
    }

    private fun backButton() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.exit_to_right,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            ?.replace(R.id.container, SignupEmailFragment.newInstance())
            ?.commitNow()
    }

    private fun enableButton() {
        nextCreateUsernameV2Btn.isEnabled = true
        nextCreateUsernameV2Btn.background = ContextCompat.getDrawable(context !!, R.drawable.button_enabled_rounded)
    }

    private fun disableButton() {
        nextCreateUsernameV2Btn.isEnabled = false
        nextCreateUsernameV2Btn.background = ContextCompat.getDrawable(context !!, R.drawable.button_disabled_rounded)
    }

    private fun checkUsernameValues(value: String): Boolean {
        return SignupHatNameHelper().checkUserNameValues(value)
    }
}