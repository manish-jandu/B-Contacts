package com.manishjandu.bcontacts.ui.fragments.addEditMessage

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentAddEditMessageBinding
import java.util.*

class AddEditMessageFragment : Fragment(R.layout.fragment_add_edit_message) {
    private lateinit var binding: FragmentAddEditMessageBinding
    private val viewModel: AddEditMessageViewModel by viewModels()
    private val arguments: AddEditMessageFragmentArgs by navArgs()
    private lateinit var textViewShowDate: TextView
    private lateinit var textViewShowTime: TextView
    private lateinit var editTextMessage: EditText
    private val calendar=Calendar.getInstance()
    private var minutes: Int?=null
    private var hour=0
    private var day: Int?=null
    private var month=0
    private var year=0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentAddEditMessageBinding.bind(view)

        val contactId=arguments.contactId
        val contactNumber=arguments.phone
        val messageId=arguments.messageId

        textViewShowDate=binding.textViewShowDate
        textViewShowTime=binding.textViewShowTime
        editTextMessage=binding.editTextMessage

        binding.buttonTimePicker.setOnClickListener {
            showDatePicker()
            showTimePicker()
        }

        binding.buttonSaveMessage.setOnClickListener {
            val message=editTextMessage.text.toString()
            if (minutes == null || day == null) {
                showErrorTimeNotSelected()
            } else if (message.isEmpty()) {
                showErrorEmptyMessage()
            } else {
                val timeInMills=createTimeInMills()
                viewModel.setFutureMessage(
                    minutes!!,
                    hour,
                    day!!,
                    month,
                    year,
                    timeInMills,
                    message,
                    contactId,
                    contactNumber,
                    messageId,
                )
            }
        }

    }

    private fun showErrorTimeNotSelected() {
        Snackbar.make(requireView(), "Please Select date and time!", Snackbar.LENGTH_LONG).show()
    }

    private fun showErrorEmptyMessage() {
        Snackbar.make(requireView(), "Please write something in message box!", Snackbar.LENGTH_LONG)
            .show()
    }

    private fun createTimeInMills(): Long {
        calendar.set(year, month, day!!, hour, minutes!!)
        return calendar.timeInMillis

    }

    private fun showTimePicker() {
        val timePicker=MaterialTimePicker.Builder().setHour(12).setTitleText("Select Time").build()
        timePicker.show(parentFragmentManager, "")

        timePicker.addOnPositiveButtonClickListener {
            minutes=String.format("%02d", timePicker.minute).toInt()
            hour=String.format("%02d", timePicker.hour).toInt()

            val timeAmPm=if (timePicker.hour > 12) {
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

            textViewShowTime.text=timeAmPm

        }
    }


    private fun showDatePicker() {
        val datePicker=MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(
                CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build()
            )
            .setTitleText("Select Date").build()
        datePicker.show(parentFragmentManager, "")

        datePicker.addOnPositiveButtonClickListener {

            calendar.time=Date(it)

            day=calendar.get(Calendar.DAY_OF_MONTH)
            month=calendar.get(Calendar.MONTH)
            year=calendar.get(Calendar.YEAR)

            val date="$day-$month-$year"
            textViewShowDate.text=date

        }
    }

}