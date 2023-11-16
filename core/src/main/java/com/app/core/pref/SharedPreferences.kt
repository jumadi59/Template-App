package com.app.core.pref

import android.content.SharedPreferences

//kotlin extensions to easy handling save data to SharedPreferences
//initialize shared preferences

const val PREFERENCE_NAME = "Preference"
lateinit var Prefs: SharedPreferences

/**
 * saving data to SharedPreferences
 * @param key Key key, which is used to identify the store data
 * @param data Any data to save
 */
fun SharedPreferences.putAny(key: String, data: Any) {
    when (data) {
        is String -> edit().putString(key, data).apply()
        is Int -> edit().putInt(key, data).apply()
        is Boolean -> edit().putBoolean(key, data).apply()
        is Float -> edit().putFloat(key, data).apply()
        is Long -> edit().putLong(key, data).apply()
    }
}

/**
 * remove data from SharedPreferences
 * @param key Key, which is used to identify data to remove
 */
fun SharedPreferences.remove(key: String){
    edit().remove(key).apply()
}

fun SharedPreferences.getBoolean(key: String) : Boolean {
    return getBoolean(key, false)
}

fun SharedPreferences.getString(key: String) : String {
    return getString(key, "")?:""
}

fun SharedPreferences.getInt(key: String) : Int {
    return getInt(key, 0)
}

fun SharedPreferences.getLong(key: String) : Long {
    return getLong(key, 0)
}

fun SharedPreferences.getFloat(key: String) : Float {
    return getFloat(key, 0f)
}