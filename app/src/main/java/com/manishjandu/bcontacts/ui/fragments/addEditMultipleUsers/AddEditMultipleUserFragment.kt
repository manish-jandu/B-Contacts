package com.manishjandu.bcontacts.ui.fragments.addEditMultipleUsers

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.manishjandu.bcontacts.utils.AlarmManagerUtil
import com.manishjandu.bcontacts.utils.Constants.CONTACT_SELECTED
import com.manishjandu.bcontacts.utils.Constants.FRAGMENT_SELECT_MULTIPLE_CONTACT_REQUEST_KEY
import com.manishjandu.bcontacts.utils.Constants.MULTIPLE_USER_MESSAGE_REQUEST_CODE
import com.manishjandu.bcontacts.utils.TimeUtil.getDateInString
import com.manishjandu.bcontacts.utils.TimeUtil.getTimeInAmPm
import com.manishjandu.bcontacts.utils.enums.FileType
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
    private val args: AddEditMultipleUserFragmentArgs by navArgs()

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
    @Named("AlarmMangerUtil")
    lateinit var alarmManagerUtil: AlarmManagerUtil

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding=FragmentAddEditMessageBinding.bind(view)

        multipleUserMessageId=args.multipleUserMessageId
        val fileType=args.fileType

        if (fileType == FileType.NEW) {
            multipleUserMessageId=null
        } else {
            viewModel.getMessage(multipleUserMessageId!!)
        }

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
                textViewShowDate.text=getDateInString(it.day, it.month + 1, it.year)
                textViewShowTime.text=getTimeInAmPm(it.hour - 1, it.minutes)
                editTextMessage.setText(it.message)
                selectedContacts=it.contacts as ArrayList<Contact>
                adapter.submitList(selectedContacts)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditMessageEvent.collect { event ->
                when (event) {
                    is AddEditMessageEvent.MessageAddingSuccessful -> {
                        findNavController().popBackStack()
                    }
                    is AddEditMessageEvent.SetAlarm -> {
                        val message=event.multipleUserMessage
                        val phoneNumbers=getContactsInString(message.contacts)
                        val requestCode = MULTIPLE_USER_MESSAGE_REQUEST_CODE+message.multipleUserMessageId
                        alarmManagerUtil.setAlarm(
                            requireContext(),
                            message.timeInMillis,
                            requestCode,
                            message.message,
                            phoneNumbers
                        )
                    }
                    is AddEditMessageEvent.CancelAlarm -> {
                        val requestCode = MULTIPLE_USER_MESSAGE_REQUEST_CODE+event.messageId
                        alarmManagerUtil.cancelAlarm(requireContext(),requestCode)
                    }
                }
            }
        }

    }

    private fun getContactsInString(contacts: List<Contact>): String {
        var contactsInString=""
        for (i in contacts) {
            contactsInString+="${i.phone},"
        }
        return contactsInString
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

        Log.i(TAG, "saveMessage: multipleUserMessageId id is $multipleUserMessageId")

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}