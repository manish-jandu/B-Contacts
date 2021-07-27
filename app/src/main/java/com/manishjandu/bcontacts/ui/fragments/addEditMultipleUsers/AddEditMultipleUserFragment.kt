package com.manishjandu.bcontacts.ui.fragments.addEditMultipleUsers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentAddEditMessageBinding

class AddEditMultipleUserFragment : Fragment(R.layout.fragment_add_edit_message) {
    private var _binding: FragmentAddEditMessageBinding?=null
    private val binding get()=_binding!!
    private val viewModel: AddEditMultipleUserViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding=FragmentAddEditMessageBinding.bind(view)

        setupMultipleUserFragment()
    }

    private fun setupMultipleUserFragment() {
        binding.apply {
            linearLayoutSelectedContact.visibility = View.VISIBLE
            imageButtonSelectContact.visibility = View.VISIBLE
         }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}