package com.manishjandu.bcontacts.ui.viewModels


import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.LocalDatabase
import com.manishjandu.bcontacts.data.local.SavedContact
import com.manishjandu.bcontacts.data.models.Contact
import kotlinx.coroutines.launch

class SharedViewModel(app: Application) : AndroidViewModel(app) {
    private val _contacts=MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts

    private val _bContacts=MutableLiveData<List<SavedContact>>()
    val bContacts: LiveData<List<SavedContact>> = _bContacts

    private val savedContactDao=
        LocalDatabase.getSavedContactDatabase(app.applicationContext).savedContactDao()
    private val repo=ContactsRepository(savedContactDao)

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
        if (!result.isNullOrEmpty()) {
            _bContacts.postValue(result)
        }
    }

    fun removeContactLocally(savedContact: SavedContact) = viewModelScope.launch {
        repo.removeContactFromSavedContact(savedContact)
    }

}


