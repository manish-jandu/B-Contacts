package com.manishjandu.bcontacts.ui.fragments.selectMultipleContact

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.databinding.ItemSelectedContactBinding

class MultipleUserAdapter :
    ListAdapter<Contact, MultipleUserAdapter.MultipleUserViewHolder>(DiffUtilCallback()) {

    private var tracker: SelectionTracker<Long>?=null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleUserViewHolder {
        val binding=
            ItemSelectedContactBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MultipleUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MultipleUserViewHolder, position: Int) {
        val parent=holder.name.parent as ConstraintLayout
        val item=getItem(position)
        item?.let {
            holder.bind(it)
        }

        if (tracker!!.isSelected(position.toLong())) {
            parent.background=ColorDrawable(
                Color.parseColor("#80deea")
            )
        } else {
            // Reset color to white if not selected
            parent.background=ColorDrawable(Color.WHITE)
        }

    }

    override fun getItemId(position: Int): Long=position.toLong()

    inner class MultipleUserViewHolder(binding: ItemSelectedContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val name=binding.textViewBContactName

        val phone=binding.textViewBContactNumber

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> {
            return object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int=adapterPosition
                override fun getSelectionKey(): Long=itemId
            }
        }

        fun bind(item: Contact) {
            name.text=item.name
            phone.text=item.phone
        }
    }

    fun setTracker(tracker: SelectionTracker<Long>?) {
        this.tracker=tracker
    }

    fun clearSelection(){
        tracker?.clearSelection()
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {

            return oldItem.contactId == newItem.contactId
        }
    }

}