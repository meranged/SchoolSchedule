package com.meranged.schoolschedule.ui.subjectdetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meranged.schoolschedule.database.SchoolScheduleDao

class SubjectDetailsNewViewModelFactory(
    private val dataSource: SchoolScheduleDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubjectDetailsNewViewModel::class.java)) {
            return SubjectDetailsNewViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}