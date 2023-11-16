package com.jumadi.app.template.extension

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.common.ui.base.BaseActivity
import com.app.common.ui.extensions.gotoActon
import com.app.common.ui.extensions.showMessage
import com.app.common.ui.message.BottomSheetMessageFragment
import com.app.core.BuildConfig
import com.app.core.network.Resource
import com.app.core.network.response.GeneralResponse
import com.app.core.util.Util.queryName
import com.dika.app.antarmoda.ui.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by Anonim date on 26/08/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/


fun FragmentActivity.showGeneralError(callbackAction: (() -> Unit)? = null) : BottomSheetMessageFragment {
    return showMessage(R.drawable.img_general_error.toString(), getString(R.string.title_error_general), getString(R.string.description_error_general), callbackAction)
}

fun Fragment.showGeneralError(callbackAction: (() -> Unit)? = null) : BottomSheetMessageFragment {
    return showMessage(R.drawable.img_general_error.toString(), getString(R.string.title_error_general), getString(R.string.description_error_general), callbackAction)
}

fun FragmentActivity.showGeneralCodeError(generalResponse: GeneralResponse, callbackAction: (() -> Unit)? = null) : BottomSheetMessageFragment {
    Log.e("showGeneralCodeError", "$generalResponse ${this::class.java.simpleName}")
    return when (generalResponse.status) {
        403, 401 -> {
            if (this is BaseActivity) {
                if (BuildConfig.DEFAULT_ACTIVITY.endsWith(this::class.java.simpleName, ignoreCase = true)) {
                    showMessage(R.drawable.img_error_load_page.toString(), getString(R.string.title_error_general), generalResponse.message ?: getString(R.string.description_error_general), callbackAction)
                } else showMessage(R.drawable.img_error_load_page.toString(), getString(R.string.title_not_logged_in), getString(R.string.message_not_logged_in)) {
                    gotoActon(BuildConfig.DEFAULT_ACTIVITY)
                }.setButtonActionText(getString(R.string.text_login))
            } else showMessage(R.drawable.img_error_load_page.toString(), getString(R.string.title_not_logged_in), getString(R.string.message_not_logged_in)) {
                gotoActon(BuildConfig.DEFAULT_ACTIVITY)
            }.setButtonActionText(getString(R.string.text_login))
        }

        0 -> showMessage(R.drawable.img_error_not_internet.toString(), getString(R.string.title_error_no_internet2), null, callbackAction)

        408, 504 -> showMessage(R.drawable.img_error_load_page.toString(), getString(R.string.title_timeout), getString(R.string.description_error_general), callbackAction)
        else -> showMessage(R.drawable.img_error_load_page.toString(), getString(R.string.title_error_general), generalResponse.message ?: getString(R.string.description_error_general), callbackAction)
    }
}


fun Fragment.showGeneralCodeError(error: GeneralResponse, callbackAction: (() -> Unit)? = null) : BottomSheetMessageFragment {
    return requireActivity().showGeneralCodeError(error, callbackAction)
}


fun String.parsingTime() : String {
    val split = this.split(":")
    return if (split.size >= 2) {
        "${split[0]}:${split[1]}"
    } else this
}


@Throws(IOException::class)
fun Uri.getFile(context: Context): File {
    val destinationFilename =
        File(context.filesDir.path + File.separatorChar + queryName(context.contentResolver, this))
    try {
        context.contentResolver.openInputStream(this).use { ins ->
            try {
                FileOutputStream(destinationFilename).use { os ->
                    val buffer = ByteArray(4096)
                    var length: Int
                    while (ins!!.read(buffer).also { length = it } > 0) {
                        os.write(buffer, 0, length)
                    }
                    os.flush()
                }
            } catch (ex: java.lang.Exception) {
                Log.e("Save File", ex.message!!)
                ex.printStackTrace()
            }
        }
    } catch (ex: java.lang.Exception) {
        Log.e("Save File", ex.message!!)
        ex.printStackTrace()
    }
    return destinationFilename
}

fun <T> Resource.Error<T>.toGeneralMessage(): GeneralResponse {
    return GeneralResponse(this.message, this.statusCode as Int)
}