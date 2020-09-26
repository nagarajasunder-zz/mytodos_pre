package com.geekydroid.mytodos

import android.os.Parcel
import android.os.Parcelable

class Task_class(
    var task_id: String?,
    var task_name: String?,
    var task_desc: String?,
    var task_type: String?,
    var task_priority: String?,
    var task_time_in_ms: String?,
    var task_expired: String?,
    var task_expired_on: String?,
var task_category: String?):Parcelable
{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(task_id)
        parcel.writeString(task_name)
        parcel.writeString(task_desc)
        parcel.writeString(task_type)
        parcel.writeString(task_priority)
        parcel.writeString(task_time_in_ms)
        parcel.writeString(task_expired)
        parcel.writeString(task_expired_on)
        parcel.writeString(task_category)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task_class> {
        override fun createFromParcel(parcel: Parcel): Task_class {
            return Task_class(parcel)
        }

        override fun newArray(size: Int): Array<Task_class?> {
            return arrayOfNulls(size)
        }
    }

}

