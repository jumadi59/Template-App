package com.app.common.ui.bindings

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import com.app.common.R
import com.app.common.ui.glide.GlideApp
import com.app.common.ui.glide.SvgSoftwareLayerSetter
import com.app.core.extensions.dpToPx
import com.app.core.extensions.isNumber
import com.app.core.util.Util
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


/**
 * Created by Anonim date on 28/06/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
object ImageViewBinding {
    @JvmStatic
    @BindingAdapter("imgSrc", "imgPath", "placeholderSrc", "errorSrc", "alt", requireAll = false)
    fun imgSrc(
        img: ImageView,
        url: String?,
        path: String? = null,
        @DrawableRes placeholder: Int? = null,
        @DrawableRes error: Int? = null,
        alt: String? = null
    ) {
        when {
            url.isNullOrEmpty() -> img.setImageResource(placeholder ?: R.drawable.placeholder_image)
            else -> {
                val bitmapError = img.context.run { ResourcesCompat.getDrawable(resources, error ?: R.drawable.placeholder_image_error, theme) }?.toBitmap(100f.dpToPx(), 100f.dpToPx())
                GlideApp.with(img.context).builder(img.context, url).placeholder(placeholder ?: R.drawable.placeholder_image)
                    .error(alt?.run { altImageBitmap(this, img.context) } ?: bitmapError)
                    .into(img)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("imgSrcNoPlaceholder", "imgPath", requireAll = false)
    fun imgSrcNoPlaceholder(img: ImageView, url: String?, path: String? = null) {
        when {
            url.isNullOrEmpty() -> {}
            else -> {
                GlideApp.with(img.context).builder(img.context, url)
                    .apply(RequestOptions().
                    diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(img)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("imgSrcCircle", "imgPath", requireAll = false)
    fun imgSrcCircle(img: ImageView, url: String?, path: String? = null) {
        when {
            url.isNullOrEmpty() -> img.setImageResource(R.drawable.placeholder_image_circle)
            else -> {
                GlideApp.with(img.context).builder(img.context, url)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .placeholder(R.drawable.placeholder_image_circle)
                    .error(R.drawable.placeholder_image_circle_error)
                    .into(img)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("imgSrcRound", "imgPath", requireAll = false)
    fun imgSrcRound(img: ImageView, url: String?, path: String? = null) {
        when {
            url.isNullOrEmpty() -> img.setImageResource(R.drawable.placeholder_round)
            else -> {
                GlideApp.with(img.context).builder(img.context, url)
                    .transform(CenterCrop(), RoundedCorners(8f.dpToPx()))
                    .placeholder(R.drawable.placeholder_round)
                    .error(R.drawable.placeholder_round)
                    .into(img)

            }
        }
    }

    @JvmStatic
    @BindingAdapter("imgQRCode", "logoQRCode", requireAll = false)
    fun imgQRCode(img: ImageView, qrCode: String?, logoQRCode: Drawable? = null) {
        if (qrCode.isNullOrEmpty()) return
        when {
            qrCode.contains("base64") -> {
                if (logoQRCode is BitmapDrawable) {
                    val bitmap = logoQRCode.bitmap
                    Util.stringBase64ToBitmap(qrCode)?.let {
                        img.setImageBitmap(Util.overlayToCenter(bitmap, it))
                    }
                } else {
                    Util.stringBase64ToBitmap(qrCode)?.let {
                        img.setImageBitmap(it)
                    }
                }
            }
            else -> {
                if (qrCode.isNotEmpty()) {
                    Util.generateQRCode(img.context, qrCode)?.let {
                        when (logoQRCode) {
                            is BitmapDrawable -> {
                                val bitmap = logoQRCode.bitmap
                                img.setImageBitmap(Util.overlayToCenter(bitmap, it))
                            }
                            else -> img.setImageBitmap(it)
                        }
                    }
                }
            }
        }
    }

    private fun imgBlackWhite(srcBitmap: Bitmap) : Bitmap {
        val bitmap = Bitmap.createBitmap(srcBitmap)
        val canvas = Canvas(bitmap)
        val ma = ColorMatrix()
        ma.setSaturation(0f)
        val paint = Paint()
        paint.colorFilter = ColorMatrixColorFilter(ma)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return bitmap
    }


    private fun replaceColor(src: Bitmap?): Bitmap? {
        if (src == null) return null
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, 1 * width, 0, 0, width, height)
        for (x in pixels.indices) {
            if (pixels[x] == Color.WHITE) pixels[x] = Color.TRANSPARENT
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

    private fun altImageBitmap(alt: String, context: Context) : Bitmap? {
        val textSize = 500f.dpToPx()
        val color = ResourcesCompat.getColor(context.resources, R.color.black, context.theme)
        return Util.textAsBitmap(alt, textSize.toFloat(), color)
    }

    fun RequestManager.builder(context: Context, url: String): RequestBuilder<out Drawable> {
        return if (url.endsWith("svg", ignoreCase = true)) this.`as`(PictureDrawable::class.java).listener(SvgSoftwareLayerSetter()).load(when {
            url.isNumber() -> this.load(url.toInt())
            url.contains("base64") -> this.load(Util.stringBase64ToBitmap(url))
            url.contains("file:///android_asset/") -> {
                try {
                    val inputStream = context.assets.open(url.replace("file:///android_asset/", ""))
                    this.load(BitmapFactory.decodeStream(inputStream))
                } catch (e: Exception) {
                    this.load(R.drawable.placeholder_image_error)
                }
            }
            else -> this.load( if(URLUtil.isValidUrl(url)) url else "/$url")
        }) else  when {
            url.isNumber() -> this.load(url.toInt())
            url.contains("base64") -> this.load(Util.stringBase64ToBitmap(url))
            url.contains("file:///android_asset/") -> {
                try {
                    val inputStream = context.assets.open(url.replace("file:///android_asset/", ""))
                    this.load(BitmapFactory.decodeStream(inputStream))
                } catch (e: Exception) {
                    this.load(R.drawable.placeholder_image_error)
                }
            }
            else -> this.load( if(URLUtil.isValidUrl(url)) url else "/$url")
        }
    }

}