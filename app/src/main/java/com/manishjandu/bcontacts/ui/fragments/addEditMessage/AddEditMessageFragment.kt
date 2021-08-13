package com.manishjandu.bcontacts.ui.fragments.addEditMessage

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.data.local.entities.Message
import com.manishjandu.bcontacts.databinding.FragmentAddEditMessageBinding
import com.manishjandu.bcontacts.utils.AlarmManagerUtil
import com.manishjandu.bcontacts.utils.TimeUtil.getDateInString
import com.manishjandu.bcontacts.utils.TimeUtil.getTimeInAmPm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
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

    @Inject
    @Named("AlarmMangerUtil")
    lateinit var alarmManagerUtil: AlarmManagerUtil

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentAddEditMessageBinding.bind(view)

        val contactId=arguments.contactId
        val contactNumber=arguments.phone
        val messageId=arguments.messageId

        viewModel.getMessage(messageId)

        textViewShowDate=binding.textViewShowDate
        textViewShowTime=binding.textViewShowTime
        editTextMessage=binding.editTextMessage

        binding.buttonTimePicker.setOnClickListener {
            showDatePicker()
        }

        binding.buttonSaveMessage.setOnClickListener {
            val message=editTextMessage.text.toString()
            if (minutes == null || day == null) {
                showErrorTimeNotSelected()
            } else if (message.isEmpty()) {
                showErrorEmptyMessage()
            } else {
                val timeInMills=createTimeInMills()
                val message=Message(
                    minutes!!,
                    hour,
                    day!!,
                    month,
                    year,
                    timeInMills,
                    message,
                    contactNumber,
                    contactId,
                    messageId
                )
                viewModel.setFutureMessage(message)
            }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            it?.let {
                textViewShowDate.text=getDateInString(it.day, it.month+1, it.year)
                textViewShowTime.text=getTimeInAmPm(it.hour-1, it.minutes)
                editTextMessage.setText(it.message)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditMessageEvent.collect { event ->
                when (event) {
                    is AddEditMessageViewModel.AddEditMessageEvent.MessageAddingSuccessful -> {
                        findNavController().popBackStack()
                    }
                    is AddEditMessageViewModel.AddEditMessageEvent.SetAlarm -> {
                        alarmManagerUtil.setAlarm(
                            requireContext(),
                            event.message.timeInMillis,
                            event.message.messageId,
                            event.message.message,
                            event.message.phone
                        )
                    }
                    is AddEditMessageViewModel.AddEditMessageEvent.CancelAlarm -> {
                        alarmManagerUtil.cancelAlarm(requireContext(),event.messageId)
                    }
                }
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

            val timeInAmPm=getTimeInAmPm(hour-1, minutes!!)
            textViewShowTime.text=timeInAmPm
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

            val dateInString=getDateInString(day!!, month+1, year)
            textViewShowDate.text=dateInString

            showTimePicker()
        }
    }
}