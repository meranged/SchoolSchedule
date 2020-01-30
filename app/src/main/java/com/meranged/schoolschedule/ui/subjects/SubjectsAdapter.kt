package com.meranged.schoolschedule.ui.subjects

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meranged.schoolschedule.App
import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SubjectWithTeacher
import com.meranged.schoolschedule.databinding.ListItemSubjectBinding
import com.meranged.schoolschedule.databinding.ListItemTimeSlotBinding
import java.io.ByteArrayInputStream

class  SubjectsAdapter(val clickListener: SubjectsListener): ListAdapter<SubjectWithTeacher, SubjectsAdapter.ViewHolder>(SubjectsDiffCallback()) {

    lateinit var viewModelOuter:SubjectsViewModel

    class ViewHolder private constructor (val binding: ListItemSubjectBinding) : RecyclerView.ViewHolder(binding.root){

        lateinit var viewModelInner:SubjectsViewModel

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ListItemSubjectBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: SubjectWithTeacher, clickListener: SubjectsListener) {

            binding.subject = item

            var s_name: String = ""

            if (!item.teachers.isEmpty()){
                s_name = item.teachers[0].firstName + " " + item.teachers[0].secondName + " " + item.teachers[0].thirdName

                if (item.teachers[0].photo != null) {
                    val arrayInputStream = ByteArrayInputStream(item.teachers[0].photo)
                    binding.teacherImageView.setImageBitmap(BitmapFactory.decodeStream(arrayInputStream))
                }

            }

            val res = binding.root.context.resources

            binding.subjectName.text = item.subject.name
            binding.roomNumber.text = item.subject.roomNumber
            binding.teacherName.text = s_name
            binding.weekDaysListTextView.text = viewModelInner.getSubjectWeekDays(item.subject.subjectId)

            binding.clickListener = clickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewModelInner = viewModelOuter
        holder.bind(getItem(position)!!, clickListener)

    }

}

class SubjectsDiffCallback : DiffUtil.ItemCallback<SubjectWithTeacher>() {
    override fun areItemsTheSame(oldItem: SubjectWithTeacher, newItem: SubjectWithTeacher): Boolean {
        return oldItem.subject.subjectId == newItem.subject.subjectId
    }

    override fun areContentsTheSame(oldItem: SubjectWithTeacher, newItem: SubjectWithTeacher): Boolean {
        return ((oldItem.subject.name != newItem.subject.name)
                or (oldItem.subject.roomNumber != newItem.subject.roomNumber)
                or (oldItem.subject.teacherId != newItem.subject.teacherId))

    }
}

class SubjectsListener(val clickListener: (ts: SubjectWithTeacher) -> Unit) {

    fun onClick(subject: SubjectWithTeacher) = clickListener(subject)

}