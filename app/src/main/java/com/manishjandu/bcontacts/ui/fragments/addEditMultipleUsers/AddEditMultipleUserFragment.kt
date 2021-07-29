package com.manishjandu.bcontacts.ui.fragments.addEditMultipleUsers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.FragmentAddEditMessageBinding
import com.manishjandu.bcontacts.ui.fragments.addEditMultipleUsers.AddEditMultipleUserAdapter.OnSelectedContactClick
import com.manishjandu.bcontacts.ui.fragments.addEditMultipleUsers.AddEditMultipleUserViewModel.AddEditMessageEvent
import com.manishjandu.bcontacts.utils.Constants.CONTACT_SELECTED
import com.manishjandu.bcontacts.utils.Constants.FRAGMENT_SELECT_MULTIPLE_CONTACT_REQUEST_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject
import javax.inject.Named

private const val TAG="AddEditMultipleUserFrag"

@AndroidEntryPoint
class AddEditMultipleUserFragment : Fragment(R.layout.fragment_add_edit_message) {
    private var _binding: FragmentAddEditMessageBinding?=null
    private val binding get()=_binding!!
    private val viewModel: AddEditMultipleUserViewModel by viewModels()
    private var selectedContacts=arrayListOf<Contact>()
    private lateinit var adapter: AddEditMultipleUserAdapter
    private lateinit var textViewShowDate: TextView
    private lateinit var textViewShowTime: TextView
    private lateinit var editTextMessage: EditText
    private var multipleUserMessageId: Int?=null
    private val calendar=Calendar.getInstance()
    private var minutes: Int?=null
    private var hour=0
    private var day: Int?=null
    private var month=0
    private var year=0

    @Inject
    @Named("futureMessageIntent")
    lateinit var intent: Intent

    @Inject
    lateinit var alarmManager: AlarmManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding=FragmentAddEditMessageBinding.bind(view)

        //nav args if enum new : message id = null
        //if enum edit : message id = nav args

        textViewShowDate=binding.textViewShowDate
        textViewShowTime=binding.textViewShowTime
        editTextMessage=binding.editTextMessage

        setupMultipleUserFragment()
        setupRecyclerViewForSelectedContacts()
        setupButtons()
        setupResultListener()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.message.observe(viewLifecycleOwner) {
            it?.let {
                textViewShowDate.text=getDateInString(it.day, it.month, it.year)
                textViewShowTime.text=getTimeInAmPm(it.hour, it.minutes)
                editTextMessage.setText(it.message)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditMessageEvent.collect { event ->
                when (event) {
                    is AddEditMessageEvent.MessageAddingSuccessful -> {
                        findNavController().popBackStack()
                    }
                    is AddEditMessageEvent.SetAlarm -> {
                        setAlarm(
                            event.message.timeInMillis,
                            event.message.messageId,
                            event.message.message,
                            event.message.phone
                        )
                    }
                    is AddEditMessageEvent.CancelAlarm -> {
                        cancelAlarm(event.messageId)
                    }
                }
            }
        }

    }

    private fun setupButtons() {
        binding.apply {
            imageButtonSelectContact.setOnClickListener {
                val action=
                    AddEditMultipleUserFragmentDirections.actionAddEditMultipleUserFragmentToSelectMultipleContactFragment()
                findNavController().navigate(action)
            }

            buttonSaveMessage.setOnClickListener {
                saveMessage()
            }

            buttonTimePicker.setOnClickListener {
                selectDataTime()
            }
        }
    }

    private fun setupRecyclerViewForSelectedContacts() {
        adapter=AddEditMultipleUserAdapter(OnClick())

        binding.apply {
            recyclerViewSelectedContacts.adapter=adapter
            recyclerViewSelectedContacts.layoutManager=LinearLayoutManager(requireContext())
        }

    }

    private fun setupResultListener() {
        setFragmentResultListener(FRAGMENT_SELECT_MULTIPLE_CONTACT_REQUEST_KEY) { _, bundle ->
            val result=bundle.getParcelableArrayList<Contact>(CONTACT_SELECTED)

            result?.let {
                selectedContacts=it
                adapter.submitList(it)
            }
        }
    }

    private fun setupMultipleUserFragment() {
        binding.apply {
            linearLayoutSelectedContact.visibility=View.VISIBLE
            imageButtonSelectContact.visibility=View.VISIBLE
        }
    }

    private fun selectDataTime() {
        showDatePicker()
    }

    private fun saveMessage() {
        val message=editTextMessage.text.toString()

        if (minutes == null || day == null) {
            showErrorTimeNotSelected()
        } else if (message.isEmpty()) {
            showErrorEmptyMessage()
        } else {
            val timeInMills=createTimeInMills()
            viewModel.setFutureMessage(
                multipleUserMessageId,
                selectedContacts,
                message,
                minutes!!,
                hour,
                day!!,
                month,
                year,
                timeInMills,
                )
        }
    }


    inner class OnClick() : OnSelectedContactClick {
        override fun onRemoveContact(position: Int) {
            val contact=adapter.currentList[position]
            selectedContacts.remove(contact)

            adapter.submitList(selectedContacts)
            adapter.notifyItemRemoved(position)
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

            showTimePicker()
        }
    }

    private fun getDateInString(day: Int, month: Int, year: Int): String {
        return "$day-$month-$year"
    }

    private fun getTimeInAmPm(hour: Int, minute: Int): String {
        return if (hour > 12) {
            String.format("%02d", hour - 12 + 1) + ":" + String.format(
                "%02d",
                minute
            ) + "PM"
        } else {
            String.format("%02d", hour + 1) + ":" + String.format(
                "%02d",
                minute
            ) + "AM"
        }
    }

    private fun setAlarm(
        timeInMillis: Long,
        requestCode: Int,
        message: String,
        contactNumber: String
    ) {
        intent.putExtra("message", message)
        intent.putExtra("contactNumber", contactNumber)
        val pendingIntent=PendingIntent.getBroadcast(requireContext(), requestCode, intent, 0)

        alarmManager.set(
            AlarmManager.RTC,
            timeInMillis,
            pendingIntent
        )
        Toast.makeText(requireContext(), "Message is set", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm(requestCode: Int) {
        val pendingIntent=PendingIntent.getBroadcast(requireContext(), requestCode, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}