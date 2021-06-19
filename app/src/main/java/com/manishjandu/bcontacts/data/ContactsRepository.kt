package com.manishjandu.bcontacts.data

import com.manishjandu.bcontacts.data.local.ContactDao
import com.manishjandu.bcontacts.data.local.entities.Notes
import com.manishjandu.bcontacts.data.local.entities.SavedContact
import com.manishjandu.bcontacts.data.local.entities.SavedContactWithNotes
import com.manishjandu.bcontacts.data.models.Contact


class ContactsRepository(private val contactDao: ContactDao) {

    suspend fun addContactInSavedContact(contact: Contact) {
        val newContact=
            SavedContact(name=contact.name, contactId=contact.contactId, phone=contact.phone)
        contactDao.insertContact(newContact)
    }

    suspend fun getContactsFromSavedContact(): List<SavedContact> {
        return contactDao.getAllSavedContact()
    }

    suspend fun removeContactFromSavedContact(savedContact: SavedContact) {
        contactDao.deleteContact(savedContact)
    }

    suspend fun addNote(note: Notes) {
        contactDao.insertNote(note)
    }

    suspend fun removeNote(note: Notes) {
        contactDao.deleteNote(note)
    }

    suspend fun getNotes(contactId: Long): SavedContactWithNotes {
        return contactDao.getNotesWithSavedContact(contactId)
    }

    suspend fun removeAllNoteWithContact(contactId: Long){
        contactDao.deleteNotesWithSavedContact(contactId)
    }
}