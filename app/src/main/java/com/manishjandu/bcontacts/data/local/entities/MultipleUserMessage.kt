package com.manishjandu.bcontacts.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.manishjandu.bcontacts.data.models.Contact

@Entity(tableName="multiple_user_message_table")
data class MultipleUserMessage(
    @PrimaryKey(autoGenerate=true)
    val MultipleUserMessageId: Int,

    val message: String,

    val contacts: List<Contact>,

    val minutes: Int,
    val hour: Int,
    val day: Int,
    val month: Int,
    val year: Int,
    val timeInMillis: Long,
)