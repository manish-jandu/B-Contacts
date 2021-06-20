package com.manishjandu.bcontacts.ui.fragments.messages

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.afollestad.assent.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentMessagesBinding

class MessagesFragment : Fragment(R.layout.fragment_messages) {
    private lateinit var binding: FragmentMessagesBinding
    private lateinit var floatingButtonAddMessage:FloatingActionButton
    private val arguments: MessagesFragmentArgs by navArgs()

    override fun onStart() {
        super.onStart()
        if(!checkSmsPermission()){
            floatingButtonAddMessage.visibility = View.GONE
            setSmsPermission()
        }else{
            floatingButtonAddMessage.visibility = View.VISIBLE
            //Todo:get messages
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentMessagesBinding.bind(view)
        floatingButtonAddMessage = binding.floatingButtonAddMessage

        val contactId=arguments.contactId
        val contactNumber=arguments.phone

        floatingButtonAddMessage.setOnClickListener {
            val action=MessagesFragmentDirections.actionMessagesFragmentToAddEditMessageFragment(
                contactId,
                contactNumber,
                0 //to show its a new message
            )
            findNavController().navigate(action)
        }
    }

    private fun checkSmsPermission(): Boolean {
        return isAllGranted(Permission.SEND_SMS)
    }

    private fun setSmsPermission() {
        askForPermissions(
            Permission.SEND_SMS,
        ) { result ->

            if (result.isAllGranted()) {
                floatingButtonAddMessage.visibility = View.VISIBLE
                //Todo:get messages
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