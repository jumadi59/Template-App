package com.app.core.util

import android.Manifest
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX


/**
 * Created by JJ date on 10/10/2023.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
object PermissionManager {

    fun cekPermission(activity: FragmentActivity, list: List<String>, permissionListener: PermissionListener) {
        if (activity.isDestroyed || activity.isFinishing) return
        val builder = PermissionX.init(activity)
            .permissions(list)

        try {
            builder.request { allGranted, _, _ ->
                if (allGranted) { permissionListener.onPermissionsChecked()
                } else { permissionListener.onPermissionsDenied() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun cekPermission(fragment: Fragment, list: List<String>,permissionListener: PermissionListener) {
        if (fragment.isDetached) return
        val builder = PermissionX.init(fragment)
            .permissions(list)

        try {
            builder.request { allGranted, _, _ ->
                if (allGranted) { permissionListener.onPermissionsChecked()
                } else { permissionListener.onPermissionsDenied() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun FragmentActivity.cekPermission(permissionType: PermissionType, permissionListener: PermissionListener) {
    PermissionManager.cekPermission(this, permissionType.list, permissionListener)
}

fun Fragment.cekPermission(permissionType: PermissionType, permissionListener: PermissionListener) {
    PermissionManager.cekPermission(this, permissionType.list, permissionListener)
}

enum class PermissionType(val list: List<String>) {
    LOCATION(arrayListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )),
    CAMERA(arrayListOf(Manifest.permission.CAMERA)),
    STORAGE(arrayListOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)),
    SMS(arrayListOf(Manifest.permission.RECEIVE_SMS)),
}

interface PermissionListener {
    fun onPermissionsChecked()
    fun onPermissionsDenied()
}