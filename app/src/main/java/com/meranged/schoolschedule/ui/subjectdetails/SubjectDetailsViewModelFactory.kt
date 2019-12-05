package com.meranged.schoolschedule.ui.subjectdetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meranged.schoolschedule.database.SchoolScheduleDao
import com.meranged.schoolschedule.ui.myteachers.MyTeachersViewModel

class SubjectDetailsViewModelFactory(
    private val subject_id: Long = 0L,
    private val dataSource: SchoolScheduleDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubjectDetailsViewModel::class.java)) {
            return SubjectDetailsViewModel(subject_id, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}