package com.meranged.schoolschedule.ui.lessonsschedule

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.convertIntTo00
import com.meranged.schoolschedule.database.Teacher
import com.meranged.schoolschedule.database.TimeSlotWithSubjects
import com.meranged.schoolschedule.databinding.ListItemTimeSlotWithSubjectAndWeekdayBinding
import java.io.ByteArrayInputStream

class  LessonsScheduleAdapter(val clickListener: LessonsScheduleListener): ListAdapter<TimeSlotWithSubjects, LessonsScheduleAdapter.ViewHolder>(SubjectsDiffCallback()) {


    class ViewHolder private constructor (val binding: ListItemTimeSlotWithSubjectAndWeekdayBinding) : RecyclerView.ViewHolder(binding.root){

        companion object {

            lateinit var t_list: List<Teacher>

            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ListItemTimeSlotWithSubjectAndWeekdayBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }

            fun getTeacherFromList(teacher_id:Long):Teacher?{

                for (teacher in t_list){
                    if (teacher.teacherId == teacher_id)
                        return teacher
                }
                return null
            }

            fun setTeachersList(list: List<Teacher>){
                t_list = list
            }
        }

        fun bind(item: TimeSlotWithSubjects, clickListener: LessonsScheduleListener) {

            val res = binding.root.context.resources
            binding.timeslotwithsubjects = item
            binding.clickListener = clickListener

            binding.teacherName.text = ""

            if (item.timeSlot.number == 1){
                binding.weekDayName.text = res.getStringArray(R.array.weekdays_array)[item.timeSlot.weekDay-1]
                binding.weekdayNameCard.visibility = View.VISIBLE
            } else {
                binding.weekdayNameCard.visibility = View.GONE
            }


            if (!item.subjects.isEmpty()){
                binding.subjectName.text = item.subjects[0].name
                binding.roomNumber.text = item.subjects[0].roomNumber
                binding.subjectName.visibility = View.VISIBLE
                binding.roomNumber.visibility = View.VISIBLE
                binding.teacherImageCard.visibility = View.VISIBLE

                if (item.subjects[0].teacherId > 0){

                    val l_teacher = getTeacherFromList(item.subjects[0].teacherId)
                    if (l_teacher != null) {
                        binding.teacherName.text = l_teacher.thirdName

                        if (l_teacher.photo != null) {
                            val arrayInputStream = ByteArrayInputStream(l_teacher.photo)
                            binding.teacherImageView.setImageBitmap(BitmapFactory.decodeStream(arrayInputStream))
                        }

                    }

                }

            } else {
                binding.subjectName.visibility = View.INVISIBLE
                binding.roomNumber.visibility = View.INVISIBLE
                binding.teacherImageCard.visibility = View.INVISIBLE
            }

            binding.lessonStart.text = "${convertIntTo00(item.timeSlot.startTimeHours)}:${convertIntTo00(item.timeSlot.startTimeMinutes)}"
            binding.lessonFinish.text = "${convertIntTo00(item.timeSlot.finishTimeHours)}:${convertIntTo00(item.timeSlot.finishTimeMinutes)}"

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(getItem(position)!!, clickListener)

    }

}

class SubjectsDiffCallback : DiffUtil.ItemCallback<TimeSlotWithSubjects>() {
    override fun areItemsTheSame(oldItem: TimeSlotWithSubjects, newItem: TimeSlotWithSubjects): Boolean {
        return oldItem.timeSlot.timeslotId == newItem.timeSlot.timeslotId
    }

    override fun areContentsTheSame(oldItem: TimeSlotWithSubjects, newItem: TimeSlotWithSubjects): Boolean {

        if (oldItem.subjects.size != newItem.subjects.size) {
            return true
        } else if (oldItem.subjects.size == 1) {
            return (oldItem.subjects[0].subjectId != newItem.subjects[0].subjectId)
        } else return true
    }
}

class LessonsScheduleListener(val clickListener: (ts: TimeSlotWithSubjects) -> Unit) {

    fun onClick(tsws: TimeSlotWithSubjects) = clickListener(tsws)

}