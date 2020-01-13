package com.meranged.schoolschedule.database

import androidx.room.Embedded
import androidx.room.Relation

data class SubjectWithTeacher (

    @Embedded val subject: Subject,
    @Relation(
        parentColumn = "teacher_id",
        entityColumn = "teacherId"
    )
    val teachers: List<Teacher>
)