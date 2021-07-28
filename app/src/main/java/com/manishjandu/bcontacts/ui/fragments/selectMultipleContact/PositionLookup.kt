package com.manishjandu.bcontacts.ui.fragments.selectMultipleContact

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class PositionLookup(private val rv:RecyclerView):ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = rv.findChildViewUnder(event.x,event.y)
        if (view!=null){
            return (rv.getChildViewHolder(view) as MultipleUserAdapter.MultipleUserViewHolder).getItemDetails()
        }
        return null
    }
}