package com.meranged.schoolschedule.database

import androidx.room.Embedded
import androidx.room.Relation

data class TimeSlotWithSubjects (

    @Embedded val timeSlot: TimeSlot,
    @Relation(
        parentColumn = "subject_id",
        entityColumn = "subjectId"
    )
    val subjects: List<Subject>
)