package com.manishjandu.bcontacts.ui.allcontact


import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.manishjandu.bcontacts.data.models.Contact

class AllContactViewModel : ViewModel() {
    private val _contacts=MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts

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
            val photo: String?=getContactsCursor.getString(2)
            val contactPhones: List<String>?=phones[contactId]
            if (contactPhones != null) {
                for (phone in contactPhones) {
                    contactList.add(Contact(contactId, name, phone, photo))
                }
                _contacts.postValue(contactList)
            }
        }
        getContactsCursor?.close()
    }

}


