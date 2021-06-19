package com.manishjandu.bcontacts.ui.fragments.notes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.manishjandu.bcontacts.R
import com.manishjandu.bcontacts.databinding.FragmentNotesBinding

class NotesFragment : Fragment(R.layout.fragment_notes) {
    private val viewModel: NotesViewModel by viewModels()
    private lateinit var binding: FragmentNotesBinding
    private val notesAdapter=NotesAdapter(OnClick())
    private val arguments: NotesFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contactId=arguments.contactId
        viewModel.getNotes(contactId)

        binding=FragmentNotesBinding.bind(view)
        val recyclerViewNotes=binding.recyclerViewNotes

        recyclerViewNotes.adapter=notesAdapter
        recyclerViewNotes.layoutManager=LinearLayoutManager(requireContext())
        binding.apply {
            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val note=notesAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.removeNote(note)
                }
            }).attachToRecyclerView(recyclerViewNotes)
        }


        binding.floatingButtonAddNote.setOnClickListener {
            navigateToAddEditNoteFragment(contactId, 0)
        }

        viewModel.notes.observe(viewLifecycleOwner) {
            notesAdapter.submitList(it.notes)
        }

    }

    fun navigateToAddEditNoteFragment(contactId: Long, notesId: Int) {
        val action=
            NotesFragmentDirections.actionNotesFragmentToAddEditNoteFragment(contactId, notesId)
        findNavController().navigate(action)
    }

    inner class OnClick : NotesAdapter.OnClick {
        override fun onRootClick(contactId: Long, noteId: Int) {
            navigateToAddEditNoteFragment(contactId, noteId)
        }

    }
}