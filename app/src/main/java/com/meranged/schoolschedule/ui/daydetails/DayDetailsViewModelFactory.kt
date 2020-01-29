package com.meranged.schoolschedule.ui.daydetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meranged.schoolschedule.database.SchoolScheduleDao

class DayDetailsViewModelFactory(
    private val timeslot_id: Long = 0L,
    private val dataSource: SchoolScheduleDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DayDetailsViewModel::class.java)) {
            return DayDetailsViewModel(timeslot_id, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}