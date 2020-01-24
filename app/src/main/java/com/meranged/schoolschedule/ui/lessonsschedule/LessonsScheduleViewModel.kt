package com.meranged.schoolschedule.ui.lessonsschedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import kotlinx.coroutines.*

class LessonsScheduleViewModel(
    val database: SchoolScheduleDao,
    application: Application
) : AndroidViewModel(application) {

    val db = database

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val time_slots_with_subjects = db.getTimeSlotWithSubjects()
    val teachers_list = db.getAllTeachers()

    init {

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
