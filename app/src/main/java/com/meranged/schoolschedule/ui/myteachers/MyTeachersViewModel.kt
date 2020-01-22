package com.meranged.schoolschedule.ui.myteachers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import com.meranged.schoolschedule.database.Teacher
import com.meranged.schoolschedule.database.TimeSlot
import kotlinx.coroutines.*

class MyTeachersViewModel(
    val database: SchoolScheduleDao,
    application: Application
) : AndroidViewModel(application) {

    val db = database

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val teachers_list = db.getAllTeachers()
    val teachers_with_subjects = db.getTeacherWithSubjects()

    init {
        uiScope.launch {
            fillDPB()
        }
    }

    private suspend fun fillDPB() {
        withContext(Dispatchers.IO) {
            db.checkAndFillTeachersList()
        }
    }

    suspend fun updateWeekTimeSlot(teacher: Teacher) {
        withContext(Dispatchers.IO) {
            db.update(teacher)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

