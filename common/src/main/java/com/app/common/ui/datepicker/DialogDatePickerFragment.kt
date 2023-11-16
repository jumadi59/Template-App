package com.app.common.ui.datepicker

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.app.common.databinding.FragmentDialogDatePickerBinding
import com.app.core.extensions.toDateFormat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*


/**
 * Created by Anonim date on 23/10/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
class DialogDatePickerFragment : BottomSheetDialogFragment() {

    companion object {
        fun show(activity: FragmentActivity, callbackOk: (Int, Int,Int) -> Unit, currentDay: Int = 0,currentMonth: Int = 0,currentYear: Int = 0, years: Array<String>?= null) =
            DialogDatePickerFragment().apply {
                this.callbackOk = callbackOk
                val args = Bundle()
                args.putInt("currentDay", currentDay)
                args.putInt("currentMonth", currentMonth)
                args.putInt("currentYear", currentYear)
                if (years !=null) args.putStringArray("years", years)
                arguments = args
                show(activity.supportFragmentManager, "DialogDatePickerFragment")
            }

        fun show(fragment: Fragment, callbackOk: (Int, Int,Int) -> Unit, currentDay: Int = 0,currentMonth: Int = 0,currentYear: Int = 0, years: Array<String>?= null) =
            DialogDatePickerFragment().apply {
                this.callbackOk = callbackOk
                val args = Bundle()
                args.putInt("currentDay", currentDay)
                args.putInt("currentMonth", currentMonth)
                args.putInt("currentYear", currentYear)
                if (years !=null) args.putStringArray("years", years)
                arguments = args
                show(fragment.childFragmentManager, "DialogDatePickerFragment")
            }
    }


    private var years = generateYearString(100)
    private var callbackOk: (Int, Int, Int) -> Unit = { _: Int, _: Int, _: Int -> }
    private var currentDay: Int? = null
    private var currentMonth: Int? = null
    private var currentYear: Int? = null
    private var _binding: FragmentDialogDatePickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentDay = arguments?.getInt("currentDay")
        currentMonth = arguments?.getInt("currentMonth")
        currentYear = arguments?.getInt("currentYear")
        if (arguments?.containsKey("years") == true) {
            years = requireArguments().getStringArray("years") as Array<String>
        }
    }

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
        _binding = FragmentDialogDatePickerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val months = getMonthNames()

        binding.pickerDay.setOnScrollListener { _, _ ->
            setCalenderUI(binding.pickerDay.value+1, binding.pickerMonth.value, years[binding.pickerYear.value].toInt())
        }

        binding.pickerMonth.refreshByNewDisplayedValues(months)
        binding.pickerMonth.setOnScrollListener { _, _ ->
            setCalender(binding.pickerDay.value, binding.pickerMonth.value, years[binding.pickerYear.value].toInt())
        }

        binding.pickerYear.refreshByNewDisplayedValues(years)
        binding.pickerYear.setOnScrollListener { _, _ ->
            setCalender(binding.pickerDay.value, binding.pickerMonth.value, years[binding.pickerYear.value].toInt())
        }

        val day = (currentDay?: Calendar.getInstance().get(Calendar.DAY_OF_MONTH))-1
        val month = currentMonth?: Calendar.getInstance().get(Calendar.MONTH)
        val year = years.indexOf((currentYear ?: Calendar.getInstance().get(Calendar.YEAR)).toString())

        binding.pickerYear.value = year
        binding.pickerMonth.value = month
        setCalender(day, month, currentYear ?: Calendar.getInstance().get(Calendar.YEAR))

        binding.btnOk.setOnClickListener {
            callbackOk.invoke(binding.pickerDay.value+1, binding.pickerMonth.value, years[binding.pickerYear.value].toInt())
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
            val nn = num+1
            val h = if ("$nn".length == 1) "0$nn" else "$nn"
            arr[num] = h
        }
        return arr
    }

    private fun generateYearString(count: Int) : Array<String> {
        val cal: Calendar = Calendar.getInstance()
        val arr = Array(count) {""}
        for ( num in 0 until count) {
            arr[num] = (cal.get(Calendar.YEAR) - num).toString()
        }
        return arr
    }

    private fun getMonthNames(): Array<String> {
        val months: ArrayList<String> = ArrayList()
        for (i in 0..11) {
            val cal = Calendar.getInstance()
            cal[Calendar.MONTH] = i
            months.add(cal.time.time.toDateFormat("MMM"))
        }
        return months.toTypedArray()
    }

    private fun setCalender(mDay: Int, mMonth: Int, mYear: Int) {
        val cal: Calendar = Calendar.getInstance()
        cal[Calendar.YEAR] = mYear
        cal[Calendar.MONTH] = mMonth
        cal[Calendar.DAY_OF_MONTH] = mDay+1
        Log.e(this::class.java.simpleName, "mMonth $mMonth mYear $mYear")

        val days = generateNumberString(cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        Log.e(this::class.java.simpleName, "days ${days.size}")
        binding.pickerDay.refreshByNewDisplayedValues(days)
        binding.pickerDay.value = if (mDay >= 0 && mDay < days.size) mDay else days.size-1
        setCalenderUI(mDay+1, mMonth, mYear)
    }

    private fun setCalenderUI(mDay: Int, mMonth: Int, mYear: Int) {
        val cal: Calendar = Calendar.getInstance()
        cal[Calendar.YEAR] = mYear
        cal[Calendar.MONTH] = mMonth
        cal[Calendar.DAY_OF_MONTH] = mDay

        Log.e(this::class.java.simpleName, "$mDay, $mMonth, $mYear")

        binding.tvDate.post {
            binding.tvDate.text = cal.time.time.toDateFormat("EEE, dd MMM yyyy")
        }
    }
}