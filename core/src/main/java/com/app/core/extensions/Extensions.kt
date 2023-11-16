package com.app.core.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.*
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.app.core.network.response.GeneralResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Modifier
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


fun Float.dpToPx(): Int {
    val metrics = Resources.getSystem().displayMetrics
    return (this * metrics.density + 0.5f).roundToInt()
}

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int
): Int {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
    val textColor = typedArray.getColor(0, 0)
    typedArray.recycle()
    return textColor
}

fun Activity.getColor(@ColorRes color: Int) {
    ResourcesCompat.getColor(resources, color, theme)
}

fun Fragment.getColor(@ColorRes color: Int) {
    ResourcesCompat.getColor(resources, color, requireActivity().theme)
}

fun Activity.setWindowLightStatusBar(isLight: Boolean, statusBarColor: Int, navigationBarColor: Int? =null) {
    window.statusBarColor = statusBarColor
   if (navigationBarColor !=null) window.navigationBarColor = navigationBarColor
    if (isLight) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (statusBarColor == Color.TRANSPARENT)
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or  View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                if (statusBarColor == Color.TRANSPARENT)
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    } else {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

fun String?.isNumber(): Boolean {
    try {
        this?.toInt()
    } catch (nfe: NumberFormatException) {
        return false
    }
    return true
}

fun <T> List<T>?.isNotNullOrEmpty() : Boolean {
    return this != null && this.isNotEmpty()
}

fun Int.toRupiah(): String {
    return NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(this.toDouble()).replace("Rp", "Rp ").replace(",00", "")
}

fun Double.toRupiah(): String {
    return NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(this).replace("Rp", "Rp ").replace(",00", "")
}

fun Int.priceDiscount(discount: Int): Int {
    return if (discount == 0) this else this - (this * discount / 100)
}

fun Int.toRupiah(discount: Int): String {
    val s = if (discount == 0) this else this - (this * discount / 100)
    return NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(s.toDouble()).replace("Rp", "Rp ").replace(",00", "")
}

fun Double.toRupiah(discount: Int): String {
    val s = if (discount == 0) this else this - (this * discount / 100)
    return NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(s).replace("Rp", "Rp ").replace(",00", "")
}


fun Drawable.changeColor(color: Int): Drawable {
    DrawableCompat.setTintList(this, ColorStateList.valueOf(color))
    return this
}

fun getDrawable(
    resources: Resources,
    @DrawableRes drawable: Int,
    theme: Resources.Theme? = null,
    @ColorRes color: Int? = null
): Drawable? {
    return ResourcesCompat.getDrawable(resources, drawable, theme).apply {
        color?.let { ResourcesCompat.getColor(resources, it, theme) }
            ?.let { c -> this?.changeColor(c) }
    }
}

fun Int.colorWithAlpha(ratio: Float): Int {
    val alpha = (Color.alpha(this) * ratio).roundToInt()
    val r = Color.red(this)
    val g = Color.green(this)
    val b = Color.blue(this)
    return Color.argb(alpha, r, g, b)
}

fun Int.colorTo(ratio: Float, colorTo: Int): Int {
    val iRatio = 1f - ratio
    val fromRed = Color.red(this)
    val fromGreen = Color.green(this)
    val fromBlue = Color.blue(this)

    val toRed = Color.red(colorTo)
    val toGreen = Color.green(colorTo)
    val toBlue = Color.blue(colorTo)

    val red = (fromRed * ratio + toRed * iRatio).roundToInt()
    val green = (fromGreen * ratio + toGreen * iRatio).roundToInt()
    val blue = (fromBlue * ratio + toBlue * iRatio).roundToInt()

    val r = if (red < 0) 0 else if (red > 255) 255 else red
    val g = if (green < 0) 0 else if (green > 255) 255 else green
    val b = if (blue < 0) 0 else if (blue > 255) 255 else blue

    return Color.rgb(r, g, b)
}

fun Context.hideInput(et: EditText, iBinder: IBinder) {
    val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)
    inputMethodManager?.hideSoftInputFromWindow(iBinder, 0)
    if (et.hasFocus())
        et.clearFocus()
}

fun <K, V> HashMap<K, V>.copy(key: K? = null, value: V? = null): HashMap<K, V> {
    val hashMap = HashMap(this)
    if (key != null && value != null) hashMap[key] = value
    return hashMap
}


fun Long.toDateFormat(format: String): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return try {
        dateFormat.format(Date(this))
    } catch (e: ParseException) {
        ""
    }
}
@SuppressLint("SimpleDateFormat")
fun String.toTimeLongFormat(format: String? = null): Long {
    return if (this.isNotEmpty() && this != "0000-00-00") {
        val dateFormat = SimpleDateFormat(format?:"yyyy-MM-dd")
        if (format?.contains("Z") == true) dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        try {
            val today = dateFormat.parse(this)
            today!!.time
        } catch (e: ParseException) {
            Date().time
        }
    } else Date().time
}

 fun <T> T?.runNotNull(block: (item: T) -> Boolean, blockFalse: (() -> Unit)? = null): Boolean {
    return if (this != null) {
        val isResult = block.invoke(this)
        if (!isResult) blockFalse?.invoke()
        isResult
    } else {
        blockFalse?.invoke()
        false
    }
}

public inline fun <reified T> String.toDataClass() : T? {
    return try {
        Gson().fromJson(this, T::class.java)
    } catch (e: Exception) {
        return null
    }
}

public inline fun <reified T> String.toDataTypeClass() : T? {
    return try {
        Gson().fromJson(this, object : TypeToken<T>() {}.type)
    } catch (e: Exception) {
        return null
    }
}

public fun <T> T.toStringJson() : String {
    return Gson().toJson(this)
}

fun Int.isFailedId(clazz: Class<*>) : Boolean  {
    try {
        val c = clazz.declaringClass
        for (f in clazz.declaredFields) {
            if (Modifier.isStatic(f.modifiers)) {
                val wasAccessible = f.isAccessible
                f.isAccessible = true
                if (f.getInt(c) == this) {
                    return true
                }
                f.isAccessible = wasAccessible
            }
        }
    } catch (e: Exception) {}
    return false
}

@SuppressLint("SimpleDateFormat")
fun String.formatDateTime() : String {
    return try {
        val format = SimpleDateFormat(this)
        val date1 = format.format(Date().time)
        date1.toString()
    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun String.formatCardNumber() : String {
    val result = StringBuilder()
    for (i in 0 until this.length) {
        if (i % 4 == 0 && i != 0) {
            result.append(" ")
        }
        result.append(this[i])
    }
    return result.toString()
}

fun String.formatPhoneNumber() : String {
    if (this.length < 7) return this

    val phone = if (this[0] == '0') this.substring(1, this.length) else this
    val digit = phone.substring(0, 3)
    val digit2 = phone.substring(3, phone.length)
    val result = StringBuilder()
    for (i in digit2.indices) {
        if (i % 4 == 0 && i != 0) {
            result.append(" ")
        }
        result.append(digit2[i])
    }
    return "$digit $result"
}

fun String?.findConstant(list: String, prefix: String) : Boolean {
    val split = list.split(prefix)
    for (s in split) {
        if (this?.contains(s, ignoreCase = true) == true) {
            return true
        }
    }
    return false
}

fun Context.copyText(text: String) {
    val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
}

fun String.capitalizeWord(): String {
    return try {
        val words = this.lowercase().split(" ").toTypedArray()
        var capitalizeWord = ""
        for (w in words) {
            val first = w.substring(0, 1)
            val afterfirst = w.substring(1)
            capitalizeWord += first.uppercase(Locale.getDefault()) + afterfirst + " "
        }
        capitalizeWord.trim { it <= ' ' }
    } catch (e: Exception) {
        this
    }
}

fun String.removeCodeCountryPhone() : String {
    return this
}

fun Context.setupLight(brightnessMode: Int, brightness: Int) {
    try {
        if (brightnessMode != Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
        }
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, brightnessMode)

    } catch (e: Exception) {
        Log.e(this::class.java.simpleName,"Exception $e")
    }
}

fun Window.setupLight(brightness: Float) {
    val lp: WindowManager.LayoutParams = attributes
    lp.screenBrightness = brightness
    attributes = lp
}


fun Context.isAppIsInBackground(): Boolean {
    var isInBackground = true
    val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningProcesses = am.runningAppProcesses
    for (processInfo: ActivityManager.RunningAppProcessInfo in runningProcesses) {
        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            for (activeProcess: String in processInfo.pkgList) {
                if ((activeProcess == this.packageName)) {
                    isInBackground = false
                }
            }
        }
    }
    return isInBackground
}