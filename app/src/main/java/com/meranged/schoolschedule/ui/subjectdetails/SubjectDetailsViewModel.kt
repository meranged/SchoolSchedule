package com.meranged.schoolschedule.ui.subjectdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import com.meranged.schoolschedule.database.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class SubjectDetailsViewModel(
    subject_id:  Long = 0L,
    dataSource: SchoolScheduleDao
) : ViewModel() {

    /**
     * Hold a reference to SleepDatabase via its SleepDatabaseDao.
     */
    val db = dataSource

    private var subject: LiveData<Subject>

    fun getSubject() = subject

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        subject=db.getSubject(subject_id)
    }

    /** Coroutine setup variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */

    suspend fun updateSubject(){
        withContext(Dispatchers.IO) {
            db.update(subject.value!!)
        }
    }

    suspend fun deleteSubject(){
        withContext(Dispatchers.IO) {
            db.deleteTeacher(subject.value!!.teacherId)
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}