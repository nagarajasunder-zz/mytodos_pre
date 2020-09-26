package com.geekydroid.mytodos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceNavigationView
import com.luseen.spacenavigation.SpaceOnClickListener

private var FLAG = 0

class Todo : AppCompatActivity() {

    private val TAG = "Todo"

    //views
    private lateinit var bottombar: SpaceNavigationView
    private lateinit var viewpager: ViewPager2
    private lateinit var tab_layout: TabLayout

    //vars
    private lateinit var list: ArrayList<String>
    private lateinit var helper: MydatabaseHelper
    private lateinit var adapter: Viewpager_Adapter
    private var first_start = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)


        var prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        first_start = prefs.getBoolean("first_start", true)
        if (first_start) {
            add_new_category()
            var prefs2 = getSharedPreferences("prefs", MODE_PRIVATE)
            var editor = prefs2.edit()
            editor.putBoolean("first_start", false)
            editor.apply()
        }

        setUI()
        if (FLAG == 0) {
            fetch_data("NOT_EXPIRED")
        }
        if (FLAG == 1) {
            fetch_data("EXPIRED")
        }

        bottombar.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {
                var intent = Intent(applicationContext, New_task::class.java)
                intent.putExtra("TYPE", "NEW")
                startActivity(intent)
            }

            override fun onItemClick(itemIndex: Int, itemName: String?) {
                when (itemIndex) {
                    0 -> {
                        FLAG = 0
                        fetch_data("NOT_EXPIRED")
                    }
                    1 -> {
                        FLAG = 1
                        fetch_data("EXPIRED")
                    }
                }
            }

            override fun onItemReselected(itemIndex: Int, itemName: String?) {
            }

        })

    }


    private fun fetch_data(validity: String) {
        list.clear()
        list = helper.get_all_categories()
        adapter = Viewpager_Adapter(list, applicationContext, validity)
        viewpager.adapter = adapter
        TabLayoutMediator(tab_layout, viewpager) { tab, pos ->
            tab.text = "${list.get(pos)}"
            var count = helper.get_task_count(list.get(pos), validity)
            tab.removeBadge()
            if (count > 0) {
                var drawable1 = tab.orCreateBadge
                drawable1.number = count
            }
        }.attach()

    }

    private fun add_new_category() {
        var helper = MydatabaseHelper(this)
        helper.add_new_category("Todo")
    }


    private fun setUI() {

        viewpager = findViewById(R.id.viewpager)
        tab_layout = findViewById(R.id.tab_layout)
        bottombar = findViewById(R.id.bottom_bar)
        helper = MydatabaseHelper(this)

        list = ArrayList()


        bottombar.addSpaceItem(SpaceItem("", R.drawable.todo))
        bottombar.addSpaceItem(SpaceItem("", R.drawable.done))
        bottombar.addSpaceItem(SpaceItem("",R.drawable.habit_tracker))
        bottombar.addSpaceItem(SpaceItem("",R.drawable.settings))
    }

    override fun onResume() {
        super.onResume()
        if (FLAG == 0) {
            fetch_data("NOT_EXPIRED")
        }
        if (FLAG == 1) {
            fetch_data("EXPIRED")
        }
    }

}