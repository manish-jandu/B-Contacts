package com.manishjandu.bcontacts.ui.fragments.addEditBirthday

import android.os.Bundle
import android.view.View
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
import com.manishjandu.bcontacts.data.local.entities.Birthday
import com.manishjandu.bcontacts.databinding.FragmentAddEditBirthdayBinding
import com.manishjandu.bcontacts.ui.fragments.addEditBirthday.AddEditBirthDayViewModel.AddEditBirthdayEvent
import com.manishjandu.bcontacts.utils.AlarmManagerUtil
import com.manishjandu.bcontacts.utils.Constants
import com.manishjandu.bcontacts.utils.TimeUtil.getDateInString
import com.manishjandu.bcontacts.utils.TimeUtil.getTimeInAmPm
import com.manishjandu.bcontacts.utils.checkSmsPermission
import com.manishjandu.bcontacts.utils.setSmsPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AddEditBirthDayFragment : Fragment(R.layout.fragment_add_edit_birthday) {
    private val viewModel: AddEditBirthDayViewModel by viewModels()
    private var _binding: FragmentAddEditBirthdayBinding?=null
    private val binding get()=_binding!!
    private val args: AddEditBirthDayFragmentArgs by navArgs()
    private lateinit var textViewShowDate: TextView
    private lateinit var textViewShowTime: TextView
    private var isFutureMessageSet=false
    private val calendar=Calendar.getInstance()
    private var minutes: Int?=null
    private var hour=0
    private var day: Int?=null
    private var month=0
    private var year=0

    @Inject
    @Named("AlarmMangerUtil")
    lateinit var alarmManagerUtil: AlarmManagerUtil

    override fun onStart() {
        super.onStart()
        if (!checkSmsPermission()) {
            setSmsPermission{}
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding=FragmentAddEditBirthdayBinding.bind(view)

        val contactNumber=args.contactNumber
        val requestCode=contactNumber.toLong().toInt()

        viewModel.getBirthday(requestCode)

        textViewShowDate=binding.textViewShowBdayDate
        textViewShowTime=binding.textViewShowBdayTime

        binding.apply {
            buttonSelectTime.setOnClickListener {
                showTimePicker()
            }
            buttonSelectDate.setOnClickListener {
                showDatePicker()
            }
            buttonSaveBirthday.setOnClickListener {
                setFutureBirthday(contactNumber, requestCode)
            }
            switchAutoMessage.setOnCheckedChangeListener { _, isChecked ->
                isFutureMessageSet=isChecked
            }
        }

        viewModel.birthday.observe(viewLifecycleOwner) {
            it?.let {
                minutes=it.minutes
                hour=it.hour
                day=it.day
                month=it.month
                year=it.year
                textViewShowTime.text=getTimeInAmPm(hour - 1, minutes!!)
                textViewShowDate.text=getDateInString(day!!, month + 1, year)
                binding.textViewBirthdayMessage.setText(it.birthdayMessage)
                binding.switchAutoMessage.setChecked(it.isAutoSet)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditBirthdayEvent.collect { event ->
                when (event) {
                    is AddEditBirthdayEvent.MessageAddingSuccessful -> {
                        findNavController().navigateUp()
                    }
                    is AddEditBirthdayEvent.SetAlarm -> {
                        val birthday=event.birthday
                        val requestCode=Constants.BIRTHDAY_REQUEST_CODE + birthday.requestCode
                        alarmManagerUtil.setAlarm(
                            requireContext(),
                            birthday.timeInMillis,
                            requestCode,
                            birthday.birthdayMessage,
                            birthday.contactNumber
                        )
                    }
                    is AddEditBirthdayEvent.CancelAlarm -> {
                        val birthday=event.birthday
                        val requestCode=Constants.BIRTHDAY_REQUEST_CODE + birthday.requestCode
                        alarmManagerUtil.cancelAlarm(
                            requireContext(),
                            requestCode
                        )
                    }
                }
            }
        }


    }

    private fun setFutureBirthday(contactNumber: String, requestCode: Int) {
        val message=binding.textViewBirthdayMessage.text.toString()
        if (minutes == null || day == null) {
            showErrorTimeNotSelected()
        } else if (message.isEmpty()) {
            showErrorEmptyMessage()
        } else {
            val timeInMillis=createTimeInMills()
            val message =Birthday(
                requestCode,
                contactNumber,
                message,
                isFutureMessageSet,
                timeInMillis,
                minutes!!,
                hour,
                day!!,
                month,
                year
            )
            viewModel.setFutureMessage(message)
        }
    }

    private fun createTimeInMills(): Long {
        calendar.set(year, month, day!!, hour, minutes!!)
        return calendar.timeInMillis
    }

    private fun showErrorTimeNotSelected() {
        Snackbar.make(requireView(), "Please Select date and time!", Snackbar.LENGTH_LONG).show()
    }

    private fun showErrorEmptyMessage() {
        Snackbar.make(requireView(), "Please write something in message box!", Snackbar.LENGTH_LONG)
            .show()
    }

    private fun showTimePicker() {
        var timePicker=MaterialTimePicker.Builder().setHour(12).setTitleText("Select Time").build()
        if (minutes != null) {
            timePicker=MaterialTimePicker.Builder().setHour(hour).setMinute(minutes!!)
                .setTitleText("Select Time").build()
        }

        timePicker.show(parentFragmentManager, "")

        timePicker.addOnPositiveButtonClickListener {
            minutes=String.format("%02d", timePicker.minute).toInt()
            hour=String.format("%02d", timePicker.hour).toInt()

            val timeInAmPm=getTimeInAmPm(hour - 1, minutes!!)
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

            val dateInString=getDateInString(day!!, month + 1, year)
            textViewShowDate.text=dateInString

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}