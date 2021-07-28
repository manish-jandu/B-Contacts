package com.manishjandu.bcontacts.ui.fragments.addEditMultipleUsers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.FragmentAddEditMessageBinding
import com.manishjandu.bcontacts.utils.Constants.CONTACT_SELECTED
import com.manishjandu.bcontacts.utils.Constants.FRAGMENT_SELECT_MULTIPLE_CONTACT_REQUEST_KEY

class AddEditMultipleUserFragment : Fragment(R.layout.fragment_add_edit_message) {
    private var _binding: FragmentAddEditMessageBinding?=null
    private val binding get()=_binding!!
    private val viewModel: AddEditMultipleUserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding=FragmentAddEditMessageBinding.bind(view)

        setupMultipleUserFragment()
        setupResultListener()
    }

    private fun setupResultListener() {
        setFragmentResultListener( FRAGMENT_SELECT_MULTIPLE_CONTACT_REQUEST_KEY) { _, bundle ->
            val result=bundle.getParcelableArrayList<Contact>(CONTACT_SELECTED)
        }
    }

    private fun setupMultipleUserFragment() {
        binding.apply {
            linearLayoutSelectedContact.visibility = View.VISIBLE
            imageButtonSelectContact.visibility = View.VISIBLE

            imageButtonSelectContact.setOnClickListener {
                val action = AddEditMultipleUserFragmentDirections.actionAddEditMultipleUserFragmentToSelectMultipleContactFragment()
                findNavController().navigate(action)
            }
         }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}