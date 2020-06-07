package com.android.multicontactpicker.sms.ui.activity

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.multicontactpicker.sms.R
import com.android.multicontactpicker.sms.model.Contact
import com.android.multicontactpicker.sms.ui.adapter.ContactListAdapter
import com.android.multicontactpicker.sms.utils.ShareUtils
import com.android.multicontactpicker.sms.viewmodel.ContactViewModel
import kotlinx.android.synthetic.main.activity_contact_sync.*
import timber.log.Timber


class ContactSyncActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val REQUEST_CODE_CONTACT = 101
    }

    lateinit var adapter: ContactListAdapter
    private lateinit var contactViewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_sync)
        initView()
        if (isSmsAndContactPermissionGranted()) {
            initializeContactViewModel()
        }
    }

    //Method used for initialize ContactViewModel
    private fun initializeContactViewModel() {
        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)
        contactViewModel.getContactListMutableLiveData().observe(this, Observer { contactList ->
            setAdapter(contactList)
        })
        contactViewModel.getSelectedContactListMutableLiveData()
            .observe(this, Observer { mSelectedContactList ->
                tvInvite.text =
                    String.format(getString(R.string.invite_with_number), mSelectedContactList.size)
            })
    }

    private fun initView() {
        val searchIcon = contactSearchView.findViewById<ImageView>(R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.WHITE)

        val cancelIcon = contactSearchView.findViewById<ImageView>(R.id.search_close_btn)
        cancelIcon.setColorFilter(Color.WHITE)

        val textView = contactSearchView.findViewById<TextView>(R.id.search_src_text)
        textView.setTextColor(Color.WHITE)
        // If you want to change the color of the cursor, change the 'colorAccent' in colors.xml

        tvInvite.text = String.format(getString(R.string.invite_with_number), 0)
        tvInvite.setOnClickListener(this)
    }

    // check if sms and contact permission granted
    private fun isSmsAndContactPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
            ) {
                Timber.v(getString(R.string.permission_granted))
                return true
            } else {
                Timber.v(getString(R.string.permission_revoked))
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.READ_CONTACTS,
                        android.Manifest.permission.SEND_SMS
                    ),
                    REQUEST_CODE_CONTACT
                )
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Timber.v(getString(R.string.permission_granted))
            return true
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tvInvite -> {
                if (contactViewModel.getSelectedContactListMutableLiveData().value!!.size > 0)
                    ShareUtils.sendSmsIntent(
                        this,
                        getString(R.string.share_msg),
                        contactViewModel.getSelectedContactListMutableLiveData().value!!.toString()
                    )
            }
        }
    }

    /**
     * Method used for set adapter in RecyclerView
     * @param contactList
     * */
    private fun setAdapter(contactList: MutableList<Contact>) {
        try {
            val llm = LinearLayoutManager(this)
            llm.orientation = LinearLayoutManager.VERTICAL
            contactRecyclerView!!.layoutManager = llm
            adapter = ContactListAdapter(contactList, contactViewModel)
            contactRecyclerView!!.adapter = adapter

            contactSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return false
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CONTACT) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                initializeContactViewModel()
            } else {
                finish()
            }
        }
    }
}
