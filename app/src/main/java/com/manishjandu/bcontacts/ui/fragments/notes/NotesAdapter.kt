package com.manishjandu.bcontacts.ui.fragments.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manishjandu.bcontacts.data.local.entities.Notes
import com.manishjandu.bcontacts.databinding.ItemNoteBinding

class NotesAdapter(private val onClick: OnClick) :
    ListAdapter<Notes, NotesAdapter.NotesViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding=ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val item=getItem(position)
        holder.bind(item)
    }

    inner class NotesViewHolder(binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        private val textViewNoteTitle=binding.textViewNoteTitle
        private val textViewNoteDescription=binding.textViewNoteDescription

        init {
            binding.root.setOnClickListener {
                val position=adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item=getItem(position)
                    onClick.onRootClick(item.contactId, item.notesId)
                }
            }
        }

        fun bind(item: Notes) {
            textViewNoteTitle.text=item.title
            textViewNoteDescription.text=item.note
        }
    }

    interface OnClick {
        fun onRootClick(contactId: Long, noteId: Int)
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Notes>() {
        override fun areItemsTheSame(
            oldItem: Notes,
            newItem: Notes
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Notes,
            newItem: Notes
        ): Boolean {
            return oldItem.contactId == newItem.contactId
        }
    }
}