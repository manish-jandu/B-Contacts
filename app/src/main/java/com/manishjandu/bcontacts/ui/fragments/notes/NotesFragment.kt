package com.manishjandu.bcontacts.ui.fragments.notes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentNotesBinding

class NotesFragment : Fragment(R.layout.fragment_notes) {
    private val viewModel: NotesViewModel by viewModels()
    private lateinit var binding: FragmentNotesBinding
    private val notesAdapter=NotesAdapter()
    private val arguments: NotesFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contactId=arguments.contactId
        viewModel.getNotes(contactId)

        binding=FragmentNotesBinding.bind(view)
        val recyclerViewNotes=binding.recyclerViewNotes

        recyclerViewNotes.adapter=notesAdapter
        recyclerViewNotes.layoutManager=LinearLayoutManager(requireContext())

        binding.floatingButtonAddNote.setOnClickListener {
            val action=NotesFragmentDirections.actionNotesFragmentToAddEditNoteFragment(contactId)
            findNavController().navigate(action)
        }

        viewModel.notes.observe(viewLifecycleOwner) {
            notesAdapter.submitList(it.notes)
        }

    }
}