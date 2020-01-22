package com.meranged.schoolschedule.ui.teacherdetails

import android.R.attr.bitmap
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import com.meranged.schoolschedule.database.Teacher
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream


class TeacherDetailsViewModel(
    teacher_id:  Long = 0L,
    dataSource: SchoolScheduleDao
) : ViewModel() {

    /**
     * Hold a reference to SleepDatabase via its SleepDatabaseDao.
     */
    val db = dataSource

    var teacher: LiveData<Teacher>

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        teacher=db.getTeacher(teacher_id)
    }

    /** Coroutine setup variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */

    suspend fun updateTeacher(){
        withContext(Dispatchers.IO) {
            db.update(teacher.value!!)
        }
    }

    suspend fun deleteTeacher(){
        withContext(Dispatchers.IO) {
            db.deleteTeacher(teacher.value!!.teacherId)
        }
    }

    fun setPicture(p: Bitmap){
        val stream = ByteArrayOutputStream()
        p.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        teacher.value!!.photo = stream.toByteArray()

        uiScope.launch {
            updateTeacher()
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}