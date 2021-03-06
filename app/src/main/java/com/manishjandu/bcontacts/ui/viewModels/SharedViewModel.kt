package com.manishjandu.bcontacts.ui.viewModels


import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manishjandu.bcontacts.data.ContactsRepository
import com.manishjandu.bcontacts.data.local.entities.SavedContact
import com.manishjandu.bcontacts.data.models.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.set

private const val TAG="SharedViewModel"

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repo: ContactsRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    private val resolver: ContentResolver=context.contentResolver

    private val _contacts=MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> =_contacts

    private val _bContacts=MutableLiveData<List<SavedContact>>()
    val bContacts: LiveData<List<SavedContact>> =_bContacts


    fun getContactsList(searchString: String?=null) {
        val phones=HashMap<Long, MutableList<String>>()
        val contactList: ArrayList<Contact> =arrayListOf()


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

        val SELECTION: String=
            "${ContactsContract.Contacts.DISPLAY_NAME_PRIMARY} LIKE ?"
        // val selectionArgs=arrayOf("%$searchString%")
        val selectionArgs=arrayOf("%$searchString%")

        if (searchString.isNullOrEmpty()) {
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
        } else {
            getContactsCursor=resolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                ),
                SELECTION,
                selectionArgs,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
            )
        }

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
            }
        }
        _contacts.postValue(contactList)
        getContactsCursor?.close()
    }

    fun addContactLocally(contact: Contact)=viewModelScope.launch {
        repo.addContactInSavedContact(contact)
    }

    fun getContactLocally()=viewModelScope.launch {
        val result=repo.getContactsFromSavedContact()
        _bContacts.postValue(result)
    }


}


