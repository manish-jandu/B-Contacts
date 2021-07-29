package com.manishjandu.bcontacts.data.local.convertor

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.manishjandu.bcontacts.data.models.Contact


class MultipleContactConvertor {

    private val listContactType = object : TypeToken<List<Contact>>(){}.type

    @TypeConverter
    fun fromListOfContacts(contacts: List<Contact>) = Gson().toJson(contacts)

    @TypeConverter
    fun toListOfContacts(value: String):List<Contact> = Gson().fromJson(value, listContactType)


}