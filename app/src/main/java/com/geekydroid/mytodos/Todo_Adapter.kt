package com.geekydroid.mytodos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Todo_Adapter(var list: ArrayList<Task_class>, var validity: String) :
    RecyclerView.Adapter<Todo_Adapter.ViewHolder>() {
    private var Date_day: ArrayList<String> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var expired_view =
            LayoutInflater.from(parent.context).inflate(R.layout.todo_item_expired, parent, false)
        var not_expired_view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_item_not_expired, parent, false)
        if (viewType == 1) {
            return ViewHolder(not_expired_view)
        } else {
            return ViewHolder(expired_view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.task_title.setText(list.get(position).task_name)
        holder.task_desc.setText(list.get(position).task_desc!!.replace("\n", ""))
        holder.priority.setText("${list.get(position).task_priority} Priority")
        if (list.get(position).task_priority.equals("HIGH")) {
            holder.priority.setBackgroundResource(R.drawable.pri_high)
        } else if (list.get(position).task_priority.equals("MID")) {
            holder.priority.setBackgroundResource(R.drawable.pri_mid)
        } else {
            holder.priority.setBackgroundResource(R.drawable.pri_low)
        }
        if (validity.equals("NOT_EXPIRED")) {
            holder.time.setText(SimpleDateFormat("EEE,dd MMM").format(Date(list.get(position).task_time_in_ms!!.toLong())))
            if (System.currentTimeMillis() < list.get(position).task_time_in_ms!!.toLong()) {
                var diff =
                    (list.get(position).task_time_in_ms!!.toLong() - System.currentTimeMillis())
                holder.falls_on.setText("In ${(diff / (24 * 60 * 60 * 1000)) % 30}d ${(diff / (60 * 60 * 1000)) % 24}hr ${(diff / (60 * 1000)) % 60}min")
            } else {
                holder.falls_on.setText("Expired")
            }
        } else {
            var date_day = SimpleDateFormat("EEE dd/MM/yyyy").format(Date(list.get(position).task_expired_on!!.toLong()))
            holder.time.setText(
                "Finished at ${SimpleDateFormat("HH:mm").format(Date(list.get(position).task_expired_on!!.toLong()))}"
            )
            if (position == 0 || Date_day.indexOf(date_day) == -1) {
                holder.date_day.visibility = View.VISIBLE
                holder.date_day.setText(date_day)
                Date_day.add(date_day)
            }
        }
        holder.main_layout.setOnClickListener {
            var intent = Intent(holder.itemView.context, New_task::class.java)
            var bundle = Bundle()
            bundle.putParcelable("task", list.get(position))
            intent.putExtra("bundle", bundle)
            intent.putExtra("TYPE", "VIEW")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var time = itemview.findViewById<TextView>(R.id.time)
        var priority = itemview.findViewById<TextView>(R.id.priority)
        var task_title = itemview.findViewById<TextView>(R.id.task_title)
        var task_desc = itemview.findViewById<TextView>(R.id.task_desc)
        var falls_on = itemview.findViewById<TextView>(R.id.falls_on)
        var main_layout = itemview.findViewById<CardView>(R.id.main_layout)
        var date_day = itemview.findViewById<TextView>(R.id.date_day)
    }

    override fun getItemViewType(position: Int): Int {
        return if (validity.equals("NOT_EXPIRED")) {
            1
        } else {
            0
        }
    }
}