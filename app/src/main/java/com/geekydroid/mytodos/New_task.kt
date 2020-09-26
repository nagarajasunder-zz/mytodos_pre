package com.geekydroid.mytodos

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class New_task : AppCompatActivity() {

    private val TAG = "New_task"

    //views
    private lateinit var title: TextInputLayout
    private lateinit var desc: TextInputLayout
    private lateinit var low: Button
    private lateinit var mid: Button
    private lateinit var high: Button
    private lateinit var add_task: Button
    private lateinit var time: TextView
    private lateinit var date: TextView
    private lateinit var complete: ImageView
    private lateinit var delete: ImageView
    private lateinit var notificationgroup: RadioGroup
    private lateinit var bottom_layout: RelativeLayout

    private lateinit var alarm: RadioButton
    private lateinit var notification: RadioButton

    private lateinit var category_spinner: Spinner

    //vars
    private lateinit var category_adapter: ArrayAdapter<String>
    private lateinit var category_list: ArrayList<String>
    private lateinit var main_calendar: Calendar
    private lateinit var textWatcher: TextWatcher
    private lateinit var helper: MydatabaseHelper
    private lateinit var Title: String
    private lateinit var Task_desc: String
    private lateinit var Priority: String
    private lateinit var Type: String
    private lateinit var Category: String
    private var Task_time_in_ms = ""
    private lateinit var task: Task_class
    private var max: Int = -1
    private lateinit var TYPE: String
    private lateinit var task_view: Task_class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        TYPE = intent.getStringExtra("TYPE").toString()
        if (TYPE.equals("VIEW")) {
            task_view =
                intent.getBundleExtra("bundle")!!.getParcelable<Task_class>("task") as Task_class
        }
        setUI()
        fetch_category_data("Todo")

        high.setOnClickListener {
            set_priority("HIGH", high, R.drawable.pri_high)
            un_select_others(mid, low)
        }
        mid.setOnClickListener {
            set_priority("MID", mid, R.drawable.pri_mid)
            un_select_others(high, low)
        }
        low.setOnClickListener {
            set_priority("LOW", low, R.drawable.pri_low)
            un_select_others(high, mid)
        }

        time.setOnClickListener {
            open_time_picker()
        }
        date.setOnClickListener {
            open_date_picker()
        }


        category_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when (category_list.get(position)) {
                    "Add New Category" -> {
                        open_dialog()
                    }
                    else -> {
                        Category = category_list.get(position)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        add_task.setOnClickListener {
            Title = title.editText!!.text.toString()
            Task_desc = desc.editText!!.text.toString()
            if (TYPE.equals("NEW")) {
                create_new_alarm()
            } else {
                update_alarm()
            }
        }

        notification.setOnClickListener {
            Type = "NOTIFICATION"
        }
        alarm.setOnClickListener {
            Type = "ALARM"
        }

        delete.setOnClickListener {
            open_delete_dialog()
        }
        complete.setOnClickListener {
            complete_task()
        }
    }

    private fun complete_task() {
        helper.update_completed_task(task_view.task_id!!, System.currentTimeMillis())
        finish()
    }

    private fun open_delete_dialog() {
        var dialog = AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Do you want to delete the task ?")
            .setPositiveButton(
                "Yes"
            ) { p0, p1 ->
                helper.delete_specific_task(task_view)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
        dialog.show()
    }

    private fun update_alarm() {
        task = Task_class(
            task_view.task_id,
            Title,
            Task_desc,
            Type,
            Priority,
            Task_time_in_ms,
            task_view.task_expired,
            task_view.task_expired,
            Category
        )
        helper.update_specific_task(task)
        set_alarm()
    }

    private fun create_new_alarm() {
        task = Task_class(
            max.toString(),
            Title,
            Task_desc,
            Type,
            Priority,
            Task_time_in_ms,
            "NOT_EXPIRED",
            "",
            Category
        )
        var result = helper.create_new_task(task)
        if (result.toInt() != -1) {
            max = helper.get_max_task_id()
            task.task_id = max.toString()
            set_alarm()
        }
    }


    private fun set_priority(priority: String, button: Button, bg: Int) {
        Priority = priority
        button.setTextColor(Color.WHITE)
        button.setBackgroundResource(bg)

    }

    private fun fetch_category_data(category: String) {
        category_list.clear()
        category_list = helper.get_all_categories()
        category_list.add("Add New Category")
        category_adapter = ArrayAdapter(applicationContext, R.layout.spinner_item, category_list)
        category_spinner.adapter = category_adapter
        category_spinner.setSelection(category_adapter.getPosition(category))
    }

    private fun open_dialog() {
        var view =
            LayoutInflater.from(applicationContext).inflate(R.layout.new_category_layout, null)
        var category = view.findViewById<EditText>(R.id.category)
        var dialog: AlertDialog = AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", null)
            .show()

        var button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        button.setOnClickListener {
            var Category = category.text.toString()
            if (Category.trim().isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please enter a new category",
                    Toast.LENGTH_SHORT
                ).show()
                category.requestFocus()
            } else {
                category.setText("")
                helper.add_new_category(Category)
                fetch_category_data(Category)
                Toast.makeText(
                    applicationContext,
                    "New Category added successfully",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

            }
        }

    }

    private fun set_alarm() {
        var alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        var intent = Intent(applicationContext, Alarm_Receiver::class.java)
        intent.putExtra("task_id", task.task_id)
        var pendingIntent =
            PendingIntent.getBroadcast(
                applicationContext,
                task.task_id!!.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            task.task_time_in_ms!!.toLong(),
            pendingIntent
        )
        Toast.makeText(
            applicationContext,
            "Remainder set for ${SimpleDateFormat("hh:mm").format(Date(main_calendar.timeInMillis))}",
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    private fun open_date_picker() {
        var datePickerDialog = DatePickerDialog(
            this,
            { view, year, month, day ->
                main_calendar.set(Calendar.YEAR, year)
                main_calendar.set(Calendar.MONTH, month)
                main_calendar.set(Calendar.DAY_OF_MONTH, day)
                Task_time_in_ms = main_calendar.timeInMillis.toString()
                date.setText(SimpleDateFormat("EEE,MMM d").format(Date(main_calendar.timeInMillis)))
                if (TYPE.equals("VIEW")) {
                    add_task.visibility = View.VISIBLE
                }
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun open_time_picker() {
        var timepicker = TimePickerDialog(
            this,
            { timePicker, hour, minute ->
                main_calendar.set(Calendar.HOUR_OF_DAY, hour)
                main_calendar.set(Calendar.MINUTE, minute)
                Task_time_in_ms = main_calendar.timeInMillis.toString()
                time.setText(SimpleDateFormat("HH:mm").format(Date(main_calendar.timeInMillis)))
                if (TYPE.equals("VIEW")) {
                    add_task.visibility = View.VISIBLE
                }
            },
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            true
        )

        timepicker.show()
    }

    private fun un_select_others(b1: Button, b2: Button) {
        b1.setBackgroundResource(R.drawable.pri_button_bg)
        b2.setBackgroundResource(R.drawable.pri_button_bg)
        b1.setTextColor(Color.BLACK)
        b2.setTextColor(Color.BLACK)
    }

    private fun setUI() {

        helper = MydatabaseHelper(applicationContext)

        if (TYPE.equals("NEW")) {
            Task_desc = ""
            Priority = "LOW"
            Type = "NOTIFICATION"
        } else {
            Task_desc = task_view.task_desc!!
            Priority = task_view.task_priority!!
            Type = task_view.task_type!!
        }
        bottom_layout = findViewById(R.id.bottom_layout)
        main_calendar = Calendar.getInstance()
        main_calendar.set(Calendar.YEAR, main_calendar.get(Calendar.YEAR))
        main_calendar.set(Calendar.MONTH, main_calendar.get(Calendar.MONTH))
        main_calendar.set(Calendar.DAY_OF_MONTH, main_calendar.get(Calendar.DAY_OF_MONTH))
        main_calendar.set(Calendar.HOUR, main_calendar.get(Calendar.HOUR))
        main_calendar.set(Calendar.MINUTE, main_calendar.get(Calendar.MINUTE))
        main_calendar.set(Calendar.SECOND, 0)
        main_calendar.set(Calendar.MILLISECOND, 0)
        if (TYPE.equals("NEW")) {
            Task_time_in_ms = main_calendar.timeInMillis.toString()
        } else {
            Task_time_in_ms = task_view.task_time_in_ms!!
        }
        title = findViewById<TextInputLayout>(R.id.title)
        desc = findViewById<TextInputLayout>(R.id.desc)
        time = findViewById<TextView>(R.id.time)
        delete = findViewById(R.id.delete)
        complete = findViewById(R.id.complete)
        date = findViewById(R.id.date)
        low = findViewById<Button>(R.id.low)
        mid = findViewById<Button>(R.id.mid)
        high = findViewById<Button>(R.id.high)
        add_task = findViewById<Button>(R.id.add_task)
        notificationgroup = findViewById<RadioGroup>(R.id.notification_group)
        alarm = findViewById<RadioButton>(R.id.alarm)
        notification = findViewById<RadioButton>(R.id.notification)
        category_spinner = findViewById(R.id.category_spinner)
        category_list = ArrayList()

        if (TYPE.equals("VIEW")) {
            title.editText!!.setText(task_view.task_name)
        }

        if (TYPE.equals("VIEW")) {
            desc.editText!!.setText(task_view.task_desc)
        }

        if (TYPE.equals("NEW")) {
            time.setText(SimpleDateFormat("HH:mm").format(Date()))
            date.setText(SimpleDateFormat("EEE,MMM dd").format(Date()))
        } else {
            time.setText(SimpleDateFormat("HH:mm").format(Date(task_view.task_time_in_ms!!.toLong())))
            date.setText(SimpleDateFormat("EEE,MMM dd").format(Date(task_view.task_time_in_ms!!.toLong())))
        }

        if (TYPE.equals("VIEW")) {
            low.setTextColor(Color.BLACK)
            when (task_view.task_priority) {
                "LOW" -> {
                    set_priority("LOW", low, R.drawable.pri_low)
                }
                "MID" -> {
                    set_priority("MID", mid, R.drawable.pri_mid)
                }
                "HIGH" -> {
                    set_priority("HIGH", high, R.drawable.pri_high)
                }
            }
        }
        if (TYPE.equals("VIEW")) {
            add_task.setText("Save")
        }
        if (TYPE.equals("VIEW")) {
            if (task_view.task_type.equals("NOTIFICATION")) {
                notification.isChecked = true
                alarm.isChecked = false
                Type = "NOTIFICATION"
            } else {
                notification.isChecked = false
                alarm.isChecked = true
                Type = "ALARM"
            }
        }

        if (TYPE.equals("VIEW")) {
            bottom_layout.visibility = View.VISIBLE
        }
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!text!!.trim().isEmpty()) {
                    add_task.visibility = View.VISIBLE
                } else {
                    add_task.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        }
        title.editText!!.addTextChangedListener(textWatcher)
    }


}
