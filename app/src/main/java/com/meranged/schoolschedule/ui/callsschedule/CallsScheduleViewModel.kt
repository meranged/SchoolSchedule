package com.meranged.schoolschedule.ui.callsschedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import com.meranged.schoolschedule.database.TimeSlot
import kotlinx.coroutines.*

class CallsScheduleViewModel(
    val database: SchoolScheduleDao,
    application: Application
) : AndroidViewModel(application) {

    val db = database

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val etalon_slots = db.getEtalonTimeSlots()

    init {
        uiScope.launch {
            fillDPB()
        }
    }

    private suspend fun fillDPB(){
        withContext(Dispatchers.IO) {
            db.checkAndFillTimeSlots()
        }
    }

    suspend fun updateWeekTimeSlot(ts: TimeSlot) {
            withContext(Dispatchers.IO) {
                db.updateWeekTimeSlot(ts)
            }
    }

    suspend fun addTimeSlot(){
        val list = etalon_slots.value
        val timeshift = 50//shift in minutes for a new timeslot
        if (!list.isNullOrEmpty()){
            withContext(Dispatchers.IO) {
                val lastTimeslot = list[list.size - 1]
                var newTimeSlot = TimeSlot()
                newTimeSlot.number = lastTimeslot.number + 1
                newTimeSlot.weekDay = lastTimeslot.weekDay
                newTimeSlot.startTimeHours = (lastTimeslot.startTimeHours * 60 + lastTimeslot.startTimeMinutes + timeshift) / 60
                newTimeSlot.startTimeMinutes = (lastTimeslot.startTimeMinutes + timeshift) % 60
                newTimeSlot.finishTimeHours = (lastTimeslot.finishTimeHours * 60 + lastTimeslot.finishTimeMinutes + timeshift) / 60
                newTimeSlot.finishTimeMinutes = (lastTimeslot.finishTimeMinutes + timeshift) % 60
                db.addEtalonTimeSlot(newTimeSlot)
            }
        }
    }

    suspend fun removeTimeSlot(){
        val list = etalon_slots.value
        if (!list.isNullOrEmpty()) {
            val timeslotToDelete = list[list.size - 1]
            withContext(Dispatchers.IO) {
                db.deleteEtalonTimeslot(timeslotToDelete.number)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
