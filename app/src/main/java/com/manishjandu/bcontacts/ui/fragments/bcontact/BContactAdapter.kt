package com.manishjandu.bcontacts.ui.fragments.bcontact


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manishjandu.bcontacts.data.local.entities.SavedContact
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.ItemBContactBinding

private const val TAG="AllContactAdapter"

class BContactAdapter(private val onClick: OnClick) :
    ListAdapter<SavedContact, BContactAdapter.BContactViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BContactViewHolder {
        val binding=ItemBContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BContactViewHolder, position: Int) {
        val item=getItem(position)
        holder.bind(item)
    }

    inner class BContactViewHolder(binding: ItemBContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val contactName=binding.textViewBContactName
        private val contactNumber=binding.textViewBContactNumber
        private val buttonCall=binding.buttonCall
        private val buttonMessage=binding.buttonMessage

        private val root=binding.root

        fun bind(item: SavedContact) {
            val contact=Contact(item.contactId, item.name, item.phone)

            contactName.text=item.name
            contactNumber.text=item.phone

            buttonCall.setOnClickListener {
                onClick.onCallClicked(item.phone)
            }
            buttonMessage.setOnClickListener {
                onClick.onMessageClicked(item.phone)
            }

            root.setOnClickListener {
                onClick.bottomSheet(contact)
            }
        }
    }

    interface OnClick {
        fun onCallClicked(contactNumber: String)
        fun onMessageClicked(contactNumber: String)
        fun bottomSheet(contact: Contact)
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