package com.jumadi.template.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.common.ui.base.BaseActivity
import com.app.common.ui.extensions.gotoActivityNewTask
import com.app.core.extensions.getColorFromAttr
import com.app.core.extensions.setWindowLightStatusBar
import com.app.core.network.ApiResponse
import com.jumadi.template.app.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val colorRedDark = getColorFromAttr(androidx.appcompat.R.attr.colorPrimary)
        setWindowLightStatusBar(false, colorRedDark, colorRedDark)

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.template.collect {resource ->
                    when(resource) {
                        is ApiResponse.Error -> binding.textView.text = resource.error.message
                        is ApiResponse.Loading -> binding.textView.text = String.format("%s", "Loading...")
                        is ApiResponse.Success -> binding.textView.text = resource.data
                    }
                }
            }
        }
    }
}