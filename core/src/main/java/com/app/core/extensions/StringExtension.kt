package com.app.core.extensions

import androidx.core.util.PatternsCompat


fun String?.isEmail() = this?.let { PatternsCompat.EMAIL_ADDRESS.matcher(it).matches() }?:false

fun String?.isUpperCase(): Boolean {
    if (this != null) {
        for (char in this.toCharArray()) {
            val isUpCase = Character.isUpperCase(char)
            if (isUpCase) return true
        }
    }
    return false
}

fun String?.isLowerCase(): Boolean {
    if (this != null) {
        for (char in this.toCharArray()) {
            val isLowerCase = Character.isLowerCase(char)
            if (isLowerCase) return true
        }
    }
    return false
}

fun String?.isDigit(): Boolean {
    if (this != null) {
        for (char in this.toCharArray()) {
            val isDigit = Character.isDigit(char)
            if (isDigit) return true
        }
    }
    return false
}


fun String?.isSpecialCharacter(): Boolean = this?.contains(Regex("[!#$%^&*.+_?@-]")) == true //!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~