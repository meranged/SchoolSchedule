package com.meranged.schoolschedule.database
import androidx.room.Embedded
import androidx.room.Relation

data class TeacherWithSubjects (

    @Embedded val teacher: Teacher,
    @Relation(
        parentColumn = "teacherId",
        entityColumn = "teacher_id"
    )
    val subjects: List<Subject>
)
