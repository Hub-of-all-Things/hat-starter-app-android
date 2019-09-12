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

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.hubofallthings.signup.R



class DialogSignupError : DialogFragment() {
    companion object {
        fun newInstance(): DialogSignupError {
            val frag = DialogSignupError()
            return frag
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater = LayoutInflater.from(activity)
        val promptView = layoutInflater.inflate(R.layout.signup_dialog_error, null)
        val builder = AlertDialog.Builder(activity)
        val okBtn = promptView.findViewById<Button>(R.id.signupErrorOkBtn)

        okBtn.setOnClickListener {
            if (activity != null) {
                activity?.finish()
            }
            dismiss()
        }

        builder.setView(promptView)
        return builder.create()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        activity?.finish()
    }

}