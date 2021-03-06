package com.meranged.schoolschedule.ui.whatsnow

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.convertIntTo00
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.database.Teacher
import com.meranged.schoolschedule.database.TimeSlotWithSubjects
import com.meranged.schoolschedule.databinding.WhatsNowFragmentBinding
import com.meranged.schoolschedule.getRightDayNumber
import java.io.ByteArrayInputStream
import java.util.*
import java.util.Calendar.*


class WhatsNowFragment : Fragment() {

    companion object {
        fun newInstance() = WhatsNowFragment()
    }

    private lateinit var whatsNowViewModel: WhatsNowViewModel
    private lateinit var whatsNowBinding: WhatsNowFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<WhatsNowFragmentBinding>(
            inflater,
            R.layout.whats_now_fragment, container, false
        )

        val application = requireNotNull(this.activity).application

        // Create an instance of the ViewModel Factory.
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = WhatsNowViewModelFactory(dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        whatsNowViewModel =
            ViewModelProviders.of(
                this, viewModelFactory
            ).get(WhatsNowViewModel::class.java)


        binding.setLifecycleOwner(this)
        whatsNowBinding = binding

        whatsNowViewModel.time_slots_with_subjects.observe(this, Observer {
            it?.let {

                val l_list: MutableList<TimeSlotWithSubjects> = ArrayList()

                for (item in it) {
                    if (item.subjects.isNotEmpty()) {
                        l_list.add(item)
                    }
                }

                whatsNowViewModel.lessons_list = l_list
                calculateNextState()
            }
        })

        whatsNowViewModel.teachers_list.observe(this, Observer {
            it?.let {
                whatsNowViewModel.t_list = it
                refreshScreen()
            }
        })

        whatsNowViewModel.needToChangeState.observe(this, Observer {
            it?.let {
                calculateNextState()
            }
        })

        whatsNowViewModel.timeToCallCounter.observe(this, Observer {
            it?.let {
                whatsNowViewModel.timeToCall = it
                refreshScreen()
            }
        })

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.whatsNowViewModel = whatsNowViewModel

        return binding.root
    }

    private fun calculateNextState() {
        val calendar = getCalendar()
        val curHour = calendar.get(HOUR_OF_DAY)
        val curMin = calendar.get(MINUTE)
        val weekDay = getRightDayNumber(calendar.get(DAY_OF_WEEK))

        val l_list = whatsNowViewModel.lessons_list


        if (l_list.isNotEmpty()) {

            val firstLesson = getFirstLessonOfTheDay(weekDay)
            val lastLesson = getLastLessonOfTheDay(weekDay)
            if (firstLesson == null) {
                //we have a free day
                setFreeDay()
            } else if ((curHour < firstLesson.timeSlot.startTimeHours)
                or ((curHour == firstLesson.timeSlot.startTimeHours)
                        and (curMin < firstLesson.timeSlot.startTimeMinutes))
            ) {
                //we have a morning, before 1st lesson
                setMorningBeforeSchool(firstLesson)
            } else if ((curHour > lastLesson!!.timeSlot.finishTimeHours)
                or ((curHour == lastLesson.timeSlot.finishTimeHours)
                        and (curMin > lastLesson.timeSlot.finishTimeMinutes))
            ) {
                //we have an evening, after a last lesson
                setEveningAfterSchool(lastLesson)
            } else {
                val curlesson = getCurrentLessonOfTheDay(weekDay, curHour, curMin)
                if (curlesson != null) {
                    //we have a current lesson
                    if (curlesson.timeSlot.number == lastLesson.timeSlot.number) {
                        setLastLesson(curlesson)
                    } else {
                        setOrdinaryLesson(curlesson)
                    }
                } else {
                    val nextLesson = getNextLesson(weekDay, curHour, curMin)
                    setSchoolBreak(nextLesson!!)
                }
            }
        }
    }

    private fun getFirstLessonOfTheDay(weekDay: Int): TimeSlotWithSubjects? {

        val l_list = whatsNowViewModel.lessons_list

        for (lesson in l_list) {
            if (lesson.timeSlot.weekDay == weekDay)
                return lesson
        }
        return null
    }

    private fun getLastLessonOfTheDay(weekDay: Int): TimeSlotWithSubjects? {

        val l_list = whatsNowViewModel.lessons_list.asReversed()

        for (lesson in l_list) {
            if (lesson.timeSlot.weekDay == weekDay)
                return lesson
        }
        return null
    }

    //It is supposed that we have either lesson or school break
    private fun getCurrentLessonOfTheDay(
        weekDay: Int,
        curHours: Int,
        curMins: Int
    ): TimeSlotWithSubjects? {
        val l_list = whatsNowViewModel.lessons_list

        val curtimeMins = curHours * 60 + curMins

        for (lesson in l_list) {
            if (lesson.timeSlot.weekDay == weekDay) {

                val startMins =
                    lesson.timeSlot.startTimeHours * 60 + lesson.timeSlot.startTimeMinutes
                val finishMins =
                    lesson.timeSlot.finishTimeHours * 60 + lesson.timeSlot.finishTimeMinutes

                if ((curtimeMins >= startMins) and (curtimeMins < finishMins))
                    return lesson
            }
        }
        //since we haven't found a lesson than we have a break
        return null
    }


    private fun getNextLesson(weekDay: Int, curHours: Int, curMins: Int): TimeSlotWithSubjects? {
        val l_list = whatsNowViewModel.lessons_list


        val curtimeMins = curHours * 60 + curMins

        for (lesson in l_list) {
            if (lesson.timeSlot.weekDay == weekDay) {

                val startMins =
                    lesson.timeSlot.startTimeHours * 60 + lesson.timeSlot.startTimeMinutes

                if (curtimeMins < startMins)
                    return lesson
            } else if (lesson.timeSlot.weekDay > weekDay) {
                return lesson
            }
        }
        //return 1st lesson of a week
        return l_list[0]

    }

    private fun getNextLesson(lesson: TimeSlotWithSubjects): TimeSlotWithSubjects {
        val l_list = whatsNowViewModel.lessons_list

        for (l_counter in l_list) {
            if (lesson.timeSlot.weekDay == l_counter.timeSlot.weekDay) {

                if (l_counter.timeSlot.number > lesson.timeSlot.number) {
                    return l_counter
                }
            } else if (l_counter.timeSlot.weekDay > lesson.timeSlot.weekDay) {
                return l_counter
            }
        }

        return l_list[0]

    }

    private fun setFreeDay() {
        val calendar = getCalendar()
        whatsNowViewModel.now_status = whatsNowViewModel.FREE_TIME
        whatsNowViewModel.currentLesson = null
        val nextLesson = getNextLesson(
            getRightDayNumber(calendar.get(DAY_OF_WEEK)), calendar.get(
                HOUR_OF_DAY
            ), calendar.get(MINUTE)
        )
        whatsNowViewModel.nextLesson = nextLesson

        if (nextLesson != null) {
            whatsNowViewModel.setTimer(
                getDiffInMillis(
                    nextLesson.timeSlot.weekDay,
                    nextLesson.timeSlot.startTimeHours,
                    nextLesson.timeSlot.startTimeMinutes
                )
            )
        }
        refreshScreen()

    }

    private fun setMorningBeforeSchool(firstLesson: TimeSlotWithSubjects) {

        whatsNowViewModel.now_status = whatsNowViewModel.MORNING_BEFORE_SCHOOL
        whatsNowViewModel.currentLesson = null
        whatsNowViewModel.nextLesson = firstLesson

        whatsNowViewModel.setTimer(
            getDiffInMillis(
                firstLesson.timeSlot.weekDay,
                firstLesson.timeSlot.startTimeHours,
                firstLesson.timeSlot.startTimeMinutes
            )
        )
        refreshScreen()

    }

    private fun setEveningAfterSchool(lastLesson: TimeSlotWithSubjects) {


        whatsNowViewModel.now_status = whatsNowViewModel.EVENING_AFTER_SCHOOL
        whatsNowViewModel.currentLesson = null
        val nextLesson = getNextLesson(lastLesson)
        whatsNowViewModel.nextLesson = nextLesson

        whatsNowViewModel.setTimer(
            getDiffInMillis(
                nextLesson.timeSlot.weekDay,
                nextLesson.timeSlot.startTimeHours,
                nextLesson.timeSlot.startTimeMinutes
            )
        )

        refreshScreen()

        //запусить таймер на разницу между текущим временем и первым уроком следующего дня или первым уроком недели
    }

    private fun setOrdinaryLesson(lesson: TimeSlotWithSubjects) {

        whatsNowViewModel.now_status = whatsNowViewModel.ORDINARY_LESSON
        whatsNowViewModel.currentLesson = lesson
        val nextLesson = getNextLesson(lesson)
        whatsNowViewModel.nextLesson = nextLesson

        whatsNowViewModel.setTimer(
            getDiffInMillis(
                lesson.timeSlot.weekDay,
                lesson.timeSlot.finishTimeHours,
                lesson.timeSlot.finishTimeMinutes
            )
        )
        refreshScreen()

        //запусить таймер на разницу между текущим временем и окончанием текущего урока
    }

    private fun setLastLesson(lesson: TimeSlotWithSubjects) {

        if (whatsNowViewModel.now_status != whatsNowViewModel.LAST_LESSON) {

            whatsNowViewModel.now_status = whatsNowViewModel.LAST_LESSON
            whatsNowViewModel.currentLesson = lesson
            val nextLesson = getNextLesson(lesson)
            whatsNowViewModel.nextLesson = nextLesson

            whatsNowViewModel.setTimer(
                getDiffInMillis(
                    lesson.timeSlot.weekDay,
                    lesson.timeSlot.finishTimeHours,
                    lesson.timeSlot.finishTimeMinutes
                )
            )

        }
        refreshScreen()
        //запустить таймер на разницу между текущим временем и окончанием урока
    }

    private fun setSchoolBreak(nextLesson: TimeSlotWithSubjects) {

        whatsNowViewModel.now_status = whatsNowViewModel.SCHOOL_BREAK
        whatsNowViewModel.currentLesson = null
        whatsNowViewModel.nextLesson = nextLesson

        whatsNowViewModel.setTimer(
            getDiffInMillis(
                nextLesson.timeSlot.weekDay,
                nextLesson.timeSlot.startTimeHours,
                nextLesson.timeSlot.startTimeMinutes
            )
        )

        refreshScreen()
        //запустить таймер на разницу между текущим временем и началом следующего урока
    }

    private fun refreshScreen() {

        Log.i("SS_LOG", "refreshScreen")

        whatsNowBinding.freeTimeCardView.visibility = View.GONE
        whatsNowBinding.freeTimeCardView2.visibility = View.GONE
        whatsNowBinding.subjectCardView.visibility = View.GONE
        whatsNowBinding.subjectCardView2.visibility = View.GONE
        whatsNowBinding.timeToCallCard.visibility = View.GONE
        whatsNowBinding.timeToCallTitleCard.visibility = View.GONE
        whatsNowBinding.whatsNextTitleCard.visibility = View.GONE
        whatsNowBinding.whatsNowTitleCard.visibility = View.GONE
        whatsNowBinding.weekdayNameCard2.visibility = View.GONE

        whatsNowBinding.weekDayName.text = whatsNowBinding.root.context.resources.getStringArray(R.array.weekdays_array)[getRightDayNumber(Calendar.getInstance().get(
            DAY_OF_WEEK))-1]

        when (whatsNowViewModel.now_status) {
            whatsNowViewModel.UNKNOWN -> {
            }
            whatsNowViewModel.MORNING_BEFORE_SCHOOL -> {
                whatsNowBinding.whatsNowTitleCard.visibility = View.VISIBLE
                whatsNowBinding.freeTimeCardView.visibility = View.VISIBLE
                whatsNowBinding.freeTimeTextView.text = getString(R.string.good_morning_dont_be_late)
                whatsNowBinding.timeToCallTitleCard.visibility = View.VISIBLE
                whatsNowBinding.timeToCallCard.visibility = View.VISIBLE
                whatsNowBinding.timeToCallTextView.text = getStringTimeToCall(whatsNowViewModel.timeToCallCounter.value!!)
                whatsNowBinding.whatsNextTitleCard.visibility = View.VISIBLE
                whatsNowBinding.subjectCardView2.visibility = View.VISIBLE
                setSubject2Card()


            }
            whatsNowViewModel.EVENING_AFTER_SCHOOL -> {
                whatsNowBinding.whatsNowTitleCard.visibility = View.VISIBLE
                whatsNowBinding.freeTimeCardView.visibility = View.VISIBLE
                whatsNowBinding.freeTimeTextView.text = getString(R.string.lessons_finished_take_a_rest)
                whatsNowBinding.timeToCallTitleCard.visibility = View.VISIBLE
                whatsNowBinding.timeToCallCard.visibility = View.VISIBLE
                whatsNowBinding.timeToCallTextView.text =
                    getStringTimeToCall(whatsNowViewModel.timeToCall)
                whatsNowBinding.whatsNextTitleCard.visibility = View.VISIBLE
                whatsNowBinding.subjectCardView2.visibility = View.VISIBLE
                setSubject2Card()
                whatsNowBinding.weekdayNameCard2.visibility = View.VISIBLE
                whatsNowBinding.weekDayName2.text = whatsNowBinding.root.context.resources.getStringArray(R.array.weekdays_array)[whatsNowViewModel.nextLesson!!.timeSlot.weekDay-1]

            }
            whatsNowViewModel.FREE_TIME -> {
                whatsNowBinding.whatsNowTitleCard.visibility = View.VISIBLE
                whatsNowBinding.freeTimeCardView.visibility = View.VISIBLE
                whatsNowBinding.freeTimeTextView.text = getString(R.string.today_is_weekend_take_a_rest)
                whatsNowBinding.timeToCallTitleCard.visibility = View.VISIBLE
                whatsNowBinding.timeToCallCard.visibility = View.VISIBLE
                whatsNowBinding.timeToCallTextView.text =
                    getStringTimeToCall(whatsNowViewModel.timeToCall)
                whatsNowBinding.whatsNextTitleCard.visibility = View.VISIBLE
                whatsNowBinding.subjectCardView2.visibility = View.VISIBLE
                setSubject2Card()
                whatsNowBinding.weekdayNameCard2.visibility = View.VISIBLE
                whatsNowBinding.weekDayName2.text = whatsNowBinding.root.context.resources.getStringArray(R.array.weekdays_array)[whatsNowViewModel.nextLesson!!.timeSlot.weekDay-1]
            }
            whatsNowViewModel.ORDINARY_LESSON -> {
                whatsNowBinding.whatsNowTitleCard.visibility = View.VISIBLE
                whatsNowBinding.subjectCardView.visibility = View.VISIBLE
                setSubject1Card()
                whatsNowBinding.timeToCallTitleCard.visibility = View.VISIBLE
                whatsNowBinding.timeToCallCard.visibility = View.VISIBLE
                whatsNowBinding.timeToCallTextView.text =
                    getStringTimeToCall(whatsNowViewModel.timeToCall)
                whatsNowBinding.whatsNextTitleCard.visibility = View.VISIBLE
                whatsNowBinding.freeTimeCardView2.visibility = View.VISIBLE
                whatsNowBinding.freeTimeTextView2.text = getString(R.string.its_a_school_break)

            }
            whatsNowViewModel.LAST_LESSON -> {
                whatsNowBinding.whatsNowTitleCard.visibility = View.VISIBLE
                whatsNowBinding.subjectCardView.visibility = View.VISIBLE
                setSubject1Card()
                whatsNowBinding.timeToCallTitleCard.visibility = View.VISIBLE
                whatsNowBinding.timeToCallCard.visibility = View.VISIBLE
                whatsNowBinding.timeToCallTextView.text =
                    getStringTimeToCall(whatsNowViewModel.timeToCall)
                whatsNowBinding.whatsNextTitleCard.visibility = View.VISIBLE
                whatsNowBinding.freeTimeCardView2.visibility = View.VISIBLE
                whatsNowBinding.freeTimeTextView2.text = getString(R.string.go_home)
            }
            whatsNowViewModel.SCHOOL_BREAK -> {
                whatsNowBinding.whatsNowTitleCard.visibility = View.VISIBLE
                whatsNowBinding.freeTimeCardView.visibility = View.VISIBLE
                whatsNowBinding.freeTimeTextView.text = getString(R.string.its_a_school_break)
                whatsNowBinding.timeToCallCard.visibility = View.VISIBLE
                whatsNowBinding.timeToCallTextView.text =
                    getStringTimeToCall(whatsNowViewModel.timeToCall)
                whatsNowBinding.whatsNextTitleCard.visibility = View.VISIBLE
                whatsNowBinding.subjectCardView2.visibility = View.VISIBLE
                setSubject2Card()
            }
        }
    }

    private fun getStringTimeToCall(timeToCall: Long): String {

        val l_ttc = timeToCall / 1000 / 60

        val hour = l_ttc / 60
        val mins = l_ttc % 60
        var s_timeToCall = ""


        if (hour > 0)
            s_timeToCall = hour.toString() + " " + getRightHours(hour.toInt()) + ", "

        s_timeToCall = s_timeToCall + mins.toString() + " " + getRightMinutes(mins.toInt())

        return s_timeToCall
    }

    private fun getRightHours(hour: Int):String{

        var i = hour
        val teenArray = listOf<Int>(11, 12, 13, 14, 111, 112, 113, 114)

        if (i in teenArray){
            return "часов"
        }

        if (i >9){
            i %= 10
        }

        if (i == 1){
            return "час"
        } else if ((i > 1) and (i <5)){
            return "часа"
        } else {
            return "часов"
        }

    }

    private fun getRightMinutes(mins: Int):String{

        var i = mins
        val teenArray = listOf<Int>(11, 12, 13, 14)

        if (i in teenArray){
            return "минут"
        }

        if (i >9){
            i %= 10
        }

        if (i == 1){
            return "минута"
        } else if ((i > 1) and (i <5)){
            return "минуты"
        } else {
            return "минут"
        }

    }

    private fun setSubject2Card() {

        val lesson = whatsNowViewModel.nextLesson!!

        whatsNowBinding.subjectName2.text = lesson.subjects[0].name
        whatsNowBinding.roomNumber2.text = lesson.subjects[0].roomNumber
        val teacher =
            whatsNowViewModel.getTeacherOfSubject(lesson.subjects[0])

        whatsNowBinding.lessonStart2.text = "${convertIntTo00(lesson.timeSlot.startTimeHours)}:${convertIntTo00(lesson.timeSlot.startTimeMinutes)}"
        whatsNowBinding.lessonFinish2.text = "${convertIntTo00(lesson.timeSlot.finishTimeHours)}:${convertIntTo00(lesson.timeSlot.finishTimeMinutes)}"

        whatsNowBinding.lessonNumber2.text = lesson.timeSlot.number.toString()

        if (lesson.timeSlot.comment.isNotEmpty()){
            whatsNowBinding.freeTimeTextView2.text = "Что было задано: " + lesson.timeSlot.comment
            whatsNowBinding.freeTimeCardView2.visibility = View.VISIBLE
        }


        if (teacher == null) {
            whatsNowBinding.teacherName2.visibility = View.INVISIBLE
            whatsNowBinding.teacherImageView2.visibility = View.INVISIBLE
            Log.i("SS_LOG", "setSubject2Card, teacher = null")
        } else {
            Log.i("SS_LOG", "setSubject2Card, teacher = " + teacher.thirdName)
            whatsNowBinding.teacherName2.text =
                getStringTeacherFIO(teacher)
            whatsNowBinding.teacherName2.visibility = View.VISIBLE

            if (ContextCompat.checkSelfPermission(activity!!.applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

                if (teacher.photo_path.isNotEmpty()) {

                    whatsNowBinding.teacherImageView2.setImageURI(Uri.parse(teacher.photo_path))

                } else {
                    whatsNowBinding.teacherImageView2.setImageResource(R.drawable.ic_face_black_24dp)
                }
            }
            whatsNowBinding.teacherImageView2.visibility = View.VISIBLE
        }
    }

    private fun setSubject1Card() {

        val lesson = whatsNowViewModel.currentLesson!!

        whatsNowBinding.subjectName.text = lesson.subjects[0].name
        whatsNowBinding.roomNumber.text = lesson.subjects[0].roomNumber
        whatsNowBinding.lessonNumber.text = lesson.timeSlot.number.toString()
        whatsNowBinding.lessonStart.text = "${convertIntTo00(lesson.timeSlot.startTimeHours)}:${convertIntTo00(lesson.timeSlot.startTimeMinutes)}"
        whatsNowBinding.lessonFinish.text = "${convertIntTo00(lesson.timeSlot.finishTimeHours)}:${convertIntTo00(lesson.timeSlot.finishTimeMinutes)}"

        val teacher =
            whatsNowViewModel.getTeacherOfSubject(lesson.subjects[0])

        if (teacher == null) {
            whatsNowBinding.teacherName.visibility = View.INVISIBLE
            whatsNowBinding.teacherImageView.visibility = View.INVISIBLE
            Log.i("SS_LOG", "setSubject1Card, teacher = null")
        } else {
            Log.i("SS_LOG", "setSubject1Card, teacher = " + teacher.thirdName)
            whatsNowBinding.teacherName.text =
                getStringTeacherFIO(teacher)
            whatsNowBinding.teacherName.visibility = View.VISIBLE

            if (ContextCompat.checkSelfPermission(activity!!.applicationContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

                if (teacher.photo_path.isNotEmpty()){

                    whatsNowBinding.teacherImageView.setImageURI(Uri.parse(teacher.photo_path))

                } else {
                    whatsNowBinding.teacherImageView.setImageResource(R.drawable.ic_face_black_24dp)
                }


            }

            whatsNowBinding.teacherImageView.visibility = View.VISIBLE

        }
    }

    private fun getStringTeacherFIO(teacher: Teacher?): String {
        if (teacher == null) {
            return ""
        } else {
            return teacher.firstName + " " + teacher.secondName
        }
    }

    private fun getDiffInMillis(weekDay: Int, hour: Int, mins: Int): Long {
        val calendar = getCalendar()
        val now_time_mins = calendar.get(HOUR_OF_DAY) * 60 + calendar.get(MINUTE)
        val call_time_mins = hour * 60 + mins
        val curWeekDay = getRightDayNumber(calendar.get(DAY_OF_WEEK))

        var timerValue = ((call_time_mins - now_time_mins) * 60000).toLong()

        if (weekDay > curWeekDay) {
            timerValue = timerValue + 24 * 60 * 60000 * (weekDay - curWeekDay)
        } else if (weekDay < curWeekDay) {
            timerValue = timerValue + 24 * 60 * 60000 * (7 - curWeekDay + weekDay)
        } else if (call_time_mins < now_time_mins ){
            timerValue = timerValue + 24 * 60 * 60000 * 7
        }

        return timerValue


    }

    private fun getCalendar(): Calendar {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY

        return cal
    }
}