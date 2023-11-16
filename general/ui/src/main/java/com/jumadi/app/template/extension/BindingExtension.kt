package com.jumadi.app.template.extension

import android.util.Log
import androidx.core.view.isVisible
import com.app.common.ui.bindings.ImageViewBinding
import com.app.common.ui.extensions.dataControllerError
import com.app.common.ui.extensions.gotoActon
import com.app.common.ui.extensions.visible
import com.app.core.BuildConfig
import com.app.core.network.ApiResponse
import com.app.core.network.Resource
import com.app.core.network.response.GeneralResponse
import com.dika.app.antarmoda.ui.R
import com.dika.app.antarmoda.ui.databinding.LayoutMsgBinding


/**
 * Created by Anonim date on 14/08/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/

fun LayoutMsgBinding.showMessage(logo: String?, title: String, subtitle: String?, callbackAction: (() -> Unit)? = null) : LayoutMsgBinding {
    if (logo != null) ImageViewBinding.imgSrcNoPlaceholder(ivIcon, logo)
    else ImageViewBinding.imgSrcNoPlaceholder(ivIcon, R.drawable.img_payment_error.toString())
    tvTitle.text = title
    tvDescription.text = subtitle

    btnAction.isVisible = callbackAction != null
    btnAction.setOnClickListener { callbackAction?.invoke() }

    root.visible()
    return this
}

fun <T> LayoutMsgBinding.showMessageError(apiResponse: ApiResponse<T>, callbackAction: (() -> Unit)? = null) : LayoutMsgBinding {
    apiResponse.dataControllerError {
        when(it.status) {
            0 -> showMessage(R.drawable.img_error_not_internet.toString(), root.context.getString(R.string.title_error_no_internet2), null, callbackAction)
            403 -> showMessage(R.drawable.img_error_load_page.toString(), root.context.getString(R.string.title_error_general), root.context.getString(R.string.description_error_session_login)) {
            //root.context.gotoActon()
            }.also { binding ->
                binding.btnAction.setText(R.string.text_login)
            }
            408, 504 -> showMessage(R.drawable.img_error_load_page.toString(), root.context.getString(R.string.title_timeout),root.context.getString(R.string.description_error_general), callbackAction)
            else -> showMessage(R.drawable.img_error_load_page.toString(), root.context.getString(R.string.title_error_general),it.message?:root.context.getString(R.string.description_error_general), callbackAction)
        }
        root.visible()
    }
    return this
}

fun <T> LayoutMsgBinding.showMessageError(response: Resource<T>, logo: String? = null, title: String? = null, message: String? = null, callbackAction: (() -> Unit)? = null) : LayoutMsgBinding {
    root.visible()
    when(response) {
        is Resource.Error -> {
            val error = GeneralResponse(response.message, response.statusCode as Int)
            Log.e("LayoutMsgBinding", "showMessageError() > $error ")
            when(error.status) {
                403, 401 -> showMessage(R.drawable.img_error_load_page.toString(), root.context.getString(R.string.title_error_general), root.context.getString(R.string.description_error_session_login)) {
                    root.context.gotoActon(BuildConfig.DEFAULT_ACTIVITY)
                }.also { binding ->
                    binding.btnAction.setText(R.string.text_login)
                }
                0 -> showMessage(R.drawable.img_error_not_internet.toString(), root.context.getString(R.string.title_error_no_internet2), null, callbackAction)
                408, 504 -> showMessage(R.drawable.img_error_load_page.toString(), root.context.getString(R.string.title_timeout),root.context.getString(R.string.description_error_general), callbackAction)
                204 -> showMessage(logo?:R.drawable.img_error_load_page.toString(),
                    title?:root.context.getString(R.string.title_error_general),
                    message?:root.context.getString(R.string.description_error_general), callbackAction)
                else -> showMessage(R.drawable.img_error_load_page.toString(), root.context.getString(R.string.title_error_general),error.message?:root.context.getString(R.string.description_error_general), callbackAction)

            }
        }
        is Resource.Loading ->{
        }
        is Resource.Success -> {
            showMessage(logo?:R.drawable.img_error_load_page.toString(),
                title?:root.context.getString(R.string.title_error_general),
                message?:root.context.getString(R.string.description_error_general), callbackAction)
        }
    }
    return this
}

fun <T> LayoutMsgBinding.showMessageError(apiResponse: ApiResponse<T>,logo: String? = null, title: String? = null, message: String? = null, callbackAction: (() -> Unit)? = null) : LayoutMsgBinding {
    apiResponse.dataControllerError {
        when(it.status) {
            0 -> showMessage(R.drawable.img_error_not_internet.toString(), root.context.getString(R.string.title_error_no_internet2), null, callbackAction)
            403 -> showMessage(R.drawable.img_error_load_page.toString(), root.context.getString(R.string.title_error_general), root.context.getString(R.string.description_error_session_login)) {
                root.context.gotoActon(BuildConfig.DEFAULT_ACTIVITY)
            }.also { binding ->
                binding.btnAction.setText(R.string.text_login)
            }
            408, 504 -> showMessage(R.drawable.img_error_load_page.toString(), root.context.getString(R.string.title_timeout),root.context.getString(R.string.description_error_general), callbackAction)
            204 -> showMessage(logo?:R.drawable.img_error_load_page.toString(),
                title?:root.context.getString(R.string.title_error_general),
                message?:root.context.getString(R.string.description_error_general), callbackAction)
            else -> showMessage(R.drawable.img_error_load_page.toString(), root.context.getString(R.string.title_error_general),it.message?:root.context.getString(R.string.description_error_general), callbackAction)
        }
        root.visible()
    }
    return this
}
