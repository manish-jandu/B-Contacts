package com.manishjandu.bcontacts.ui.fragments.bcontact


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manishjandu.bcontacts.data.local.entities.SavedContact
import com.manishjandu.bcontacts.databinding.ItemContactBinding

private const val TAG="AllContactAdapter"

class BContactAdapter(private val onClick: OnClick) :
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
        private val buttonCall=binding.buttonCall
        private val buttonMessage=binding.buttonMessage
        private val buttonMoreOption=binding.buttonMoreOption

        fun bind(item: SavedContact) {
            contactName.text=item.name
            contactNumber.text=item.phone

            buttonCall.setOnClickListener {
                onClick.onCallClicked(item.phone)
            }
            buttonMessage.setOnClickListener {
                onClick.onMessageClicked(item.phone)
            }
            buttonMoreOption.setOnClickListener {
                onClick.onMoreOptionClicked(item, buttonMoreOption)
            }
        }
    }

    interface OnClick {
        fun onCallClicked(contactNumber: String)
        fun onMessageClicked(contactNumber: String)
        fun onMoreOptionClicked(savedContact: SavedContact, buttonMoreOption: ImageButton)
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