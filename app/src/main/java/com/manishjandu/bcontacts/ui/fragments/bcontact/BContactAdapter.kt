package com.manishjandu.bcontacts.ui.fragments.bcontact


import android.view.LayoutInflater
import android.view.ViewGroup
 import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manishjandu.bcontacts.data.local.SavedContact
 import com.manishjandu.bcontacts.databinding.ItemContactBinding

private const val TAG="AllContactAdapter"

class BContactAdapter  :
    ListAdapter<SavedContact, BContactAdapter.BContactViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BContactViewHolder {
        val binding=ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BContactViewHolder, position: Int) {
        val item=getItem(position)
        holder.bind(item)
    }

    inner class BContactViewHolder(binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val contactName=binding.textViewContactName
        private val contactNumber=binding.textViewContactNumber
        fun bind(item: SavedContact) {
            contactName.text=item.name
            contactNumber.text=item.phone
        }
    }


    class DiffUtilCallback : DiffUtil.ItemCallback<SavedContact>() {
        override fun areItemsTheSame(oldItem: SavedContact, newItem: SavedContact): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SavedContact, newItem: SavedContact): Boolean {
            return oldItem == newItem
        }
    }


}