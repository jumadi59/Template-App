package com.app.common.ui.cropimage

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.app.common.R
import com.app.common.databinding.ActivityCropImageBinding
import com.app.common.ui.base.BaseActivity
import com.app.core.extensions.setWindowLightStatusBar
import com.app.core.util.Util
import com.isseiaoki.simplecropview.CropImageView

const val EXTRA_IMAGE_BASE64 = "extra_image_base64"
const val EXTRA_IMAGE_BITMAP = "extra_image_bitmap"

class CropImageActivity : BaseActivity() {

    companion object {
        var croppedBitmap: Bitmap? = null
    }

    private val binding: ActivityCropImageBinding by viewBinding(ActivityCropImageBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setWindowLightStatusBar(true, ResourcesCompat.getColor(resources, R.color.black, theme))

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels

        binding.cropImageView.apply {
            setCompressQuality(100)
            setOutputMaxSize(width, height)
            setCustomRatio(1, 1)
            setCropMode(CropImageView.CropMode.CUSTOM)
        }

        binding.btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.btnOk.setOnClickListener {
            croppedBitmap = binding.cropImageView.croppedBitmap
            setResult(RESULT_OK)
            finish()
        }

        binding.btnRotate.setOnClickListener {
            binding.cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D)
        }

        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val base64 = intent.getStringExtra(EXTRA_IMAGE_BASE64)
        val bitmap = intent.extras?.get(EXTRA_IMAGE_BITMAP)
        if (!base64.isNullOrEmpty())
            binding.cropImageView.imageBitmap = Util.stringBase64ToBitmap(base64)
        else if (bitmap is Bitmap)
            binding.cropImageView.imageBitmap =bitmap
        else if (intent.data != null) {
            contentResolver.openInputStream(intent.data!!)?.use {
                val bp = Util.resizeImage(BitmapFactory.decodeStream(it), 1000f, true)
                binding.cropImageView.imageBitmap =bp
            }
        }

    }
}