package com.jumadi.template.app

import android.app.Application
import com.app.core.pref.PREFERENCE_NAME
import com.app.core.pref.Prefs
import dagger.hilt.android.HiltAndroidApp


/**
 * Created by JJ date on 17/10/2023.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Prefs = getSharedPreferences(PREFERENCE_NAME, 0)
    }
}