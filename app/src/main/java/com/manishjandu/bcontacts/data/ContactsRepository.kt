package com.manishjandu.bcontacts.data

import com.manishjandu.bcontacts.data.local.SavedContact
import com.manishjandu.bcontacts.data.local.SavedContactDao
import com.manishjandu.bcontacts.data.models.Contact


class ContactsRepository(private val savedContactDao: SavedContactDao) {

    suspend fun addContactInSavedContact(contact: Contact) {
        val newContact=
            SavedContact(name=contact.name, contactId=contact.contactId, phone=contact.phone)
        savedContactDao.insertContact(newContact)
    }

    suspend fun getContactsFromSavedContact(): List<SavedContact> {
        return savedContactDao.getAllSavedContact()
    }

    suspend fun removeContactFromSavedContact(savedContact:SavedContact) {
        savedContactDao.deleteContact(savedContact)
    }

}