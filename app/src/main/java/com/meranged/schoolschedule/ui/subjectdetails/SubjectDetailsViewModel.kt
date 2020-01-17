package com.meranged.schoolschedule.ui.subjectdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import com.meranged.schoolschedule.database.Subject
import com.meranged.schoolschedule.database.Teacher
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

    val teachers_list = db.getAllTeachers()

    init {
        subject = db.getSubject(subject_id)
    }

    /** Coroutine setup variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */

    suspend fun updateSubject(teacher_id:Long){
        withContext(Dispatchers.IO) {
            subject.value!!.teacherId = teacher_id
            db.update(subject.value!!)
        }
    }

    suspend fun deleteSubject(){
        withContext(Dispatchers.IO) {
            db.deleteSubject(subject.value!!.subjectId)
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}