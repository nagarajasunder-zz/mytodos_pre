package com.geekydroid.mytodos

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

private lateinit var manager: NotificationManager
private lateinit var alarmManager: AlarmManager
private lateinit var alarm_intent: Intent
private lateinit var pendingIntent: PendingIntent
private lateinit var receiver: Alarm_Receiver
private lateinit var helper: MydatabaseHelper

class Snooze_Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {


        helper = MydatabaseHelper(context)
        receiver = Alarm_Receiver()
        receiver.stop_alarm()
        var time = System.currentTimeMillis() + (5 * 60 * 1000)

        var task = intent!!.getBundleExtra("bundle")!!.getParcelable<Task_class>("task")
        manager = context!!.getSystemService(NotificationManager::class.java)
        manager.cancel(task!!.task_id!!.toInt())
        helper.update_snooze_time(task.task_id!!, time.toString())

        alarm_intent = Intent(context, Alarm_Receiver::class.java)
        alarm_intent.putExtra("task_id", task.task_id)

        pendingIntent = PendingIntent.getBroadcast(
            context,
            task.task_id!!.toInt(),
            alarm_intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        Toast.makeText(context, "${task.task_name} snoozed for five minutes", Toast.LENGTH_SHORT)
            .show()
    }
}