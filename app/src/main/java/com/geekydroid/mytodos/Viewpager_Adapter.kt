package com.geekydroid.mytodos

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

private const val TAG = "Viewpager_Adapter"

class Viewpager_Adapter() : RecyclerView.Adapter<Viewpager_Adapter.ViewHolder>() {

    private lateinit var list: ArrayList<String>
    private lateinit var helper: MydatabaseHelper
    private lateinit var task: ArrayList<Task_class>
    private lateinit var validity: String

    constructor(list: ArrayList<String>, context: Context, validity: String) : this() {
        Log.d(TAG, "$validity: ")
        this.validity = validity
        this.list = list
        helper = MydatabaseHelper(context)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.viewpager_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.recyclerView.setHasFixedSize(true)
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        task = ArrayList()
        task = helper.get_all_tasks(list.get(position), validity)
        if (task.size > 0) {
            var adapter = Todo_Adapter(task,validity)
            holder.recyclerView.adapter = adapter
            holder.recyclerView.visibility = View.VISIBLE
        } else {
            if (validity.equals("EXPIRED")) {
                holder.lottie.setAnimation(R.raw.no_tasks_anim)
            } else {
                holder.lottie.setAnimation(R.raw.all_task_completed)
            }
            holder.lottie.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_view)
        var lottie = itemview.findViewById<LottieAnimationView>(R.id.lottie)
    }
}