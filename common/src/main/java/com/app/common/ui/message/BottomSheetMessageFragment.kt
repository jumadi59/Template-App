package com.app.common.ui.message

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.app.common.databinding.FragmentBottomSheetMessageBinding
import com.app.common.ui.bindings.ImageViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetMessageFragment : BottomSheetDialogFragment() {


    companion object {
        fun newInstance(
            logo: String?,
            title: String,
            subTitle: String?,): BottomSheetMessageFragment {
            val args = Bundle()
            args.putString("logo", logo)
            args.putString("title", title)
            args.putString("subTitle", subTitle)
            val f = BottomSheetMessageFragment()
            f.arguments = args
            return f
        }
    }

    private var _binding: FragmentBottomSheetMessageBinding? = null
    private val binding get() = _binding!!

    var callbackActionClick: (() -> Unit?)? = null
    var callbackDismissClick: (() -> Unit?)? = null
    private var title: String?= null
    private var subTitle: String? = null
    private var icon: String? = null
    private var textButtonAction = ""
    private var isShowButtonClose = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetMessageBinding.inflate(inflater, container, false)
        icon = arguments?.getString("logo")
        title = arguments?.getString("title")
        subTitle = arguments?.getString("subTitle")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments

        ImageViewBinding.imgSrcNoPlaceholder(binding.ivLogo, icon)
        binding.title = title
        binding.subtitle = subTitle

        binding.arrowBack.isVisible = isShowButtonClose

        binding.arrowBack.setOnClickListener {
            dismiss()
        }

        if (textButtonAction.isNotEmpty()) binding.btnAction.text = textButtonAction

        binding.btnAction.isVisible = callbackActionClick != null
        binding.btnAction.setOnClickListener {
            callbackActionClick?.invoke()
            dismiss()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        callbackDismissClick?.invoke()
    }

    fun setButtonActionText(text: String) : BottomSheetMessageFragment {
        textButtonAction = text
        return this
    }

    fun isShowButtonClose(b: Boolean) : BottomSheetMessageFragment {
        isShowButtonClose = b
        return this
    }

    fun setIcon(icon: String) : BottomSheetMessageFragment {
        this.icon = icon
        return this
    }

    fun setCallbackDismiss(callbackDismissClick: (() -> Unit)?) : BottomSheetMessageFragment {
        this.callbackDismissClick = callbackDismissClick
        return this
    }

    fun setCallbackActionClick(callbackActionClick: (() -> Unit)?) : BottomSheetMessageFragment {
        this.callbackActionClick = callbackActionClick
        return this
    }

}