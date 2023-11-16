package com.app.common.ui.bindings

import android.view.View
import androidx.databinding.BindingAdapter


/**
 * Created by Anonim date on 28/06/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
object ViewBinding {

    @JvmStatic
    @BindingAdapter("isGone")
    fun isGone(view: View, b: Boolean) {
        view.visibility = if (b) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("isVisible")
    fun isVisible(view: View, b: Boolean) {
        view.visibility = if (b) View.VISIBLE else View.GONE
    }



    @JvmStatic
    @BindingAdapter("isInVisible")
    fun isInVisible(view: View, b: Boolean) {
        view.visibility = if (b) View.VISIBLE else View.INVISIBLE
    }
}