package com.manishjandu.bcontacts.ui.fragments.addEditNote

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentAddEditNoteBinding
import kotlinx.coroutines.flow.collect


class AddEditNoteFragment : Fragment(R.layout.fragment_add_edit_note) {
    private val viewModel: AddEditNoteViewModel by viewModels()
    private lateinit var binding: FragmentAddEditNoteBinding
    private val arguments: AddEditNoteFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contactId = arguments.contactId
        val notesId:Int = arguments.notesId

        viewModel.getNote(notesId)

        binding=FragmentAddEditNoteBinding.bind(view)
        val noteTitle = binding.editTextNoteTitle
        val noteDescription = binding.editTextNoteDescription

        viewModel.note.observe(viewLifecycleOwner){
            noteTitle.setText(it.title)
            noteDescription.setText(it.note)
        }

        binding.buttonSaveNote.setOnClickListener {
            val title = noteTitle.text.toString()
            val description = noteDescription.text.toString()
            viewModel.addNote(contactId,title,description,notesId)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditNoteEvent.collect { event ->
                when (event) {
                    is AddEditNoteViewModel.AddEditNoteEvent.NoteAddingSuccessful -> {
                        findNavController().popBackStack()
                    }
                    is AddEditNoteViewModel.AddEditNoteEvent.NoteAddingNotSuccessful -> {
                        Snackbar.make(
                            requireView(),
                            "Don't Leave anything blanck",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }

    }

}