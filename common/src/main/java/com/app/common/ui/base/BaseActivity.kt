package com.app.common.ui.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding
import com.app.common.databinding.DialogProgressBinding
import com.app.common.ui.extensions.gotoActivityNewTask
import java.lang.reflect.Field


/**
 * Created by Jumadi Janjaya date on 20/06/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) ******. All rights reserved.
 **/
abstract class  BaseActivity : AppCompatActivity() {

    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val fields: Array<Field> = this::class.java.declaredFields
            for (field in fields) {
                if (field.name.startsWith("binding")) {
                    field.isAccessible = true
                    val binding = field.get(this) as Lazy<*>
                    setContentView((binding.value as ViewBinding).root)
                    break
                }
            }
        } catch (e :Exception) {
            e.printStackTrace()
        }
    }

    /**
     * show progress
     */
    protected fun showProgress() {
        if (!this::progressDialog.isInitialized) {
            progressDialog = Dialog(this@BaseActivity).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                val binding = DialogProgressBinding.inflate(LayoutInflater.from(this@BaseActivity))
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                setContentView(binding.root)
                setCancelable(false)
                window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                if (!isShowing) show()
            }
        } else {
            if (!progressDialog.isShowing) progressDialog.show()

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    /**
     * hide progress
     */
    protected fun hideProgress() {
        if (this::progressDialog.isInitialized && progressDialog.isShowing) progressDialog.dismiss()
    }

    open fun permissionsGranted() {
        Log.e("Base", "permissionsGranted()")
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val override = Configuration(newBase?.resources?.configuration)
        override.uiMode = Configuration.UI_MODE_NIGHT_NO
        override.fontScale = 1f
        applyOverrideConfiguration(override)
    }

    inline fun <reified T: Activity> logOut() {
        gotoActivityNewTask<T>()
    }

    fun clearData() {
    }

    inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
        crossinline bindingInflater: (LayoutInflater) -> T
    ) = lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater)
    }

}