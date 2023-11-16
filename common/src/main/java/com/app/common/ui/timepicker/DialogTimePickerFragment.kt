package com.app.common.ui.timepicker

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.common.databinding.FragmentDialogTimePickerBinding
import com.app.core.extensions.formatDateTime
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DialogTimePickerFragment : BottomSheetDialogFragment() {

    companion object {
        fun show(activity: FragmentActivity, callbackOk: (String, String) -> Unit) =
            DialogTimePickerFragment().apply {
                this.callbackOk = callbackOk
                show(activity.supportFragmentManager, "DialogTimePickerFragment")
            }

        fun show(fragment: Fragment, callbackOk: (String, String) -> Unit) =
            DialogTimePickerFragment().apply {
                this.callbackOk = callbackOk
                show(fragment.childFragmentManager, "DialogTimePickerFragment")
            }
    }

    private var _binding: FragmentDialogTimePickerBinding? = null
    private val binding get() = _binding!!

    var startTime: String? = null
    private var callbackOk: (String, String) -> Unit = { _: String, _: String -> }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val bottomSheet =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(android.R.color.transparent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogTimePickerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val hours = generateNumberString(24)
        val minutes = generateNumberString(60)
        binding.pickerHour.refreshByNewDisplayedValues(hours)
        binding.pickerMinute.refreshByNewDisplayedValues(minutes)
        val time = startTime?.ifEmpty { "HH:mm".formatDateTime() }?:"HH:mm".formatDateTime()
        try {
            binding.pickerHour.value = hours.indexOf(hours.find { it == time.slice(0..1) })
            binding.pickerMinute.value = minutes.indexOf(minutes.find { it == time.slice(3..4) })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.btnOk.setOnClickListener {
            callbackOk.invoke(hours[binding.pickerHour.value], minutes[binding.pickerMinute.value])
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateNumberString(count: Int) : Array<String> {
        val arr = Array(count) {""}
        for ( num in 0 until count) {
            val h = if ("$num".length == 1) "0$num" else "$num"
            arr[num] = h
        }
        return arr
    }

}