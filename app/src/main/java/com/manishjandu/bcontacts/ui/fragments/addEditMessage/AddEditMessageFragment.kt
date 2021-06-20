package com.manishjandu.bcontacts.ui.fragments.addEditMessage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentAddEditMessageBinding

class AddEditMessageFragment : Fragment(R.layout.fragment_add_edit_message) {
    private lateinit var binding: FragmentAddEditMessageBinding
    private val arguments: AddEditMessageFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentAddEditMessageBinding.bind(view)

        val contactId=arguments.contactId
        val contactNumber=arguments.phone

        binding.buttonDatePicker.setOnClickListener {
            //Todo:pick date
        }

        binding.buttonTimePicker.setOnClickListener {
            //Todo:pick time
        }

    }

}