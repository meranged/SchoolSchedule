package com.meranged.schoolschedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subject")
data class Subject(
    @PrimaryKey(autoGenerate = true)
    var subjectId: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "room_number")
    var roomNumber: String = "",

    @ColumnInfo(name = "teacher_id")
    var teacherId: Long = 0L
)