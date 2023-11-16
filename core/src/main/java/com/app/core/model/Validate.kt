package com.app.core.model


/**
 * Created by JJ date on 11/10/2023.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
interface Validate<T> {

    fun isValidate() : T

    fun isEmpty(text: String?) = text.isNullOrEmpty()

    fun isMinLength(text: String?, min: Int) = (text?.length ?: 0) < min

    fun isMaxLength(text: String?, max: Int) = (text?.length ?: 0) > max

}