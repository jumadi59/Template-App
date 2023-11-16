package com.app.core.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.provider.Settings.Secure
import android.util.Base64
import android.util.Log
import android.view.Display
import android.view.WindowManager
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.zxing.WriterException
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.roundToInt


/**
 * Created by Anonim date on 20/07/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
object Util {

    fun generateQRCode(context: Context, string: String): Bitmap? {
        val manager = context.getSystemService(WINDOW_SERVICE) as WindowManager?
        val display: Display = manager!!.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width: Int = point.x
        val height: Int = point.y
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 3
        val qrgEncoder = QRGEncoder(string, null, QRGContents.Type.TEXT, dimen)
        return try {
            qrgEncoder.encodeAsBitmap()
        } catch (e: WriterException) {
            Log.e("generateQRCode", e.toString())
            null
        }
    }

    fun bitMapToStringBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val arr = baos.toByteArray()
        return "data:image/png;base64," + Base64.encodeToString(arr, Base64.DEFAULT)
    }

    fun stringBase64ToBitmap(base64: String): Bitmap? {
        val decodedByte = Base64.decode(base64.split(",")[1], Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
    }

    fun textAsBitmap(text: String, textSize: Float, textColor: Int): Bitmap? {
        // adapted from https://stackoverflow.com/a/8799344/1476989
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        paint.textAlign = Paint.Align.LEFT
        val baseline: Float = -paint.ascent() // ascent() is negative
        var width = (paint.measureText(text) + 0.0f).toInt() // round
        var height = (baseline + paint.descent() + 0.0f).toInt()
        val trueWidth = width
        if (width > height) height = width else width = height
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawText(text, (width / 2 - trueWidth / 2).toFloat(), baseline, paint)
        return image
    }

    fun overlayToCenter(overlayBitmap: Bitmap, bitmap: Bitmap): Bitmap {
        val bitmap2Width = overlayBitmap.width
        val bitmap2Height = overlayBitmap.height
        val marginLeft = (bitmap.width * 0.5 - bitmap2Width * 0.5).toFloat()
        val marginTop = (bitmap.height * 0.5 - bitmap2Height * 0.5).toFloat()
        val canvas = Canvas(bitmap)
        canvas.drawBitmap(bitmap, Matrix(), null)
        canvas.drawBitmap(overlayBitmap, marginLeft, marginTop, null)
        return bitmap
    }

    fun resizeImage(realImage: Bitmap, maxImageSize: Float, filter: Boolean): Bitmap {
        val ratio = (maxImageSize / realImage.width).coerceAtMost(maxImageSize / realImage.height)
        val width = (ratio * realImage.width).roundToInt()
        val height = (ratio * realImage.height).roundToInt()
        return Bitmap.createScaledBitmap(realImage, width, height, filter)
    }

    fun queryName(resolver: ContentResolver, uri: Uri): String {
        val returnCursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    fun isRooted(context: Context): Boolean {
        val isEmulator = isEmulator(context)
        val buildTags = Build.TAGS
        return if (!isEmulator && buildTags != null && buildTags.contains("test-keys")) {
            true
        } else {
            var file = File("/system/app/Superuser.apk")
            if (file.exists()) {
                true
            } else {
                file = File("/system/xbin/su")
                !isEmulator && file.exists()
            }
        }
    }

    @SuppressLint("HardwareIds")
    fun isEmulator(context: Context): Boolean {
        val androidId = Secure.getString(context.contentResolver, "android_id")
        return "sdk" == Build.PRODUCT || "google_sdk" == Build.PRODUCT || androidId == null
    }
}