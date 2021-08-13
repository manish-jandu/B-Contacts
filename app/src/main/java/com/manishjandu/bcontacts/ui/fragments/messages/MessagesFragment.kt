package com.manishjandu.bcontacts.ui.fragments.messages

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentMessagesBinding
import com.manishjandu.bcontacts.utils.checkSmsPermission
import com.manishjandu.bcontacts.utils.setSmsPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MessagesFragment : Fragment(R.layout.fragment_messages) {
    private val viewModel: MessagesViewModel by viewModels()
    private lateinit var binding: FragmentMessagesBinding
    private lateinit var floatingButtonAddMessage: FloatingActionButton
    private val messageAdapter=MessagesAdapter(OnMessageClick())
    private val arguments: MessagesFragmentArgs by navArgs()
    private var contactId: Long?=null

    @Inject
    @Named("futureMessageIntent")
    lateinit var intent: Intent

    @Inject
    lateinit var alarmManager: AlarmManager

    override fun onStart() {
        super.onStart()
        if (!checkSmsPermission()) {
            floatingButtonAddMessage.visibility=View.GONE
            setSmsPermission {
                viewModel.getMessages(contactId!!)
                floatingButtonAddMessage.visibility=View.VISIBLE
            }
        } else {
            floatingButtonAddMessage.visibility=View.VISIBLE
            viewModel.getMessages(contactId!!)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentMessagesBinding.bind(view)

        floatingButtonAddMessage=binding.floatingButtonAddMessage

        contactId=arguments.contactId
        val contactNumber=arguments.phone

        setupRecyclerView()

        floatingButtonAddMessage.setOnClickListener {
            navigateToAddEditMessage(contactId!!, contactNumber, 0)
        }

        viewModel.messages.observe(viewLifecycleOwner) {
            messageAdapter.submitList(it.messages)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.messageEvent.collect { event ->
                when (event) {
                    is MessagesViewModel.MessageEvent.ShowUndoMessageDelete -> {
                        Snackbar.make(requireView(), "Message Deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                setAlarm(
                                    event.message.timeInMillis,
                                    event.message.messageId,
                                    event.message.message,
                                    event.message.phone
                                )
                                viewModel.addDeletedMessage(event.message)
                            }.setActionTextColor(Color.RED)
                            .show()
                    }
                    is MessagesViewModel.MessageEvent.CancelAlarm -> {
                        cancelAlarm(event.messageId)
                    }
                }

            }
        }

    }

    private fun setupRecyclerView() {
        binding.recyclerViewMessages.apply {
            adapter=messageAdapter
            layoutManager=LinearLayoutManager(requireContext())
        }

        binding.apply {
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
                    val message=messageAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.removeMessage(message)
                }
            }).attachToRecyclerView(recyclerViewMessages)
        }
    }

    inner class OnMessageClick : MessagesAdapter.OnMessageClick {
        override fun onRootClick(messageId: Int, contactId: Long, contactNumber: String) {
            navigateToAddEditMessage(contactId, contactNumber, messageId)
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

    private fun navigateToAddEditMessage(contactId: Long, contactNumber: String, messageId: Int) {
        val action=MessagesFragmentDirections.actionMessagesFragmentToAddEditMessageFragment(
            contactId, contactNumber, messageId
        )
        findNavController().navigate(action)
    }

}