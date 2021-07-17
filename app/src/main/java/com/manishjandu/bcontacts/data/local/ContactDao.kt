package com.manishjandu.bcontacts.data.local

import androidx.room.*
import com.manishjandu.bcontacts.data.local.entities.*


@Dao
interface ContactDao {
    @Query("SELECT * FROM saved_contact_table")
    suspend fun getAllSavedContact(): List<SavedContact>

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun insertContact(savedContact: SavedContact)

    @Delete
    suspend fun deleteContact(savedContact: SavedContact)

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Notes)

    @Delete
    suspend fun deleteNote(note: Notes)

    @Query("SELECT * FROM notes_table WHERE notesId =:notesId")
    suspend fun getNote(notesId: Int): Notes

    @Transaction
    @Query("SELECT * FROM saved_contact_table where contactId =:contactId ")
    suspend fun getNotesWithSavedContact(contactId: Long): SavedContactWithNotes

    @Transaction
    @Query("DELETE FROM notes_table WHERE contactId =:contactId  ")
    suspend fun deleteNotesWithSavedContact(contactId: Long)

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long

    @Delete
    suspend fun deleteMessage(message: Message)

    @Query("SELECT * FROM message_table WHERE messageId =:messageId")
    suspend fun getMessage(messageId: Int): Message

    @Transaction
    @Query("SELECT * FROM saved_contact_table  where contactId =:contactId ")
    suspend fun getMessagesWithSavedContact(contactId: Long): SavedContactWithMessages

    @Transaction
    @Query("DELETE FROM message_table WHERE contactId =:contactId")
    suspend fun deleteMessagesWithSavedContact(contactId: Long)

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun insertBirthDay(birthday: Birthday)

    @Query("SELECT * FROM birthday_table WHERE requestCode =:requestCode")
    suspend fun getBirthDay(requestCode: Int): Birthday

    @Delete
    suspend fun deleteBirthDay(birthday: Birthday)

}