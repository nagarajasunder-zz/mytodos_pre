package com.geekydroid.mytodos

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.geekydroid.mytodos.Notification_Creator.Companion.CHANNEL_ID

private lateinit var media: MediaPlayer
private const val TAG = "Alarm_Receiver"
private lateinit var helper: MydatabaseHelper
private lateinit var task: Task_class
private lateinit var compat: NotificationManagerCompat
private lateinit var CompleteIntent: Intent
private lateinit var bundle: Bundle
private lateinit var ActionIntent: PendingIntent
private lateinit var Alarm_Screen_action: Intent
private lateinit var content_intent: PendingIntent
private lateinit var snooze_action: Intent
private lateinit var action_intent2: PendingIntent
private lateinit var notification: Notification
private lateinit var list: ArrayList<Task_class>

class Alarm_Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        var vibrate = longArrayOf(1000, 1000, 1000, 1000, 1000)
        helper = MydatabaseHelper(context)
        compat = NotificationManagerCompat.from(context!!)
        var task_id = intent!!.getStringExtra("task_id")
        list = ArrayList()
        list = helper.get_specific_task(task_id!!, "NOT_EXPIRED")

        if (list.size > 0) {
            task = list.get(0)
            //First Action Triggered when marks as done action is clicked
            CompleteIntent = Intent(context, Alarm_Received_Class::class.java)
            bundle = Bundle()
            bundle.putParcelable("task", task)
            CompleteIntent.putExtra("bundle", bundle)
            ActionIntent =
                PendingIntent.getBroadcast(context, task!!.task_id!!.toInt(), CompleteIntent, 0)
            //First Action

            //Second action When Notification_Clicked
            Alarm_Screen_action = Intent(context, Alarm_Screen::class.java)
            Alarm_Screen_action.putExtra("bundle", bundle)
            content_intent =
                PendingIntent.getActivity(context, task.task_id!!.toInt(), Alarm_Screen_action, 0)
            //Second Action

            //Third action when snooze clicked
            snooze_action = Intent(context, Snooze_Receiver::class.java)
            snooze_action.putExtra("bundle", bundle)
            action_intent2 =
                PendingIntent.getBroadcast(context, task.task_id!!.toInt(), snooze_action, 0)
            //Third action

            media = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI)

            if (task!!.task_type.equals("NOTIFICATION")) {
                notification = NotificationCompat.Builder(
                    context, CHANNEL_ID
                )
                    .setContentTitle(task!!.task_name)
                    .setContentText(task.task_desc)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setColor(Color.RED)
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .addAction(R.mipmap.ic_launcher, "Mark as done", ActionIntent)
                    .addAction(R.mipmap.ic_launcher, "Snooze", action_intent2)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setVibrate(vibrate)
                    .build()
                compat.notify(task.task_id!!.toInt(), notification)
            } else if (task.task_type.equals("ALARM")) {

                media.start()
                media.isLooping = true
                notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(task.task_name)
                    .setContentText(task.task_desc)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .addAction(R.mipmap.ic_launcher, "Mark as done", ActionIntent)
                    .addAction(R.mipmap.ic_launcher, "Snooze", action_intent2)
                    .setContentIntent(content_intent)
                    .setOngoing(true)
                    .build()

                compat.notify(task.task_id!!.toInt(), notification)

                Handler().postDelayed({
                    stop_alarm()
                }, 60000)
            }

        }

    }


    fun stop_alarm() {
        if (media.isPlaying) {
            media.stop()
        }
    }

    fun update_alarm() {
        helper.update_completed_task(task.task_id!!, System.currentTimeMillis())
    }
}