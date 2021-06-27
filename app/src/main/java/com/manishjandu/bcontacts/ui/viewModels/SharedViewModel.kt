package com.manishjandu.bcontacts.ui.viewModels


import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.LocalDatabase
import com.manishjandu.bcontacts.data.local.entities.SavedContact
import com.manishjandu.bcontacts.data.models.Contact
import com.manishjandu.bcontacts.utils.FutureMessage
import kotlinx.coroutines.launch

private const val TAG="SharedViewModel"

class SharedViewModel(val app: Application) : AndroidViewModel(app) {
    private val _contacts=MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts

    private val _bContacts=MutableLiveData<List<SavedContact>>()
    val bContacts: LiveData<List<SavedContact>> = _bContacts

    private val alarmManager=
        app.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val intent=Intent(app.applicationContext, FutureMessage::class.java)

    private val contactDao=
        LocalDatabase.getSavedContactDatabase(app.applicationContext).contactDao()
    private val repo=ContactsRepository(contactDao)

    fun getContactsList(context: Context) {
        val contactList=arrayListOf<Contact>()
        val resolver: ContentResolver=context.contentResolver
        val phones=HashMap<Long, MutableList<String>>()

        var getContactsCursor: Cursor?=resolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            null
        )

        if (getContactsCursor != null) {
            while (getContactsCursor.moveToNext()) {
                val contactId: Long=getContactsCursor.getLong(0)
                var phone: String=getContactsCursor.getString(1)
                phone=phone.replace("\\s".toRegex(), "")
                phone=phone.replace("-", "")
                phone=phone.replace("+91", "")

                var list: MutableList<String>

                if (phones.containsKey(contactId)) {
                    list=phones[contactId]!!
                    if (!list.contains(phone)) {
                        list.add(phone)
                    }
                } else {
                    list=ArrayList()
                    list.add(phone)
                }
                phones[contactId]=list
            }
            getContactsCursor.close()
        }

        getContactsCursor=resolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
            ),
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        while (getContactsCursor != null &&
            getContactsCursor.moveToNext()
        ) {
            val contactId: Long=getContactsCursor.getLong(0)
            val name: String?=getContactsCursor.getString(1)
            val contactPhones: List<String>?=phones[contactId]
            if (contactPhones != null) {
                for (phone in contactPhones) {
                    contactList.add(Contact(contactId, name, phone))
                }
                _contacts.postValue(contactList)
            }
        }
        getContactsCursor?.close()
    }

    fun addContactLocally(contact: Contact)=viewModelScope.launch {
        repo.addContactInSavedContact(contact)
    }

    fun getContactLocally()=viewModelScope.launch {
        val result=repo.getContactsFromSavedContact()
        _bContacts.postValue(result)
    }

    fun removeContactLocally(savedContact: SavedContact)=viewModelScope.launch {
        repo.removeAllNoteWithContact(savedContact.contactId)
        repo.removeContactFromSavedContact(savedContact)
        removeAlarms(savedContact)
        getContactLocally()
    }

    private suspend fun removeAlarms(savedContact: SavedContact){
        val savedMessages = repo.getMessages(savedContact.contactId)
        for(message in savedMessages.messages){
            cancelAlarm(message.messageId)
        }
    }

    private fun cancelAlarm(requestCode: Int) {
        val pendingIntent=PendingIntent.getBroadcast(app.applicationContext, requestCode, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

}


