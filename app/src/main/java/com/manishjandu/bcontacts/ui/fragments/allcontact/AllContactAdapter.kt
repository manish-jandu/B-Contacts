package com.manishjandu.bcontacts.ui.fragments.allcontact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.ItemContactBinding

private const val TAG="AllContactAdapter"

class AllContactAdapter(private val onClick: OnClick) :
    ListAdapter<Contact, AllContactAdapter.AllContactViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllContactViewHolder {
        val binding=ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllContactViewHolder, position: Int) {
        val item=getItem(position)
        holder.bind(item)
    }

    inner class AllContactViewHolder(binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val contactName=binding.textViewContactName
        private val contactNumber=binding.textViewContactNumber
        private val buttonCall=binding.buttonCall
        private val buttonMessage=binding.buttonMessage
        private val root=binding.root
        private val expandableLayoutAllContact=binding.expandableLayoutAllContact
        private val buttonAddToBContact=binding.buttonAddToBContact

        fun bind(item: Contact) {
            contactName.text=item.name
            contactNumber.text=item.phone
            buttonCall.setOnClickListener {
                onClick.onCallClicked(item.phone)
            }
            buttonMessage.setOnClickListener {
                onClick.onMessageClicked(item.phone)
            }
            buttonAddToBContact.setOnClickListener {
                onClick.onAddToBContactClicked(item)
            }
            root.setOnClickListener {
                if (expandableLayoutAllContact.visibility == View.GONE) {
                    expandableLayoutAllContact.visibility=View.VISIBLE
                } else {
                    expandableLayoutAllContact.visibility=View.GONE
                }
            }
        }
    }

    interface OnClick {
        fun onCallClicked(contactNumber: String)
        fun onMessageClicked(contactNumber: String)
        fun onAddToBContactClicked(contact: Contact)
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