package com.manishjandu.bcontacts.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "message_table",
    foreignKeys = [ForeignKey(
        entity = SavedContact::class,
        parentColumns = arrayOf("contactId"),
        childColumns = arrayOf("contactId"),
        onDelete = CASCADE
    )]
)
data class Message(
    @ColumnInfo(name="minutes")
    val minutes: Int,
    @ColumnInfo(name="hour")
    val hour: Int,
    @ColumnInfo(name="day")
    val day: Int,
    @ColumnInfo(name="month")
    val month: Int,
    @ColumnInfo(name="year")
    val year: Int,
    @ColumnInfo(name="time_in_millis")
    val timeInMillis: Long,
    @ColumnInfo(name="message")
    val message: String,
    @ColumnInfo(name="phone")
    val phone: String,

    @ColumnInfo(index=true)
    val contactId: Long,

    @PrimaryKey(autoGenerate = true)
    var messageId:Int = 0,
)
