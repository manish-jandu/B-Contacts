package com.manishjandu.bcontacts.di

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.ContactDao
import com.manishjandu.bcontacts.data.local.LocalDatabase
import com.manishjandu.bcontacts.utils.FutureMessage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDao(
        @ApplicationContext context: Context
    ): ContactDao {
        return LocalDatabase.getSavedContactDatabase(context).contactDao()
    }

    @Provides
    @Singleton
    fun providesContactsRepository(
        contactDao: ContactDao
    ): ContactsRepository {
        return ContactsRepository(contactDao)
    }

    @Provides
    @Singleton
    fun providesAlarmManager(
        @ApplicationContext context: Context
    ):AlarmManager{
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    @Singleton
    @Named("futureMessageIntent")
    fun providesFutureMessageIntent(
        @ApplicationContext context: Context
    ): Intent {
        return Intent(context, FutureMessage::class.java)
    }
}