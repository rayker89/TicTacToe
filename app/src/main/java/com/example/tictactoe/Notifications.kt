package com.example.tictactoe

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import kotlin.coroutines.coroutineContext

class Notifications() {
    val notifyTag = "New request"




    fun Notify(context: Context, message:String, number:Int) {

        val intent = Intent(context, Login::class.java)

        val builder = NotificationCompat.Builder(context, "TicTacToeGame")
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle("New request")
            .setContentText(message)
            .setNumber(number)
            .setSmallIcon(R.drawable.tictac)
            .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
            .setAutoCancel(true)

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR ) {
            nm.notify(notifyTag, 0, builder.build())
        } else {
            nm.notify(notifyTag.hashCode(), builder.build())
        }
    }
}