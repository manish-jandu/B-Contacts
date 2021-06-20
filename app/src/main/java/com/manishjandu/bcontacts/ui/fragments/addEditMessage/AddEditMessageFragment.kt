package com.manishjandu.bcontacts.ui.fragments.addEditMessage

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentAddEditMessageBinding
import java.util.*

class AddEditMessageFragment : Fragment(R.layout.fragment_add_edit_message) {
    private lateinit var binding: FragmentAddEditMessageBinding
    private val arguments: AddEditMessageFragmentArgs by navArgs()
    private lateinit var textViewShowDate: TextView
    private lateinit var textViewShowTime: TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentAddEditMessageBinding.bind(view)

        val contactId=arguments.contactId
        val contactNumber=arguments.phone

        textViewShowDate=binding.textViewShowDate
        textViewShowTime=binding.textViewShowTime

        binding.buttonDatePicker.setOnClickListener {
            showDatePicker()
        }

        binding.buttonTimePicker.setOnClickListener {
            showTimePicker()
        }

    }

    private fun showTimePicker() {
        val timePicker=MaterialTimePicker.Builder().setHour(12).setTitleText("Select Time").build()
        timePicker.show(parentFragmentManager, "")

        timePicker.addOnPositiveButtonClickListener {
            var time=""

            time=if (timePicker.hour > 12) {
                String.format("%02d", timePicker.hour - 12) + ":" + String.format(
                    "%02d",
                    timePicker.minute
                ) + "PM"
            } else {
                String.format("%02d", timePicker.hour) + ":" + String.format(
                    "%02d",
                    timePicker.minute
                ) + "AM"

            }

            textViewShowTime.text=time

        }
    }


    private fun showDatePicker() {
        val datePicker=MaterialDatePicker.Builder.datePicker().setTitleText("Select Date").build()
        datePicker.show(parentFragmentManager, "")

        datePicker.addOnPositiveButtonClickListener {
            val calendar=Calendar.getInstance()
            calendar.time=Date(it)

            val day=calendar.get(Calendar.DAY_OF_MONTH)
            val month=calendar.get(Calendar.MONTH) + 1
            val year=calendar.get(Calendar.YEAR)
            val date="$day-$month-$year"

            textViewShowDate.text=date
        }

    }

}