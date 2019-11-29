package com.meranged.schoolschedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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