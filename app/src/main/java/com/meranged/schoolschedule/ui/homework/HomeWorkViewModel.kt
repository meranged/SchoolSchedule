package com.meranged.schoolschedule.ui.homework

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import com.meranged.schoolschedule.database.TimeSlot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class HomeWorkViewModel(
        timeslot_id:  Long = 0L,
        dataSource: SchoolScheduleDao
    ) : ViewModel() {

        /**
         * Hold a reference to SleepDatabase via its SleepDatabaseDao.
         */
        val db = dataSource

        var timeslot: LiveData<TimeSlot>

        private val viewModelJob = Job()

        private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        init {
            timeslot = db.getTimeslot(timeslot_id)

        }

        /** Coroutine setup variables */

        /**
         * viewModelJob allows us to cancel all coroutines started by this ViewModel.
         */

        suspend fun updateTimeSlot(){

            withContext(Dispatchers.IO) {
                val ts = timeslot.value!!
                db.update(ts)
            }
        }

        override fun onCleared() {
            super.onCleared()
            viewModelJob.cancel()
        }

    }