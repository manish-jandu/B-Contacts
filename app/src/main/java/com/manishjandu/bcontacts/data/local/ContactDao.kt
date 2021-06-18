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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Notes)

    @Delete
    suspend fun deleteNote(note: Notes)

    @Transaction
    @Query("SELECT * FROM saved_contact_table where contactId =:contactId ")
    suspend fun getNotesWithSavedContact(contactId:Long):List<SavedContactWithNotes>
}