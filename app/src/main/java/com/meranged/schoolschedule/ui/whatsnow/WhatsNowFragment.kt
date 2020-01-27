package com.meranged.schoolschedule.ui.whatsnow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.TimeSlotWithSubjects
import com.meranged.schoolschedule.databinding.WhatsNowFragmentBinding
import java.util.*
import java.util.Calendar.*


class WhatsNowFragment : Fragment() {

    companion object {
        fun newInstance() = WhatsNowFragment()
    }

    private lateinit var whatsNowViewModel: WhatsNowViewModel
    private lateinit var whatsNowBinding:WhatsNowFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<WhatsNowFragmentBinding>(
            inflater,
            R.layout.whats_now_fragment, container, false)

        val application = requireNotNull(this.activity).application

        // Create an instance of the ViewModel Factory.
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = WhatsNowViewModelFactory(dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        whatsNowViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(WhatsNowViewModel::class.java)


        binding.setLifecycleOwner(this)
        whatsNowBinding = binding

        var viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        whatsNowViewModel.time_slots_with_subjects.observe(this, Observer {
            it?.let {

                var l_list: MutableList<TimeSlotWithSubjects> = ArrayList()

                for (item in whatsNowViewModel.lessons_list){
                    if (item.subjects.isNotEmpty()){
                        l_list.add(item)
                    }
                }

                whatsNowViewModel.lessons_list = l_list
            }
        })

        whatsNowViewModel.teachers_list.observe(this, Observer {
            it?.let {
                whatsNowViewModel.t_list = it
            }
        })

        whatsNowViewModel.needToChangeState.observe(this, Observer {
            it?.let {
                calculateNextState()
            }
        })

        whatsNowViewModel.now_time.observe(this, Observer {
            it?.let {
                refreshScreen()
            }
        })

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.whatsNowViewModel = whatsNowViewModel

        return binding.root
    }

    private fun calculateNextState(){
        val calendar = Calendar.getInstance()
        val curHour = calendar.get(HOUR_OF_DAY)
        val curMin = calendar.get(MINUTE)
        val weekDay = calendar.get(DAY_OF_WEEK)

        val l_list = whatsNowViewModel.lessons_list


        if (l_list.isNotEmpty()){

            var firstLesson = getFirstLessonOfTheDay(weekDay)
            var lastLesson = getLastLessonOfTheDay(weekDay)
            if (firstLesson == null){
            //we have a free day
                setFreeDay()
            } else if ((curHour < firstLesson.timeSlot.startTimeHours)
                        or ((curHour == firstLesson.timeSlot.startTimeHours)
                            and (curMin < firstLesson.timeSlot.startTimeMinutes))){
            //we have a morning, before 1st lesson
                setMorningBeforeSchool(firstLesson, calendar)
            } else if ((curHour > firstLesson.timeSlot.startTimeHours)
                or ((curHour == firstLesson.timeSlot.startTimeHours)
                        and (curMin > firstLesson.timeSlot.startTimeMinutes))){
            //we have an evening, after a last lesson
                setEveningAfterSchool()
            } else {
                val curlesson = getCurrentLessonOfTheDay(weekDay, curHour, curMin)
                if (curlesson != null){
            //we have a current lesson
                    if (curlesson.timeSlot.number == lastLesson!!.timeSlot.number){
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

        for(lesson in l_list){

        }
    }

    private fun getFirstLessonOfTheDay(weekDay:Int):TimeSlotWithSubjects?{

        val l_list = whatsNowViewModel.lessons_list

        for(lesson in l_list){
            if (lesson.timeSlot.weekDay == weekDay)
                return lesson
        }
        return null
    }

    private fun getLastLessonOfTheDay(weekDay:Int):TimeSlotWithSubjects?{

        val l_list = whatsNowViewModel.lessons_list.asReversed()

        for(lesson in l_list){
            if (lesson.timeSlot.weekDay == weekDay)
                return lesson
        }
        return null
    }

    //It is supposed that we have either lesson or school break
    private fun getCurrentLessonOfTheDay(weekDay: Int, curHours: Int, curMins:Int):TimeSlotWithSubjects?{
        val l_list = whatsNowViewModel.lessons_list

        val curtimeMins = curHours*60 + curMins

        for(lesson in l_list){
            if (lesson.timeSlot.weekDay == weekDay){

                val startMins = lesson.timeSlot.startTimeHours*60 + lesson.timeSlot.startTimeMinutes
                val finishMins = lesson.timeSlot.finishTimeHours*60 + lesson.timeSlot.finishTimeMinutes

                if ((curtimeMins >= startMins) and (curtimeMins < finishMins))
                    return lesson
            }
        }
        //since we haven't found a lesson than we have a break
        return null
    }

    private fun getNextLesson(weekDay: Int, curHours: Int, curMins:Int):TimeSlotWithSubjects?{
        val l_list = whatsNowViewModel.lessons_list


        val curtimeMins = curHours*60 + curMins

        for(lesson in l_list){
            if (lesson.timeSlot.weekDay == weekDay){

                val startMins = lesson.timeSlot.startTimeHours*60 + lesson.timeSlot.startTimeMinutes

                if (curtimeMins < startMins)
                    return lesson
            }
        }
        //since we haven't found a lesson than we have a break
        return null

    }

    private fun setFreeDay(){
        whatsNowViewModel.now_status = whatsNowViewModel.FREE_TIME
    }

    private fun setMorningBeforeSchool(firstLesson: TimeSlotWithSubjects, calendar:Calendar){
        whatsNowViewModel.now_status = whatsNowViewModel.MORNING_BEFORE_SCHOOL
        whatsNowViewModel.currentLesson = null
        whatsNowViewModel.nextLesson = firstLesson

        val now_time_mins = calendar.get(HOUR_OF_DAY)*60 + calendar.get(MINUTE)
        val call_time_mins = firstLesson.timeSlot.startTimeHours*60 + firstLesson.timeSlot.startTimeMinutes
        val timerValue = (call_time_mins - now_time_mins)*60000


        whatsNowViewModel.setTimer(timerValue.toLong())
        refreshScreen()

    }

    private fun setEveningAfterSchool(){
        whatsNowViewModel.now_status = whatsNowViewModel.EVENING_AFTER_SCHOOL
    }

    private fun setOrdinaryLesson(lesson:TimeSlotWithSubjects){
        whatsNowViewModel.now_status = whatsNowViewModel.ORDINARY_LESSON
    }

    private fun setLastLesson(lesson:TimeSlotWithSubjects){
        whatsNowViewModel.now_status = whatsNowViewModel.LAST_LESSON
    }

    private fun setSchoolBreak(nextLesson:TimeSlotWithSubjects){
        whatsNowViewModel.now_status = whatsNowViewModel.SCHOOL_BREAK
    }

    private fun refreshScreen(){

        whatsNowBinding.freeTimeCardView.visibility = View.GONE
        whatsNowBinding.freeTimeCardView2.visibility = View.GONE
        whatsNowBinding.subjectCardView.visibility = View.GONE
        whatsNowBinding.subjectCardView2.visibility = View.GONE
        whatsNowBinding.timeToCallCard.visibility = View.GONE
        whatsNowBinding.timeToCallTitleCard.visibility = View.GONE
        whatsNowBinding.whatsNextTitleCard.visibility = View.GONE
        whatsNowBinding.whatsNowTitleCard.visibility = View.GONE


        when (whatsNowViewModel.now_status){
            whatsNowViewModel.UNKNOWN ->{}
            whatsNowViewModel.MORNING_BEFORE_SCHOOL ->{
                whatsNowBinding.whatsNowTitleCard.visibility = View.VISIBLE
                whatsNowBinding.freeTimeCardView.visibility = View.VISIBLE
                whatsNowBinding.freeTimeTextView.text = getStringTimeToCall(whatsNowViewModel.timeToCallCounter)

            }
            whatsNowViewModel.EVENING_AFTER_SCHOOL ->{}
            whatsNowViewModel.FREE_TIME ->{}
            whatsNowViewModel.ORDINARY_LESSON ->{}
            whatsNowViewModel.LAST_LESSON ->{}
            whatsNowViewModel.SCHOOL_BREAK -> {}
        }

    }

    private fun getStringTimeToCall(timeToCall: Long):String{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeToCall
        val hour = calendar.get(HOUR_OF_DAY)
        val mins = calendar.get(MINUTE)
        val secs = calendar.get(SECOND)
        var s_timeToCall:String = ""


        if (hour > 0)
            s_timeToCall = hour.toString() + " часов, "

        s_timeToCall = s_timeToCall + mins.toString() + " минут, " + secs.toString() + " секунд"

        return s_timeToCall
    }

}