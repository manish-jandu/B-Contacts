package com.manishjandu.bcontacts.ui.fragments.addEditNote

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentAddEditNoteBinding


class AddEditNoteFragment : Fragment(R.layout.fragment_add_edit_note) {
    private val viewModel: AddEditNoteViewModel by viewModels()
    private lateinit var binding: FragmentAddEditNoteBinding
    private val arguments:AddEditNoteFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding=FragmentAddEditNoteBinding.bind(view)
        val noteTitle = binding.editTextNoteTitle
        val noteDescription = binding.editTextNoteDescription

        val contactId = arguments.contactId

        binding.buttonSaveNote.setOnClickListener {
            val title = noteTitle.text.toString()
            val description = noteDescription.text.toString()
            viewModel.addNote(contactId,title,description)
        }

    }

}