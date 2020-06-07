package com.android.multicontactpicker.sms.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    val id: String,
    var contactName: String,
    var contactNumber: String,
    var isSelected: Boolean
) : Parcelable {
    override fun toString(): String = contactNumber
}