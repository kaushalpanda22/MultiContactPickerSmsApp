package com.android.multicontactpicker.sms.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object ShareUtils {

    fun sendSmsIntent(context: Context, message: String, phoneNumber: String) {
        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = Uri.parse("sms:$phoneNumber")
        sendIntent.putExtra("sms_body", message)
        context.startActivity(sendIntent)
    }
}