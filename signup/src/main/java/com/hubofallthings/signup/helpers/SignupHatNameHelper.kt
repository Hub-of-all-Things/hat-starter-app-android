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

package com.hubofallthings.signup.helpers

class SignupHatNameHelper {
    private val HAT_URL_REGEX = "[a-z][a-z0-9]{2,19}[a-z0-9]".toRegex()
    private val SPECIAL_CHARS_REGEX = "^[^<>{}\"/|;:.,~!?@#$%^=&*\\]\\\\()\\[¿§«»ω⊙¤°℃℉€¥£¢¡®©0-9_+]*$".toRegex()
    private val NUMBER_FIRST_CHAR_REGEX = "^[a-zA-Z].*".toRegex()

    fun checkUserNameValues(name: String): Boolean {
        return name.matches(HAT_URL_REGEX)
    }

    fun urlDetailedError(url: String): String {

        return if (url.length < 4 || url.length > 21) {
            "The name must be between 4 to 21 characters."
        } else if (url !== url.toLowerCase()) {
            "The name cannot contain uppercase letters."
        } else if (url.matches(SPECIAL_CHARS_REGEX)) {
            "The name cannot contain special characters. (eg.  - _ # % /)"
        } else if (url.isNotEmpty() && !url[0].isLetter()) {
            "The name must start with a letter."
        } else {
            "The format of the name is incorrect."
        }
    }
}