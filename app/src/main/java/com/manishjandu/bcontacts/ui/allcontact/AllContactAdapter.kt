package com.manishjandu.bcontacts.ui.allcontact

import android.view.LayoutInflater
import android.view.ViewGroup
 import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.ItemContactBinding

class AllContactAdapter :
    ListAdapter<Contact, AllContactAdapter.AllContactViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllContactViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class AllContactViewHolder(binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val contactName = binding.textViewContactName
        fun bind(item: Contact) {
         }

    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }


}