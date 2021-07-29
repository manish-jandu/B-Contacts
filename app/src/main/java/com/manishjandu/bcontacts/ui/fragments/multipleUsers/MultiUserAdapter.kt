package com.manishjandu.bcontacts.ui.fragments.multipleUsers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manishjandu.bcontacts.data.local.entities.MultipleUserMessage
import com.manishjandu.bcontacts.databinding.ItemMessageBinding

class MultiUserAdapter(private val onMessageClick: OnMessageClick) :
    ListAdapter<MultipleUserMessage, MultiUserAdapter.MultiUserViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultiUserViewHolder {
        val binding=ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MultiUserViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MultiUserViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        item?.let {
            holder.bind(it)
        }
    }

    inner class MultiUserViewHolder(binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val message = binding.textViewMessage
        val time = binding.textViewMessageTime

        init {
            binding.root.setOnClickListener {
                val position=adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item=getItem(position)
                    onMessageClick.onRootClick(item.multipleUserMessageId)
                }
            }
        }

        fun bind(item: MultipleUserMessage) {
            val messageTime =  "${item.day}-${item.month}-${item.year} at ${item.hour}:${item.minutes}"
            time.text = messageTime
            message.text = item.message
        }
    }

    interface OnMessageClick {
        fun onRootClick(messageId:Int)
    }

    class DiffUtilCallback() : DiffUtil.ItemCallback<MultipleUserMessage>() {
        override fun areItemsTheSame(
            oldItem: MultipleUserMessage,
            newItem: MultipleUserMessage
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: MultipleUserMessage,
            newItem: MultipleUserMessage
        ): Boolean {
            return oldItem.multipleUserMessageId == newItem.multipleUserMessageId
        }
    }
}