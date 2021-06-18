package com.manishjandu.bcontacts.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class SavedContactWithNotes(
    @Embedded val savedContact: SavedContact,
    @Relation(
        parentColumn="contactId",
        entityColumn="contactId",
    )
    val notes: List<Notes>
)
