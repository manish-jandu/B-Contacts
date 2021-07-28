package com.manishjandu.bcontacts.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SelectedContacts(
    var contacts:List<Contact>
) : Parcelable