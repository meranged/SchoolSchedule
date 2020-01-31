package com.meranged.schoolschedule.ui.homework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meranged.schoolschedule.database.SchoolScheduleDao

class HomeWorkViewModelFactory(
    private val timeslot_id: Long = 0L,
    private val dataSource: SchoolScheduleDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeWorkViewModel::class.java)) {
            return HomeWorkViewModel(timeslot_id, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}