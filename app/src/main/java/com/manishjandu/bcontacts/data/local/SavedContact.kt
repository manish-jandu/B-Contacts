package com.manishjandu.bcontacts.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_contact_table")
data class SavedContact(
    val contactId: Long,
    val name: String?=null,
    val phone: String,

    @PrimaryKey(autoGenerate=true)
    val id: Int=0
)
