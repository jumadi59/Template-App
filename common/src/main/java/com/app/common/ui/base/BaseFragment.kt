package com.app.common.ui.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.app.common.databinding.DialogProgressBinding
import java.lang.reflect.Field


/**
 * Created by Jumadi Janjaya date on 25/06/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) ******. All rights reserved.
 **/

const val ACTION_URL = "action_url"

abstract class BaseFragment : Fragment() {

    private lateinit var progressDialog: Dialog
    var mLayoutInflater: LayoutInflater? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("TAG", "onCreateView")
        mLayoutInflater = inflater
        try {
            val fields: Array<Field> = this::class.java.declaredFields
            for (field in fields) {
                if (field.name.startsWith("binding")) {
                    field.isAccessible = true
                    val binding = field.get(this) as Lazy<*>
                    binding.isInitialized()
                    Log.e("TAG", "isInitialized")
                    return (binding.value as ViewBinding).root
                }
            }
        } catch (e :Exception) {
            e.printStackTrace()
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * show progress
     */
    protected fun showProgress() {
        if (!this::progressDialog.isInitialized) {
            progressDialog = Dialog(requireContext()).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                val binding = DialogProgressBinding.inflate(LayoutInflater.from(requireContext()))
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

    /**
     * hide progress
     */
    protected fun hideProgress() {
        if (this::progressDialog.isInitialized && progressDialog.isShowing) progressDialog.dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(ACTION_URL)?.let {handleActionLink(Uri.parse(it))}
        initView()
    }

    open fun handleActionLink(uri: Uri) {}

    abstract fun initView()

    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState)
    }

    inline fun <T : ViewBinding> viewBinding(
        crossinline bindingInflater: (LayoutInflater) -> T
    ) = lazy(LazyThreadSafetyMode.NONE) {
        Log.e("TAG", "initialize")
        bindingInflater.invoke(mLayoutInflater!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mLayoutInflater = null

    }
}