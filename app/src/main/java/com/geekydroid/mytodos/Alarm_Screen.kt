package com.geekydroid.mytodos

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.skyfishjy.library.RippleBackground
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "Alarm_Screen"

class Alarm_Screen : AppCompatActivity() {

    //views
    private lateinit var ripple: RippleBackground
    private lateinit var dismiss: Button
    private lateinit var snooze: Button
    private lateinit var text: TextView
    private lateinit var time: TextView

    //vars
    private lateinit var task: Task_class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm__screen)

        var time = System.currentTimeMillis() + (5 * 60 * 1000)
        var receiver = Alarm_Receiver()
        receiver.stop_alarm()
        var media = MediaPlayer.create(applicationContext, Settings.System.DEFAULT_RINGTONE_URI)
        media.start()
        media.isLooping = true
        task = intent.getBundleExtra("bundle")!!.getParcelable<Task_class>("task") as Task_class
        var delaytime = 60000 - (System.currentTimeMillis() - task.task_time_in_ms!!.toLong())
        Handler().postDelayed({
            receiver.update_alarm()
            media.stop()
            finish()
        }, delaytime)
        setUI()
        ripple.startRippleAnimation()
        dismiss.setOnClickListener {
            receiver.update_alarm()
            media.stop()
            Toast.makeText(applicationContext, "${task.task_name} Completed!", Toast.LENGTH_SHORT)
                .show()
            finish()
        }

        snooze.setOnClickListener {
            media.stop()
            var helper = MydatabaseHelper(applicationContext)
            helper.update_snooze_time(task.task_id!!,time.toString())
            var intent = Intent(applicationContext, Alarm_Receiver::class.java)
            intent.putExtra("task_id", task.task_id)
            var pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                task.task_id!!.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            var alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            Toast.makeText(
                applicationContext,
                "${task.task_name} snoozed for 5 minutes",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

    }

    private fun setUI() {

        ripple = findViewById(R.id.ripple)
        snooze = findViewById(R.id.snooze)
        dismiss = findViewById(R.id.dismiss)
        text = findViewById(R.id.text)
        text.setText(task.task_name)
        time = findViewById(R.id.time)
        time.setText(SimpleDateFormat("HH:mm").format(Date(task.task_time_in_ms!!.toLong())))
    }
}