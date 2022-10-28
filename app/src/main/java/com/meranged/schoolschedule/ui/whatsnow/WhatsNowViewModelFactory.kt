package com.meranged.schoolschedule.ui.whatsnow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meranged.schoolschedule.database.SchoolScheduleDao

class WhatsNowViewModelFactory(
    private val dataSource: SchoolScheduleDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WhatsNowViewModel::class.java)) {
            return WhatsNowViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}