package com.android.multicontactpicker.sms.viewmodel

import android.app.Application
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.multicontactpicker.sms.model.Contact
import timber.log.Timber

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private var mSelectedContactListLiveData = MutableLiveData<MutableList<Contact>>()
    private var mSelectedContactList = mutableListOf<Contact>()

    private var mContactListLiveData = MutableLiveData<MutableList<Contact>>()
    private var contactList = mutableListOf<Contact>()

    private val applicationContext = application

    init {
        getContactManagedQuery()
        mContactListLiveData.value = contactList
    }

    fun getSelectedContactListMutableLiveData(): MutableLiveData<MutableList<Contact>> {
        return mSelectedContactListLiveData
    }

    fun getContactListMutableLiveData(): MutableLiveData<MutableList<Contact>> {
        return mContactListLiveData
    }

    /**
     * Method to fetch contacts from device
     */
    private fun getContactManagedQuery() {
        val cr = applicationContext.contentResolver
        val cursor = cr.query(
            ContactsContract.Data.CONTENT_URI, // value
            null,
            ContactsContract.Data.MIMETYPE + "=?", // condition
            arrayOf(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE),
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE NOCASE ASC"
        )
        val contactIdSet = HashSet<String>()

        while (cursor!!.moveToNext()) {
            val type: Int =
                cursor.let { it.getInt(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)) }
            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                val id =
                    cursor.let { it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)) }
                val name =
                    cursor.let { it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) }
                val number =
                    cursor.let { it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) }


                val con = Contact(id, name, number.trim(), false)

                Timber.i(" Contact id  :  ${con.id}")
                Timber.i(" Contact name :  ${con.contactName}")
                Timber.i(" Contact number :  ${con.contactNumber}")
                if (!contactIdSet.contains(id)) {
                    contactList.add(con)
                    contactIdSet.add(id)
                }
            }
        }

        cursor.close()
    }

    /**
     * add contact in SelectionList
     * @param contact
     * */
    fun onAdd(contact: Contact) {
        mSelectedContactList.add(contact)
        mSelectedContactListLiveData.value = mSelectedContactList
    }

    /**
     * remove contact from SelectionList
     * @param contact
     * */
    fun onRemove(contact: Contact) {
        mSelectedContactList.remove(contact)
        mSelectedContactListLiveData.value = mSelectedContactList
    }
}