package com.udacity.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R


const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
        messageBody: String,
        applicationContext: Context,
        fileName: String,
        downloadStatus: Boolean
) {

    val intentContent = Intent(applicationContext, DetailActivity::class.java).apply {
        putExtra("fileName", fileName)
        putExtra("downloadStatus", downloadStatus)
    }

    val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            intentContent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_channel_id)
    )
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext
                    .getString(R.string.notification_title))
            .setContentText(messageBody)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())

}