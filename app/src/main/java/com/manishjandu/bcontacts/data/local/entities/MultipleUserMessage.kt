package com.manishjandu.bcontacts.data.local.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.manishjandu.bcontacts.data.local.convertor.MultipleContactConvertor
import com.manishjandu.bcontacts.data.models.Contact
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName="multiple_user_message_table")
@TypeConverters(MultipleContactConvertor::class)
data class MultipleUserMessage(
    @PrimaryKey(autoGenerate=true)
    var multipleUserMessageId: Int=0,

    val message: String,

    val contacts: List<Contact>,

    val minutes: Int,
    val hour: Int,
    val day: Int,
    val month: Int,
    val year: Int,
    val timeInMillis: Long,
):Parcelable