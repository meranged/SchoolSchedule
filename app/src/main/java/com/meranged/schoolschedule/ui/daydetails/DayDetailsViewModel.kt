package com.meranged.schoolschedule.ui.daydetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao


class DayDetailsViewModel(
    val database: SchoolScheduleDao,
    application: Application
) : AndroidViewModel(application) {
}
