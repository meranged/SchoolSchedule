package com.meranged.schoolschedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity(tableName = "subject")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    var subjectId: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "room_number")
    var roomNumber: Int = 0,

    @ColumnInfo(name = "teacher_id")
    var teacherId: Long = 0L
)

@Entity(tableName = "teacher")
data class Teacher(
    @PrimaryKey(autoGenerate = true)
    var teacherId: Long = 0L,

    @ColumnInfo(name = "first_name")
    val firstName: String = "",

    @ColumnInfo(name = "second_name")
    val secondName: String = "",

    @ColumnInfo(name = "third_name")
    val thirdName: String = "",

    @ColumnInfo(name = "nick_name")
    val nickName: String = "",

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB, name = "photo")
    var photo: ByteArray? = null
)

@Entity(tableName = "time_slot")
data class TimeSlot(
    @PrimaryKey(autoGenerate = true)
    var timeslotId: Long = 0L,

    @ColumnInfo(name = "number")
    val number: Int = 0,

    @ColumnInfo(name = "start_time_hours")
    var startTimeHours: Int = 0,

    @ColumnInfo(name = "start_time_minutes")
    var startTimeMinutes: Int = 0,

    @ColumnInfo(name = "finish_time_hours")
    var finishTimeHours: Int = 0,

    @ColumnInfo(name = "finish_time_minutes")
    var finishTimeMinutes: Int = 0,

    @ColumnInfo(name = "week_day")
    var weekDay: Int = 0
)

@Entity(tableName = "lesson")
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    var lessonId: Long = 0L,

    @ColumnInfo(name = "time_slot_id")
    val timeslotId: Long = 0L,

    @ColumnInfo(name = "subject_id")
    var subjectId: Long = 0L,

    @ColumnInfo(name = "comment")
    var comment: String = ""
)