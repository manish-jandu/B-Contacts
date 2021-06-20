package com.manishjandu.bcontacts.ui.fragments.messages

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentMessagesBinding

class MessagesFragment : Fragment(R.layout.fragment_messages) {
    private lateinit var binding: FragmentMessagesBinding
    private val arguments: MessagesFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentMessagesBinding.bind(view)

        val contactId=arguments.contactId
        val contactNumber=arguments.phone

        binding.floatingButtonAddMessage.setOnClickListener {
            val action=MessagesFragmentDirections.actionMessagesFragmentToAddEditMessageFragment(
                contactId,
                contactNumber
            )
            findNavController().navigate(action)
        }
    }
}