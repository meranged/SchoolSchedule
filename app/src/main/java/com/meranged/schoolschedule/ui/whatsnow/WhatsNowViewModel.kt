package com.meranged.schoolschedule.ui.whatsnow

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meranged.schoolschedule.database.SchoolScheduleDao
import com.meranged.schoolschedule.database.Subject
import com.meranged.schoolschedule.database.Teacher
import com.meranged.schoolschedule.database.TimeSlotWithSubjects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.sql.Time
import java.util.*


class WhatsNowViewModel(
    dataSource: SchoolScheduleDao
) : ViewModel() {

    /**
     * Hold a reference to SleepDatabase via its SleepDatabaseDao.
     */
    val db = dataSource
    val UNKNOWN = 0
    val ORDINARY_LESSON = 1
    val LAST_LESSON = 2
    val SCHOOL_BREAK = 3
    val EVENING_AFTER_SCHOOL = 4
    val MORNING_BEFORE_SCHOOL = 5
    val FREE_TIME = 6


    private val viewModelJob = Job()

    val time_slots_with_subjects = db.getTimeSlotWithSubjects()
    var lessons_list = emptyList<TimeSlotWithSubjects>()
    val teachers_list = db.getAllTeachers()
    var t_list = emptyList<Teacher>()
    var now_status = UNKNOWN
    var timeToCallCounter:Long = 0

    var currentLesson:TimeSlotWithSubjects? = null
    var nextLesson:TimeSlotWithSubjects? = null

    private var _needToChangeState = MutableLiveData<Int>()

    val needToChangeState: LiveData<Int>
        get() = _needToChangeState

    private var _now_time = MutableLiveData<Date>()

    val now_time: LiveData<Date>
        get() = _now_time

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        _needToChangeState.value = 0
        _now_time.value = Calendar.getInstance().time
    }

    /** Coroutine setup variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun setTimer(timeToCall: Long){

        timeToCallCounter = timeToCall

        object : CountDownTimer(timeToCall, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeToCallCounter = millisUntilFinished
                _now_time.value = Calendar.getInstance().time
            }

            override fun onFinish() {
                _needToChangeState.value = _needToChangeState.value!! + 1
            }
        }.start()
    }

    fun getTeacherOfSubject(subject: Subject):Teacher?{
        for (teacher in t_list){
            if (subject.teacherId == teacher.teacherId)
                return teacher
        }
        return null
    }
}