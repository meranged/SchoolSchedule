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
    }


    suspend fun updateWeekTimeSlot(ts: TimeSlot) {
        if (is_valid_ts(ts)) {
            withContext(Dispatchers.IO) {
                db.updateWeekTimeSlot(ts)
            }
        }
    }

    fun is_valid_ts(ts: TimeSlot): Boolean {
        if ((ts.startTimeHours*60 + ts.startTimeMinutes) >= (ts.finishTimeHours*60 + ts.finishTimeMinutes)){
            return false
        }

        if (ts.number > 1){
            if (etalon_slots.value != null) {
                for (prev_lesson in etalon_slots.value!!){
                    if (prev_lesson.number == (ts.number - 1)){
                        if ((prev_lesson.finishTimeHours*60 + prev_lesson.finishTimeMinutes) >=
                            (ts.startTimeHours*60 + ts.startTimeMinutes)){
                            return false
                        }
                    }
                }
            } else {
                return false
            }
        }

        return true
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
