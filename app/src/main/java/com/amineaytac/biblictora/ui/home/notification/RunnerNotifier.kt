package com.amineaytac.biblictora.ui.home.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.amineaytac.biblictora.MainActivity
import com.amineaytac.biblictora.R

class RunnerNotifier(
    notificationManager: NotificationManager, private val context: Context
) : Notifier(notificationManager) {

    override val notificationChannelId: String = "runner_channel_id"
    override val notificationChannelName: String = "Running Notification"
    override val notificationId: Int = 200

    override fun buildNotification(): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(context, notificationChannelId)
            .setContentTitle(getNotificationTitle()).setContentText(getNotificationMessage())
            .setSmallIcon(R.drawable.ic_book).setContentIntent(pendingIntent).setAutoCancel(true)
            .build()
    }

    override fun getNotificationTitle(): String {
        return "It's time to go read 📚"
    }

    override fun getNotificationMessage(): String {
        return "Are you ready to read?"
    }
}