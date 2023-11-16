package com.app.common.ui.extensions

import android.animation.ValueAnimator
import android.app.Activity
import android.os.Build
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.common.ui.base.BaseAdapter
import com.app.common.ui.bindings.ImageViewBinding
import com.app.core.extensions.copyText
import com.app.core.extensions.dpToPx
import com.app.core.extensions.isNotNullOrEmpty
import com.app.core.network.ApiResponse
import com.app.core.network.Resource
import com.app.core.network.response.GeneralResponse
import com.google.android.material.appbar.AppBarLayout


/**
 * Created by Anonim date on 28/06/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/

fun View.gone(): View {
    this.visibility = View.GONE
    return this
}

fun View.visible(): View {
    this.visibility = View.VISIBLE
    return this
}

fun View.invisible(): View {
    this.visibility = View.INVISIBLE
    return this
}


fun EditText.showInput() {
    requestFocus()
    postDelayed({
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, 0)
    },200)
}

fun EditText.hideInput(iBinder: IBinder) {
    val inputMethodManager = ContextCompat.getSystemService(
        this.context,
        InputMethodManager::class.java
    )
    inputMethodManager?.hideSoftInputFromWindow(iBinder, 0)
    if (this.hasFocus())
        this.clearFocus()
}

fun NestedScrollView.elevationAppBar(appBarLayout: AppBarLayout) {
    //ViewCompat.setElevation(appBarLayout, 4f)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY == 0)
                appBarLayout.elevation = 2f.dpToPx().toFloat()
            else
                appBarLayout.elevation = 5f.dpToPx().toFloat()
        }
    }
}

fun RecyclerView.elevationAppBar(appBarLayout: AppBarLayout) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        var overallYScroll = 0
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            overallYScroll += dy
            if (overallYScroll > 0)
                appBarLayout.elevation = 5f.dpToPx().toFloat()
            else
                appBarLayout.elevation = 2f.dpToPx().toFloat()
        }
    })
}

fun View.startAnimationExpand(isOpen: Boolean = true, callBackEnd: () -> Unit) {

    val actualHeight: Int
    val animation = if (isOpen) {
        measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        actualHeight = measuredHeight

        layoutParams.height = 0
        visible()
        object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (actualHeight * interpolatedTime).toInt()
                requestLayout()
            }
        }
    } else {
        actualHeight = measuredHeight
        object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    gone()
                } else {
                    layoutParams.height = actualHeight - (actualHeight * interpolatedTime).toInt()
                    requestLayout()
                }
            }
        }
    }
    animation.duration = (actualHeight / context.resources.displayMetrics.density).toLong()
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {}

        override fun onAnimationEnd(p0: Animation?) {
            callBackEnd.invoke()
        }

        override fun onAnimationRepeat(p0: Animation?) {}

    })
    startAnimation(animation)
}

fun EditText.textChangedListener(
    before: ((text: CharSequence?) -> Unit)? = null,
    on: ((text: CharSequence?) -> Unit)? = null,
    after: ((text: CharSequence?) -> Unit)? = null
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            before?.invoke(p0)
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            on?.invoke(p0)
        }

        override fun afterTextChanged(p0: Editable?) {
            after?.invoke(p0)
        }
    })
}

fun <T> T?.dataNotNullView(vararg view: Pair<String, View?>) {
    if (this != null) {
        view.forEach {
            when (it.first) {
                "load" -> it.second?.gone()
                "fail" -> it.second?.gone()
                "filled" -> it.second?.visible()
            }
        }
    } else {
        view.forEach {
            when (it.first) {
                "load" -> it.second?.gone()
                "filled" -> it.second?.gone()
                "fail" -> it.second?.visible()
            }
        }
    }
}
inline fun <reified T> List<T>?.dataControlView(vararg view: Pair<String, View?>) {
    if (isNotNullOrEmpty()) {
        view.forEach {
            Log.e(T::class.java.simpleName, "${it.first} ${it.second}")
            when (it.first) {
                "load" -> it.second?.gone()
                "fail" -> it.second?.gone()
                "filled" -> it.second?.visible()
            }
        }
    } else {
        view.forEach {
            when (it.first) {
                "load" -> it.second?.gone()
                "filled" -> it.second?.gone()
                "fail" -> it.second?.visible()
            }
        }
    }
}

inline fun <reified T> Resource<List<T>>.dataListControllerView(vararg view: Pair<String, View?>, swipeRefresh: SwipeRefreshLayout? = null) {
    when(this) {
        is Resource.Error -> {
            swipeRefresh?.isRefreshing = false
            if (data == null) {
                view.forEach {
                    when (it.first) {
                        "load" -> it.second?.gone()
                        "filled" -> it.second?.gone()
                        "fail" -> it.second?.visible()
                    }
                }
            } else data?.dataControlView(*view)
        }
        is Resource.Loading -> {
            if (swipeRefresh?.isRefreshing == true) {
                data?.dataControlView(*view)
            } else if (data.isNotNullOrEmpty()) {
                view.forEach {
                    when (it.first) {
                        "load" -> it.second?.gone()
                        "fail" -> it.second?.gone()
                        "filled" -> it.second?.visible()
                    }
                }
            } else {
                var doubleAction: View? = null
                view.forEach {
                    when (it.first) {
                        "filled" -> {
                            if (doubleAction?.equals(it.second) != true) it.second?.gone()
                        }
                        "fail" -> it.second?.gone()
                        "load" -> it.second?.visible().also { v ->
                            doubleAction = v
                        }
                    }
                }
            }
        }
        is Resource.Success -> {
            swipeRefresh?.isRefreshing = false
            data.dataControlView(*view)
        }
    }
}
inline fun <reified T> ApiResponse<List<T>?>.dataListControllerView(vararg view: Pair<String, View?>) {
    when(this) {
        is ApiResponse.Error -> {
            view.forEach {
                when (it.first) {
                    "load" -> it.second?.gone()
                    "filled" -> it.second?.gone()
                    "fail" -> it.second?.visible()
                }
            }
        }
        is ApiResponse.Loading -> {
            view.forEach {
                when (it.first) {
                    "filled" -> it.second?.gone()
                    "fail" -> it.second?.gone()
                    "load" -> it.second?.visible()
                }
            }
        }
        is ApiResponse.Success -> {
            data.dataControlView(*view)
        }
    }
}

fun <T> Resource<List<T>>.submitDataController(baseAdapter: BaseAdapter<T, *>) {
    when(this) {
        is Resource.Error -> {
            baseAdapter.submitList(data)
        }
        is Resource.Loading -> {
            baseAdapter.submitList(data)
        }
        is Resource.Success -> {
            baseAdapter.submitList(data)
        }
    }
}
fun <T> ApiResponse<List<T>?>.submitDataController(baseAdapter: BaseAdapter<T, *>) {
    when(this) {
        is ApiResponse.Error -> {}
        is ApiResponse.Loading -> {}
        is ApiResponse.Success -> {
            baseAdapter.submitList(data)
        }
    }
}

fun <T> ApiResponse<List<T>?>.submitDataController(baseAdapter: BaseAdapter<T, *>, vararg view: Pair<String, View?>) {
    when(this) {
        is ApiResponse.Error -> {
            if (baseAdapter.itemCount <=0) this.dataControllerView(*view)
        }
        is ApiResponse.Loading -> {
            if (baseAdapter.itemCount <= 0) this.dataControllerView(*view)

        }
        is ApiResponse.Success -> {
            this.data.submitDataController(baseAdapter, *view)
        }
    }
}

fun <T> List<T>?.submitDataController(baseAdapter: BaseAdapter<T, *>, vararg view: Pair<String, View?>) {
    if (isNotNullOrEmpty()) {
        view.forEach {
            when (it.first) {
                "load" -> it.second?.gone()
                "fail" -> it.second?.gone()
                "filled" -> it.second?.visible()
            }
        }
        baseAdapter.submitList(this)
    } else {
        view.forEach {
            when (it.first) {
                "load" -> it.second?.gone()
                "filled" -> it.second?.gone()
                "fail" -> it.second?.visible()
            }
        }
    }
}

fun <T> ApiResponse<T>.dataControllerView(vararg view: Pair<String, View?>) {
    when(this) {
        is ApiResponse.Error -> {
            view.forEach {
                when (it.first) {
                    "load" -> it.second?.gone()
                    "filled" -> it.second?.gone()
                    "fail" -> it.second?.visible()
                }
            }
        }
        is ApiResponse.Loading -> {
            view.forEach {
                when (it.first) {
                    "filled" -> it.second?.gone()
                    "fail" -> it.second?.gone()
                    "load" -> it.second?.visible()
                }
            }
        }
        is ApiResponse.Success -> data.dataNotNullView(*view)
    }
}
fun <T> Resource<T>.dataControllerView(vararg view: Pair<String, View?>) {
    when(this) {
        is Resource.Error -> data.dataNotNullView(*view)
        is Resource.Loading -> data.dataNotNullView(*view)
        is Resource.Success -> data.dataNotNullView(*view)
    }
}

inline fun <T> Resource<T>.dataController(crossinline call: (T?) -> Unit) {
    when(this) {
        is Resource.Error -> call.invoke(data)
        is Resource.Loading -> call.invoke(data)
        is Resource.Success -> call.invoke(data)
    }
}

inline fun <T> ApiResponse<T>.dataControllerSuccess(crossinline call: (T) -> Unit) {
    when(this) {
        is ApiResponse.Error -> {}
        is ApiResponse.Loading -> {}
        is ApiResponse.Success -> data?.let { call.invoke(it) }
    }
}

inline fun <T> ApiResponse<T>.dataControllerError(crossinline call: (GeneralResponse) -> Unit) {
    when(this) {
        is ApiResponse.Error -> call.invoke(error)
        is ApiResponse.Loading -> {}
        is ApiResponse.Success -> {}
    }
}

inline fun <reified T: View, reified V: Any> View.valAnim(duration: Long, from: V, to: V, crossinline updater: (T, V) -> Unit) = ValueAnimator().apply {
    repeatMode = ValueAnimator.REVERSE
    this.duration = duration
    interpolator = LinearInterpolator()
    when(from) {
        is Int -> {
            setIntValues(from, to as Int)
            addUpdateListener { updater.invoke(this@valAnim as T, it.animatedValue as V) }
        }
        is Float -> {
            setFloatValues(from, to as Float)
            addUpdateListener { updater.invoke(this@valAnim as T, it.animatedValue as V) }
        }
    }

    this.start()
}

fun TextView.copyText() {
    this.context.copyText(this.text.toString())
}

fun View.setMargins(start: Int = -1, top: Int = -1, end: Int = -1, bottom: Int = -1) {
    if (this.layoutParams is MarginLayoutParams) {
        (this.layoutParams as MarginLayoutParams).setMargins(
            if (start >= 0) start else marginStart,
            if (top >= 0) top else marginTop,
            if (end >= 0) end else marginEnd,
            if (bottom >= 0) bottom else marginBottom)
    }
}

fun ImageView.loadImageSrc(url: String) {
    ImageViewBinding.imgSrc(this, url)
}

fun ImageView.loadImageNoPlaceholder(url: String) {
    ImageViewBinding.imgSrcNoPlaceholder(this, url)
}

fun ImageView.imgSrcCircle(url: String) {
    ImageViewBinding.imgSrc(this, url)
}

fun ImageView.loadImageRound(url: String) {
    ImageViewBinding.imgSrcRound(this, url)
}