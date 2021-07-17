package com.manishjandu.bcontacts.data

import com.manishjandu.bcontacts.data.local.ContactDao
import com.manishjandu.bcontacts.data.local.entities.*
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

    suspend fun getNote(notesId: Int): Notes {
        return contactDao.getNote(notesId)
    }

    suspend fun removeNote(note: Notes) {
        contactDao.deleteNote(note)
    }

    suspend fun getNotes(contactId: Long): SavedContactWithNotes {
        return contactDao.getNotesWithSavedContact(contactId)
    }

    suspend fun removeAllNoteWithContact(contactId: Long) {
        contactDao.deleteNotesWithSavedContact(contactId)
    }

    suspend fun addMessage(message: Message): Int {
        return contactDao.insertMessage(message).toInt()
    }

    suspend fun getMessage(messageId: Int): Message {
        return contactDao.getMessage(messageId)
    }

    suspend fun removeMessage(message: Message) {
        contactDao.deleteMessage(message)
    }

    suspend fun getMessages(contactId: Long): SavedContactWithMessages {
        return contactDao.getMessagesWithSavedContact(contactId)
    }

    suspend fun removeAllMessagesWithContact(contactId: Long) {
        contactDao.deleteMessagesWithSavedContact(contactId)
    }

    suspend fun addBirthday(birthday: Birthday){
        contactDao.insertBirthDay(birthday)
    }

    suspend fun removeBirthday(birthday: Birthday){
        contactDao.deleteBirthDay(birthday)
    }

    suspend fun getBirthday(requestCode:Int): Birthday {
        return contactDao.getBirthDay(requestCode)
    }
}