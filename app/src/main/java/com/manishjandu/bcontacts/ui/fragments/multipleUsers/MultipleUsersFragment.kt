package com.manishjandu.bcontacts.ui.fragments.multipleUsers

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.FragmentMessagesBinding
import com.manishjandu.bcontacts.ui.fragments.multipleUsers.MultipleUsersViewModel.MessageEvent
import com.manishjandu.bcontacts.utils.AlarmManagerUtil
import com.manishjandu.bcontacts.utils.Constants
import com.manishjandu.bcontacts.utils.checkSmsPermission
import com.manishjandu.bcontacts.utils.enums.FileType
import com.manishjandu.bcontacts.utils.setSmsPermission
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
    @Named("AlarmMangerUtil")
    lateinit var alarmManagerUtil: AlarmManagerUtil

    override fun onStart() {
        super.onStart()
        if (!checkSmsPermission()) {
            setSmsPermission{ viewModel.getMessages()}
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
                                alarmManagerUtil.setAlarm(
                                    requireContext(),
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