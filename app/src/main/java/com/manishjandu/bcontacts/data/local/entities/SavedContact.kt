package com.manishjandu.bcontacts.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="saved_contact_table")
data class SavedContact(
    @ColumnInfo(index=true)
    @PrimaryKey(autoGenerate=false)
    val contactId: Long,

    val name: String?=null,
    val phone: String,
)
