package com.geekydroid.mytodos

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class Alarm_Received_Class : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        var helper = MydatabaseHelper(context)
        var task = intent!!.getBundleExtra("bundle")!!.getParcelable<Task_class>("task")
        var manager = context!!.getSystemService(NotificationManager::class.java)
        manager.cancel(task!!.task_id!!.toInt())

        helper.update_completed_task(task.task_id!!, System.currentTimeMillis())
        Toast.makeText(context,"${task.task_name} Completed!",Toast.LENGTH_SHORT).show()

        if (task.task_type.equals("ALARM")) {
            var receiver = Alarm_Receiver()
            receiver.stop_alarm()
        }
    }
}