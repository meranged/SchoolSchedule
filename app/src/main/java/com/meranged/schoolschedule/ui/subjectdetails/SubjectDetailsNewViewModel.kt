package com.meranged.schoolschedule.ui.subjectdetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SubjectDetailsNewViewModel(
    val database: SchoolScheduleDao,
    application: Application
) : AndroidViewModel(application) {

    val db = database

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val teachers_list = db.getAllTeachers()

    init {

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}