package com.manishjandu.bcontacts.ui.fragments.addEditMultipleUsers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.ItemContactBinding

class AddEditMultipleUserAdapter(val onSelectedContactClick:OnSelectedContactClick) :
    ListAdapter<Contact, AddEditMultipleUserAdapter.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=getItem(position)
        item?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        val name=binding.textViewContactName
        val phone=binding.textViewContactNumber
        val buttonClear=binding.buttonClear

        init {
            binding.apply {
                buttonCall.visibility=View.GONE
                buttonMessage.visibility=View.GONE
                buttonClear.visibility=View.VISIBLE

                buttonClear.setOnClickListener {
                    val position = adapterPosition
                    if (position!=RecyclerView.NO_POSITION){
                        onSelectedContactClick.onRemoveContact(position)
                    }
                }
            }
        }

        fun bind(item: Contact) {
            name.text = item.name
            phone.text = item.phone
        }

    }

    interface OnSelectedContactClick{
        fun onRemoveContact(position: Int)
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.contactId == newItem.contactId
        }
    }
}