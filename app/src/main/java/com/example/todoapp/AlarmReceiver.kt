package com.example.todoapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat


class AlarmReceiver : BroadcastReceiver() {

    companion object{
        const val ID = "NOTE_ID"
        const val NAME = "Note"
    }

    override fun onReceive(context: Context?, intent: Intent) {
        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val subject = intent.extras?.getString("subject")
        val description = intent.extras?.getString("description")
        val id = intent.extras?.getInt("id")


        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        val noteNotification: NotificationCompat.Builder =
                NotificationCompat.Builder(
                        context, ID)
                        .setContentTitle(subject)
                        .setContentText(description)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
        val note = NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_HIGH)
        note.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(note)
            if (id != null) {
                manager.notify(id, noteNotification.build())
            }
        }
    }
}