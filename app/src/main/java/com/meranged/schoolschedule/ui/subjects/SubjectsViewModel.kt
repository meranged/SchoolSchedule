package com.meranged.schoolschedule.ui.subjects

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import com.meranged.schoolschedule.database.Subject
import kotlinx.coroutines.*

class SubjectsViewModel(
    val database: SchoolScheduleDao,
    application: Application
) : AndroidViewModel(application) {

    val db = database

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val subjects_list = db.getAllSubjects()

    init {
        uiScope.launch {
            fillDPB()
        }
    }

    private suspend fun fillDPB() {
        withContext(Dispatchers.IO) {
            db.checkAndFillSubjectsList()
        }
    }

    suspend fun updateSubject(subject: Subject) {
        withContext(Dispatchers.IO) {
            db.update(subject)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}