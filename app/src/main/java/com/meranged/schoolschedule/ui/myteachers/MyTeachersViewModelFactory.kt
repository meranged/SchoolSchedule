package com.meranged.schoolschedule.ui.myteachers

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meranged.schoolschedule.database.SchoolScheduleDao

class MyTeachersViewModelFactory(
    private val dataSource: SchoolScheduleDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyTeachersViewModel::class.java)) {
            return MyTeachersViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}