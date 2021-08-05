package com.manishjandu.bcontacts.ui.fragments.multipleUsers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.assent.*
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.FragmentMessagesBinding
import com.manishjandu.bcontacts.ui.fragments.multipleUsers.MultipleUsersViewModel.MessageEvent
import com.manishjandu.bcontacts.utils.Constants
import com.manishjandu.bcontacts.utils.enums.FileType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MultipleUsersFragment : Fragment(R.layout.fragment_messages) {
    private val viewModel: MultipleUsersViewModel by viewModels()
    private var _binding: FragmentMessagesBinding?=null
    private val binding get()=_binding!!
    private val messageAdapter=MultiUserAdapter(OnMessageClick())

    @Inject
    @Named("futureMessageIntent")
    lateinit var intent: Intent

    @Inject
    lateinit var alarmManager: AlarmManager

    override fun onStart() {
        super.onStart()
        if (!checkSmsPermission()) {
            setSmsPermission()
        } else {
            viewModel.getMessages()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding=FragmentMessagesBinding.bind(view)

        binding.floatingButtonAddMessage.setOnClickListener {
            val action=
                MultipleUsersFragmentDirections.actionMultipleUsersFragmentToAddEditMultipleUserFragment(
                    FileType.NEW,
                    0
                )
            findNavController().navigate(action)
        }

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        binding.apply {
            recyclerViewMessages.adapter=messageAdapter
            recyclerViewMessages.layoutManager=LinearLayoutManager(requireContext())

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val multipleUserMessage=messageAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.removeMessage(multipleUserMessage)
                }
            }).attachToRecyclerView(recyclerViewMessages)
        }
    }

    private fun setupObservers() {
        viewModel.messages.observe(viewLifecycleOwner) {
            it?.let {
                messageAdapter.submitList(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.messageEvent.collect { event ->
                when (event) {
                    is MessageEvent.ShowUndoMessageDelete -> {
                        Snackbar.make(requireView(), "Message Deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                val message=event.multipleUserMessage
                                val phoneNumbers=getContactsInString(message.contacts)
                                val requestCode=
                                    Constants.MULTIPLE_USER_MESSAGE_REQUEST_CODE + message.multipleUserMessageId
                                setAlarm(
                                    message.timeInMillis,
                                    requestCode,
                                    message.message,
                                    phoneNumbers
                                )
                                viewModel.addDeletedMessage(message)
                            }.setActionTextColor(Color.RED)
                            .show()
                    }
                    is MessageEvent.CancelAlarm -> {
                        val requestCode=
                            Constants.MULTIPLE_USER_MESSAGE_REQUEST_CODE + event.messageId
                        cancelAlarm(requestCode)
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


    private fun checkSmsPermission(): Boolean {
        return isAllGranted(Permission.SEND_SMS)
    }

    private fun setSmsPermission() {
        askForPermissions(
            Permission.SEND_SMS,
        ) { result ->

            if (result.isAllGranted()) {
                viewModel.getMessages()
            }

            if (result[Permission.SEND_SMS] == GrantResult.DENIED
            ) {
                showSnackBarOnPermissionError("Permission is required", "Ok") {
                    setSmsPermission()
                }
            }

            if (result[Permission.SEND_SMS] == GrantResult.PERMANENTLY_DENIED
            ) {
                showSnackBarOnPermissionError("Goto Settings Page", "Settings") {
                    showSystemAppDetailsPage()
                }
            }

        }
    }

    private fun showSnackBarOnPermissionError(
        message: String,
        textAction: String,
        action: () -> Unit
    ) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setAction(textAction) {
                action()
            }.setActionTextColor(Color.RED)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    inner class OnMessageClick : MultiUserAdapter.OnMessageClick {
        override fun onRootClick(messageId: Int) {
            val action=
                MultipleUsersFragmentDirections.actionMultipleUsersFragmentToAddEditMultipleUserFragment(
                    FileType.EDIT,
                    messageId
                )
            findNavController().navigate(action)
        }

    }
}