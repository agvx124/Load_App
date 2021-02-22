package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var fileName = ""
    private var downloadStatus = false

    private lateinit var downloadManager: DownloadManager

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_title)
        )

        // TEST
        custom_button.setBtnState(ButtonState.Loading)

        custom_button.setOnClickListener {
            when {
                radio_btn_glide.isChecked -> {
                    fileName = radio_btn_glide.text.toString()
                    download(URL_GLIDE)
                }
                radio_btn_udacity.isChecked -> {
                    fileName = radio_btn_udacity.text.toString()
                    download(URL_UDACITY)
                }
                radio_btn_retrofit.isChecked -> {
                    fileName = radio_btn_retrofit.text.toString()
                    download(URL_RETROFIT)
                }
                else -> {
                    Toast.makeText(applicationContext, "Please select the file to download", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val c = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
            if (c.moveToFirst()) {
                val status: Int = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                Log.i("statusDownload", status.toString())

                when (status) {
                    DownloadManager.STATUS_FAILED -> {
                        Log.i("statusDownload", "Failed")
                        downloadStatus = false
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        Log.i("statusDownload", "Finish")
                        downloadStatus = true
                    }
                }

                sendNotification()
                c.close()
            }
        }
    }

    private fun download(url: String) {
        custom_button.setBtnState(ButtonState.Clicked)

        val request =
                DownloadManager.Request(Uri.parse(url))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channel: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channel,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)

            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification() {
        notificationManager.sendNotification(
                getString(R.string.notification_description),
                applicationContext,
                fileName,
                downloadStatus
        )

        custom_button.setBtnState(ButtonState.Completed)

        Log.i("statusDownload", "Send notification")
    }

    companion object {
        private const val URL_GLIDE = "https://github.com/bumptech/glide"
        private const val URL_UDACITY = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
    }

}
