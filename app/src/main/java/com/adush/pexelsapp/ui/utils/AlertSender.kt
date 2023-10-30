package com.adush.pexelsapp.ui.utils

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

object AlertSender {

    fun sendMessage(context: Context, msg: String) {
        val intent = Intent(Constants.ALERT_ACTION)
        intent.putExtra(Constants.ALERT_MESSAGE_EXTRA, msg)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}