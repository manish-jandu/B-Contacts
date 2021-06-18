package com.manishjandu.bcontacts.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.manishjandu.bcontacts.data.local.entities.Notes
 import com.manishjandu.bcontacts.data.local.entities.SavedContact

@Database(entities=[SavedContact::class, Notes::class], version=1)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao


    companion object {

        @Volatile
        private var savedContactInstance: LocalDatabase?=null

        fun getSavedContactDatabase(context: Context): LocalDatabase {
            synchronized(this) {
                if (savedContactInstance == null) {
                    savedContactInstance=createSavedContactInstance(context)
                }
                return savedContactInstance!!
            }
        }

        private fun createSavedContactInstance(context: Context): LocalDatabase {
            return Room.databaseBuilder(
                context,
                LocalDatabase::class.java,
                "saved_contact_database"
            ).build()
        }

    }

}