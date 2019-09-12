package io.dataswift.starterApp.model

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
data class HATBundleObject(
    val endpoint: String,
    val recordId: String,
    val data: ValueObject
)

data class ValueObject(
    val value: String
)