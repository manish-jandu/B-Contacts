package com.manishjandu.bcontacts.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName="notes_table",
    foreignKeys=[ForeignKey(
        entity=SavedContact::class,
        parentColumns=arrayOf("contactId"),
        childColumns=arrayOf("contactId"),
        onDelete=CASCADE
    )]
)
data class Notes(
    val contactId: Long,
    val title: String,
    val note: String,

    @PrimaryKey(autoGenerate=true)
    val notesId: Int=0,
)

