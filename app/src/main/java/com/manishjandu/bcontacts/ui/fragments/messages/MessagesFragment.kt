package com.manishjandu.bcontacts.ui.fragments.messages

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.assent.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentMessagesBinding
import kotlinx.coroutines.flow.collect

class MessagesFragment : Fragment(R.layout.fragment_messages) {
    private val viewModel: MessagesViewModel by viewModels()
    private lateinit var binding: FragmentMessagesBinding
    private lateinit var floatingButtonAddMessage: FloatingActionButton
    private val messageAdapter=MessagesAdapter(OnMessageClick())
    private val arguments: MessagesFragmentArgs by navArgs()
    private var contactId: Long?=null

    override fun onStart() {
        super.onStart()
        if (!checkSmsPermission()) {
            floatingButtonAddMessage.visibility=View.GONE
            setSmsPermission()
        } else {
            floatingButtonAddMessage.visibility=View.VISIBLE
            viewModel.getMessages(contactId!!)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentMessagesBinding.bind(view)
        val recyclerView=binding.recyclerViewMessages
        floatingButtonAddMessage=binding.floatingButtonAddMessage

        contactId=arguments.contactId
        val contactNumber=arguments.phone

        recyclerView.adapter=messageAdapter
        recyclerView.layoutManager=LinearLayoutManager(requireContext())
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

        floatingButtonAddMessage.setOnClickListener {
            navigateToAddEditMessage(contactId!!, contactNumber, 0)
        }

        viewModel.messages.observe(viewLifecycleOwner) {
            messageAdapter.submitList(it.messages)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.messageEvent.collect { event ->
                when (event) {
                    is MessagesViewModel.MessageEvent.showUndoMessageDelete -> {
                        Snackbar.make(requireView(), "Message Deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                viewModel.addDeletedMessage(event.message)
                            }.setActionTextColor(Color.RED)
                    }
                }

            }
        }

    }

    inner class OnMessageClick : MessagesAdapter.OnMessageClick {
        override fun onRootClick(messageId: Int, contactId: Long, contactNumber: String) {
            navigateToAddEditMessage(contactId, contactNumber, messageId)
        }

    }

    private fun navigateToAddEditMessage(contactId: Long, contactNumber: String, messageId: Int) {
        val action=MessagesFragmentDirections.actionMessagesFragmentToAddEditMessageFragment(
            contactId, contactNumber, messageId
        )
        findNavController().navigate(action)
    }

    private fun checkSmsPermission(): Boolean {
        return isAllGranted(Permission.SEND_SMS)
    }

    private fun setSmsPermission() {
        askForPermissions(
            Permission.SEND_SMS,
        ) { result ->

            if (result.isAllGranted()) {
                floatingButtonAddMessage.visibility=View.VISIBLE
                viewModel.getMessages(contactId!!)
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

}