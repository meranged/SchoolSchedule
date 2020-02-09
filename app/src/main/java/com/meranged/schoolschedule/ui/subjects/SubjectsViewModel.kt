package com.meranged.schoolschedule.ui.subjects

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import com.meranged.schoolschedule.database.TimeSlot
import com.meranged.schoolschedule.getStringFromDaysList
import kotlinx.coroutines.*
import java.util.HashSet

class SubjectsViewModel(
    val database: SchoolScheduleDao,
    application: Application
) : AndroidViewModel(application) {

    val db = database

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val subjects_with_teachers_list = db.getSubjectsWithTeacher()
    val timeslots_list_gen = db.getAllTimeSlots()

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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun getSubjectWeekDays(subject_id:Long):String{

        val dayslist = ArrayList<Int>()

        var last_added_day = 0

        if (timeslots_list_gen.value != null) {

            if (!timeslots_list_gen.value.isNullOrEmpty()) {
                val ts_list = timeslots_list_gen.value
                for (item in ts_list!!) {
                    if (item.subject_id == subject_id) {
                        if (item.weekDay != last_added_day) {
                            dayslist.add(item.weekDay)
                            last_added_day = item.weekDay
                        }
                    }
                }
            }
        }

        return getStringFromDaysList(dayslist)
    }
}