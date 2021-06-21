package com.manishjandu.bcontacts.ui.fragments.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manishjandu.bcontacts.data.local.entities.Message
 import com.manishjandu.bcontacts.databinding.ItemMessageBinding

class MessagesAdapter :
    ListAdapter<Message, MessagesAdapter.MessagesViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        val binding=ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val item=getItem(position)
        holder.bind(item)
    }

    inner class MessagesViewHolder(binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val textViewMessage=binding.textViewMessage
        private val textViewMessageTime=binding.textViewMessageTime

        fun bind(item: Message) {
            textViewMessage.text=item.message

            val msgTime="${item.day}-${item.month}-${item.year} at ${item.hour}:${item.minutes}"
            textViewMessageTime.text=msgTime
        }
    }


     class DiffUtilCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean {
            return oldItem.contactId == newItem.contactId
        }
    }
}