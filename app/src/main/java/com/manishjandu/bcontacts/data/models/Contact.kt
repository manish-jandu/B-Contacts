package com.manishjandu.bcontacts.data.models

data class Contact(
    val contactId: Long,
    val name: String?=null,
    val phone: String,
)