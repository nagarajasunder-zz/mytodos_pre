package com.geekydroid.mytodos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MydatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val TAG = "MydatabaseHelper"

    private var TABLE_NAME1 = "TASKS"
    private var TABLE_NAME2 = "CATEGORY"

    private var T1C1 = "TASK_ID"
    private var T1C2 = "TASK_NAME"
    private var T1C3 = "DESCRIPTION"
    private var T1C4 = "TYPE"
    private var T1C5 = "PRIORITY"
    private var T1C6 = "TIME_IN_MS"
    private var T1C7 = "EXPIRED"
    private var T1C8 = "EXPIRED_ON"
    private var T1C9 = "CATEGORY"

    private var T2C1 = "C_ID"
    private var T2C2 = "C_NAME"


    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        var query1 =
            "CREATE TABLE ${TABLE_NAME1}($T1C1 INTEGER PRIMARY KEY AUTOINCREMENT , $T1C2 TEXT , $T1C3 TEXT , $T1C4 TEXT , $T1C5 TEXT , $T1C6 INTEGER , $T1C7 TEXT , $T1C8 TEXT , $T1C9 TEXT);"

        var query2 =
            "CREATE TABLE ${TABLE_NAME2}($T2C1 INTEGER PRIMARY KEY AUTOINCREMENT , $T2C2 TEXT);"
        sqLiteDatabase.execSQL(query1)
        sqLiteDatabase.execSQL(query2)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_NAME1")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLE_NAME2")
    }


    companion object {
        const val DATABASE_NAME = "mytodos.db"
        const val DATABASE_VERSION = 1
    }

    fun create_new_task(task: Task_class): Long {
        var database = this.writableDatabase
        var cv = ContentValues()
        cv.put(T1C2, task.task_name)
        cv.put(T1C3, task.task_desc)
        cv.put(T1C4, task.task_type)
        cv.put(T1C5, task.task_priority)
        cv.put(T1C6, task.task_time_in_ms)
        cv.put(T1C7, task.task_expired)
        cv.put(T1C8, task.task_expired_on)
        cv.put(T1C9, task.task_category)

        var result = database.insert(TABLE_NAME1, null, cv)

        return result
    }

    public fun get_max_task_id(): Int {
        var max = 0
        var database = this.readableDatabase
        var query = "SELECT MAX($T1C1) FROM $TABLE_NAME1"
        lateinit var cursor: Cursor
        cursor = database?.rawQuery(query, null)!!
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                max = cursor.getString(0).toInt()
            }
        }
        return max
    }

    fun update_completed_task(task_id: String, timeinmillsec: Long) {
        var database = this.writableDatabase
        var cv = ContentValues()
        cv.put(T1C7, "EXPIRED")
        cv.put(T1C8, timeinmillsec)
        if (database != null) {
            database.update(TABLE_NAME1, cv, "$T1C1 = ?", arrayOf(task_id))
        }
    }

    fun get_all_tasks(category: String, validity: String): ArrayList<Task_class> {
        var task: ArrayList<Task_class> = ArrayList()
        task.clear()
        var database = this.readableDatabase
        var query =
            "SELECT * FROM $TABLE_NAME1 WHERE $T1C9=? AND $T1C7=?"
        var cursor: Cursor?
        if (database != null) {
            cursor = database.rawQuery(query, arrayOf(category, validity))
            if (cursor != null && cursor.count > 0) {
                while (cursor.moveToNext()) {
                    task.add(
                        Task_class(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getString(8)
                        )
                    )
                }
            }
        }

        Log.d(TAG, "get_all_tasks: ${task.size} $validity")
        return task
    }

    fun add_new_category(category: String): Long {
        var database = this.writableDatabase
        var cv = ContentValues()
        cv.put(T2C2, category)
        var result = database.insert(TABLE_NAME2, null, cv)
        return result
    }

    fun get_all_categories(): ArrayList<String> {
        var list = ArrayList<String>()
        var database = this.readableDatabase
        var cursor: Cursor? = null
        var query = "SELECT * FROM $TABLE_NAME2"
        if (database != null) {
            cursor = database.rawQuery(query, null)
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(1))
                }
            }
        }
        return list
    }

    fun get_task_count(category: String, validity: String): Int {
        var count = 0
        var database = this.readableDatabase
        var query =
            "SELECT COUNT(${T1C1}) FROM $TABLE_NAME1 WHERE $T1C7=? AND $T1C9 =?"
        if (database != null) {
            var cursor = database.rawQuery(query, arrayOf(validity, category))
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    count = cursor.getString(0).toInt()
                }
            }
        }
        return count
    }

    fun get_specific_task(id: String, validity: String): ArrayList<Task_class> {
        var list: ArrayList<Task_class> = ArrayList()
        var query = "SELECT * FROM $TABLE_NAME1 WHERE $T1C1=? AND $T1C7=?"
        var database = this.writableDatabase
        if (database != null) {
            var cursor = database.rawQuery(query, arrayOf(id, validity))
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    list.add(
                        Task_class(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7),
                            cursor.getString(8)
                        )
                    )
                }
            }
        }
        return list

    }

    fun update_specific_task(task: Task_class) {
        var database = this.writableDatabase
        var cv = ContentValues()
        cv.put(T1C2, task.task_name)
        cv.put(T1C3, task.task_desc)
        cv.put(T1C4, task.task_type)
        cv.put(T1C5, task.task_priority)
        cv.put(T1C6, task.task_time_in_ms)
        cv.put(T1C7, task.task_expired)
        cv.put(T1C8, task.task_expired_on)
        cv.put(T1C9, task.task_category)
        if (database != null) {
            var result = database.update(TABLE_NAME1, cv, "$T1C1 = ?", arrayOf(task.task_id))
        }
    }

    fun delete_specific_task(task: Task_class) {
        var database = this.writableDatabase
        database.delete(TABLE_NAME1, "$T1C1 = ?", arrayOf(task.task_id))
    }

    fun update_snooze_time(task_id: String, timeinmillsec: String) {
        var database = this.writableDatabase
        var cv = ContentValues()
        cv.put(T1C6, timeinmillsec)
        database.update(TABLE_NAME1, cv, "$T1C1 = ?", arrayOf(task_id))
    }
}