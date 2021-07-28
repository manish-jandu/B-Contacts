package com.manishjandu.bcontacts.ui.fragments.multipleUsers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentMessagesBinding

class MultipleUsersFragment : Fragment(R.layout.fragment_messages) {
    private var _binding: FragmentMessagesBinding?=null
    private val binding get()=_binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding=FragmentMessagesBinding.bind(view)

        binding.floatingButtonAddMessage.setOnClickListener {
            val action=
                MultipleUsersFragmentDirections.actionMultipleUsersFragmentToAddEditMultipleUserFragment()
            findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}