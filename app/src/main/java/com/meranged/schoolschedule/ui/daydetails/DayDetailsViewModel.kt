package com.meranged.schoolschedule.ui.daydetails

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import com.meranged.schoolschedule.database.TimeSlot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext


class DayDetailsViewModel(
    timeslot_id:  Long = 0L,
    dataSource: SchoolScheduleDao
) : ViewModel() {

    /**
     * Hold a reference to SleepDatabase via its SleepDatabaseDao.
     */
    val db = dataSource

    var timeslot: LiveData<TimeSlot>
    val subjects_list = db.getAllSubjects()

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        timeslot = db.getTimeslot(timeslot_id)
        Log.i("ttt_timeslotInint", timeslot.value.toString())



    }

    /** Coroutine setup variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */

    suspend fun updateTimeSlot(subject_id:Long){


        withContext(Dispatchers.IO) {

            val ts = timeslot.value!!

            ts.subject_id = subject_id

            db.update(ts)
        }
    }

    suspend fun clearTimeSlot(){
        withContext(Dispatchers.IO) {

            val ts = timeslot.value!!

            ts.subject_id = -1

            db.update(ts)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}

