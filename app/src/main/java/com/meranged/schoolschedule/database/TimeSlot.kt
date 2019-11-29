package com.meranged.schoolschedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_slot")
data class TimeSlot(
    @PrimaryKey(autoGenerate = true)
    var timeslotId: Long = 0L,

    @ColumnInfo(name = "number")
    var number: Int = 0,

    @ColumnInfo(name = "start_time_hours")
    var startTimeHours: Int = 0,

    @ColumnInfo(name = "start_time_minutes")
    var startTimeMinutes: Int = 0,

    @ColumnInfo(name = "finish_time_hours")
    var finishTimeHours: Int = 0,

    @ColumnInfo(name = "finish_time_minutes")
    var finishTimeMinutes: Int = 0,

    @ColumnInfo(name = "week_day")
    var weekDay: Int = 0,

    @ColumnInfo(name = "subject_id")
    var subject_id: Long = 0L,

    @ColumnInfo(name = "comment")
    var comment: String = ""

)