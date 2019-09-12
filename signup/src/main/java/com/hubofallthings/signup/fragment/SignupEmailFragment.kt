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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hubofallthings.signup.R
import com.hubofallthings.signup.activity.SignupActivity
import com.hubofallthings.signup.helpers.HATNetworkHelper
import com.hubofallthings.signup.helpers.MakeLinksHelper
import com.hubofallthings.signup.services.SignupServices
import kotlinx.android.synthetic.main.create_account_email.*

class SignupEmailFragment : Fragment() {
    private val TAG = SignupEmailFragment::class.java.simpleName
    lateinit var mHATNetworkHelper: HATNetworkHelper
    var snackbar: Snackbar? = null
    lateinit var mSignupServices: SignupServices
    private var emailError = false

    companion object {
        fun newInstance() = SignupEmailFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.create_account_email, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mHATNetworkHelper = HATNetworkHelper(context !!)
        mSignupServices = SignupServices()

        createEmailEt.addTextChangedListener(getTextWatcher())
        nextCreateNameEmailBtn.setOnClickListener { nextButton() }
        back_button_create_account_email.setOnClickListener {
            activity?.finish()
            activity?.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
        }
        addValues()
        (activity as SignupActivity).setOnBackClickListener(object :
            SignupActivity.OnBackClickListener {
            override fun onBackClick(): Boolean {
                return false
            }
        })
        cancelMailBtn.setOnClickListener {
            activity?.finish()
            activity?.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
        }
        hatLinksInit(createEmailPrivacyPolicy)
    }

    private fun getTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                removeErrorBanner()
                emailError = false
            }
        }
    }

    private fun enableButton() {
        nextCreateNameEmailBtn.isEnabled = true
        nextCreateNameEmailBtn.background = ContextCompat.getDrawable(context !!, R.drawable.button_enabled_rounded)
    }

    private fun disableButton() {
        nextCreateNameEmailBtn.isEnabled = false
        nextCreateNameEmailBtn.background = ContextCompat.getDrawable(context !!, R.drawable.button_disabled_rounded)
    }

    private fun emailValidate(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun addValues() {
        createEmailEt.setText(SignupActivity.mCreateObject.email)
        checkBoxOptins.isChecked = SignupActivity.mCreateObject.optins
    }

    private fun nextButton() {
        val email = createEmailEt.text.toString().trim()

        if (mHATNetworkHelper.isNetworkAvailable()) {
            if (emailValidate(email)) {
                validEmailAddressHatters(email)
            } else {
                failCallBack("Email is not valid")
            }
        } else {
            snackbar =
                Snackbar.make(activity !!.findViewById(R.id.container), "No internet connection", Snackbar.LENGTH_SHORT)
            if (snackbar != null) {
                snackbar?.show()
            }
        }
    }

    private fun validEmailAddressHatters(email: String) {
        if (!emailError) {
            disableButton()
            mSignupServices.validateEmailAddress(
                email,
                { successfulCallBack() },
                {
                    failCallBack("A HAT with this email already exists.")
                    emailError = true
                    enableButton()
                }
            )
        }
    }

    private fun successfulCallBack() {
        if (activity != null) {
            SignupActivity.mCreateObject.email = createEmailEt?.text?.toString()?.trim()
            SignupActivity.mCreateObject.optins = checkBoxOptins.isChecked

            activity?.supportFragmentManager?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_right,
                    R.anim.exit_to_left
                )
                ?.replace(R.id.container, SignupHatNameFragment.newInstance())
                ?.commitNow()
        }
    }

    private fun failCallBack(errorText: String) {
        enableButton()
        val errorMsg = activity?.findViewById<LinearLayout>(R.id.errorIncl)
        val errorTxt = activity?.findViewById<TextView>(R.id.errorTxt)
        errorMsg?.visibility = View.VISIBLE
        errorMsg?.alpha = 0.0f
        errorMsg?.animate()?.alpha(1.0f)?.duration = 500
        errorTxt?.text = errorText
        createEmailEt.background = resources.getDrawable(R.drawable.edittext_error, null)
        createEmailEt.setTextColor(ContextCompat.getColor(context !!, R.color.error_border))
    }

    private fun removeErrorBanner() {
        val errorMsg = activity?.findViewById<LinearLayout>(R.id.errorIncl)
        errorMsg?.visibility = View.GONE

        createEmailEt.background = resources.getDrawable(R.color.white_color, null)
        createEmailEt.setTextColor(ContextCompat.getColor(context !!, R.color.createViewDarkBlue))
    }

    private fun hatLinksInit(terms: TextView) {
        val privacyClickSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                openPdf("Privacy Policy")
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false // set to false to remove underline
                ds.color = ContextCompat.getColor(context !!, R.color.colorButtonEnabled)
            }
        }
        MakeLinksHelper().makeLinks(
            terms,
            arrayOf("privacy policy"),
            arrayOf(privacyClickSpan)
        )
    }

    fun openPdf(link: String) {
        val url = if (link == "Terms of Service") {
            "https://cdn.dataswift.io/legal/hat-owner-terms-of-service.pdf"
        } else {
            "https://cdn.dataswift.io/legal/dataswift-privacy-policy.pdf"
        }
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        try {
            activity?.startActivity(i)
        } catch (e: Exception) {
            Toast.makeText(activity, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }
}