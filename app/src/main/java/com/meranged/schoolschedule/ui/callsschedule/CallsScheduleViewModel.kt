package com.meranged.schoolschedule.ui.callsschedule

import android.app.Application
import androidx.lifecycle.*
import com.meranged.schoolschedule.App
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
