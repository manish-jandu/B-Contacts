package com.manishjandu.bcontacts.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    val contactId: Long,
    val name: String?=null,
    val phone: String,
):Parcelable