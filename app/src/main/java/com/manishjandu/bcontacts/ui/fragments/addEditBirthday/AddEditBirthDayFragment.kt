package com.manishjandu.bcontacts.ui.fragments.addEditBirthday

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentAddEditBirthdayBinding

class AddEditBirthDayFragment:Fragment(R.layout.fragment_add_edit_birthday) {
    private val viewModel:AddEditBirthDayViewModel by viewModels()
    private var _binding : FragmentAddEditBirthdayBinding?=null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}