package com.manishjandu.bcontacts.data.local

import androidx.room.*
import com.manishjandu.bcontacts.data.local.entities.Notes
import com.manishjandu.bcontacts.data.local.entities.SavedContact
import com.manishjandu.bcontacts.data.local.entities.SavedContactWithNotes

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

}