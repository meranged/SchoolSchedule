package com.meranged.schoolschedule.ui.myteachers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import kotlinx.coroutines.*

class MyTeachersViewModel(
    val database: SchoolScheduleDao,
    application: Application
) : AndroidViewModel(application) {

    val db = database

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val teachers_with_subjects_list = db.getTeacherWithSubjects()

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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

