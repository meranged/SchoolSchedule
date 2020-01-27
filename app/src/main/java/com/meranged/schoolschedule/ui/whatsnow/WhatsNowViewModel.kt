package com.meranged.schoolschedule.ui.whatsnow

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import com.meranged.schoolschedule.database.Teacher
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.sql.Time
import java.util.*


class WhatsNowViewModel(
    dataSource: SchoolScheduleDao
) : ViewModel() {

    /**
     * Hold a reference to SleepDatabase via its SleepDatabaseDao.
     */
    val db = dataSource
    val UNKNOWN = 0
    val ORDINARY_LESSON = 1
    val LAST_LESSON = 2
    val BEFORE_SCHOOL = 3
    val FREE_TIME = 4


    private val viewModelJob = Job()

    var calendar = Calendar.getInstance()
    val time_slots_with_subjects = db.getTimeSlotWithSubjects()
    val teachers_list = db.getAllTeachers()
    var now_status = UNKNOWN
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {

    }

    /** Coroutine setup variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}