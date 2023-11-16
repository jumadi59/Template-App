package com.app.common.ui.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.app.common.R
import com.app.common.ui.message.BottomSheetMessageFragment
import com.app.core.extensions.isNumber
import com.app.core.network.response.GeneralResponse
import java.io.Serializable
import java.util.*


/**
 * Created by Anonim date on 28/06/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/

inline fun <reified T: Activity> Context.gotoActivity(vararg params: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java).putAny(*params)
    startActivity(intent)
}

inline fun <reified T: Activity> Activity.gotoActivityNewTask(vararg params: Pair<String, Any?>) {
    startActivity(Intent(this, T::class.java).putAny(*params).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
    finish()
}


inline fun <reified T: Activity> Fragment.gotoActivity(vararg params: Pair<String, Any?>) {
    context?.gotoActivity<T>(*params)
}

inline fun <reified T: Activity> ActivityResultLauncher<Intent>.gotoActivity(activity: Activity, vararg params: Pair<String, Any?>) {
    val intent = Intent(activity, T::class.java).putAny(*params)
    launch(intent)
}

fun Context.gotoActon(action: String) : Boolean {
    if (action.isNumber()) {
        return try {
            findNavController(this as Activity, R.id.nav_host_fragment_content_home).navigate(action.toInt())
            true
        }catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    } else if(action.contains("://")) {
        return try {
            if (action.contains("class://")) {
                val uri = Uri.parse(action)
                val intent= Intent(this, Class.forName(uri.host!!))
                uri.queryParameterNames.forEach {
                    intent.putExtra(it, uri.getQueryParameter(it))
                }
                startActivity(intent)
            }
            /*else
                CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(action))*/
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Application not found", Toast.LENGTH_SHORT).show()
            false
        }
    } else {
        return try {
            startActivity(Intent(this, Class.forName(action)))
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }
}


fun Fragment.gotoActon(action: String) : Boolean {
    if (action.isNumber()) {
        return try {
            findNavController().navigate(action.toInt())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    } else if(action.contains("://")) {
        return try {
            if (action.contains("class://")) {
                val uri = Uri.parse(action)
                val intent= Intent(requireContext(), Class.forName(uri.host!!))
                uri.queryParameterNames.forEach {
                    intent.putExtra(it, uri.getQueryParameter(it))
                }
                startActivity(intent)
            }
            /*else
                CustomTabsIntent.Builder().build().launchUrl(requireContext(), Uri.parse(action))*/
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Application not found", Toast.LENGTH_SHORT).show()
            false
        }
    } else {
        return try {
            startActivity(Intent(requireContext(), Class.forName(action)))
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }
}

fun Intent.putAny(vararg params: Pair<String, Any?>) : Intent {
    params.forEach {
        when (val value = it.second) {
            null -> putExtra(it.first, null as Serializable?)
            is Int -> putExtra(it.first, value)
            is Long -> putExtra(it.first, value)
            is CharSequence -> putExtra(it.first, value)
            is String -> putExtra(it.first, value)
            is Float -> putExtra(it.first, value)
            is Double -> putExtra(it.first, value)
            is Char -> putExtra(it.first, value)
            is Short -> putExtra(it.first, value)
            is Boolean -> putExtra(it.first, value)
            is Serializable -> putExtra(it.first, value)
            is Bundle -> putExtra(it.first, value)
            is Parcelable -> putExtra(it.first, value)
            is Array<*> -> when {
                value.isArrayOf<CharSequence>() -> putExtra(it.first, value)
                value.isArrayOf<String>() -> putExtra(it.first, value)
                value.isArrayOf<Parcelable>() -> putExtra(it.first, value)
                else -> throw Exception("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            is IntArray -> putExtra(it.first, value)
            is LongArray -> putExtra(it.first, value)
            is FloatArray -> putExtra(it.first, value)
            is DoubleArray -> putExtra(it.first, value)
            is CharArray -> putExtra(it.first, value)
            is ShortArray -> putExtra(it.first, value)
            is BooleanArray -> putExtra(it.first, value)
            else -> throw Exception("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
        }
    }
    return this
}

fun FragmentActivity.showMessage(logo: String?, title: String, subtitle: String?, callbackAction: (() -> Unit)? = null) : BottomSheetMessageFragment {
    val fragment = BottomSheetMessageFragment.newInstance(logo, title, subtitle).setCallbackActionClick(callbackAction)
    if (!isFinishing) fragment.show(supportFragmentManager, "MESSAGE_BOTTOM_SHEET_${Date().time}")
    return fragment
}

fun Fragment.showMessage(logo: String?, title: String, subtitle: String?, callbackAction: (() -> Unit)? = null) : BottomSheetMessageFragment {
    val fragment = BottomSheetMessageFragment.newInstance(logo, title, subtitle).setCallbackActionClick(callbackAction)
    if (isResumed) fragment.show(childFragmentManager, "MESSAGE_BOTTOM_SHEET_${Date().time}")
    return fragment
}

fun FragmentActivity.showMessage(generalResponse: GeneralResponse, callbackAction: (() -> Unit)? = null) : BottomSheetMessageFragment {
    return this.showMessage(generalResponse.icon, generalResponse.error, generalResponse.message, callbackAction)
}

fun Fragment.showMessage(generalResponse: GeneralResponse, callbackAction: (() -> Unit)? = null) : BottomSheetMessageFragment {
    return this.showMessage(generalResponse.icon, generalResponse.error, generalResponse.message, callbackAction)
}

fun FragmentActivity.showMessageError(title: String, subtitle: String?, callbackAction: (() -> Unit)? = null) : BottomSheetMessageFragment {
    return this.showMessage(R.drawable.ic_baseline_error_outline_24.toString(), title, subtitle, callbackAction)
}

fun FragmentActivity.showMessageSuccess(title: String, subtitle: String?, callbackAction: (() -> Unit)? = null) : BottomSheetMessageFragment {
    return this.showMessage(R.drawable.ic_baseline_check_circle_outline_24.toString(), title, subtitle, callbackAction)
}

