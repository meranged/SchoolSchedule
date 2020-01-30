package com.meranged.schoolschedule

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProviders
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.ui.callsschedule.CallsScheduleViewModel
import com.meranged.schoolschedule.ui.lessonsschedule.LessonsScheduleViewModel
import com.meranged.schoolschedule.ui.myteachers.MyTeachersViewModel
import com.meranged.schoolschedule.ui.subjects.SubjectsViewModel
import com.meranged.schoolschedule.ui.subjects.SubjectsViewModelFactory
import com.meranged.schoolschedule.ui.whatsnow.WhatsNowViewModel


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        mContext = this
    }

    companion object {
        private var mContext: Context? = null
        val context: Context?
            get() = mContext
/*
        lateinit var subjectsViewModel: SubjectsViewModel

        lateinit var whatsNowViewModel: WhatsNowViewModel

        lateinit var myTeachersViewModel: MyTeachersViewModel
        lateinit var lessonsScheduleViewModel:LessonsScheduleViewModel
        lateinit var callsScheduleViewModel: CallsScheduleViewModel*/


    }
}
