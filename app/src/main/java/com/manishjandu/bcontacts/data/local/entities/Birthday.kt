package com.manishjandu.bcontacts.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "birthday_table")
data class Birthday(

    @PrimaryKey(autoGenerate = false)
    val requestCode:Int,

    val contactNumber: String,

    val birthdayMessage:String,
    val isAutoSet:Boolean,
    val timeInMillis:Long,

    val minutes:Int,
    val hour:Int,
    val day:Int,
    val month:Int,
    val year:Int,


)
