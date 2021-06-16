package com.manishjandu.bcontacts.data.local

import androidx.room.*

@Dao
interface SavedContactDao {

    @Query("SELECT * FROM saved_contact_table")
    suspend fun getAllSavedContact(): List<SavedContact>

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: SavedContact)

    @Delete
    suspend fun deleteContact(contact: SavedContact)

}