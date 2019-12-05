package com.meranged.schoolschedule.ui.subjects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meranged.schoolschedule.database.Subject
import com.meranged.schoolschedule.databinding.ListItemSubjectBinding
import com.meranged.schoolschedule.databinding.ListItemTimeSlotBinding

class  SubjectsAdapter(val clickListener: SubjectsListener): ListAdapter<Subject, SubjectsAdapter.ViewHolder>(SubjectsDiffCallback()) {

    class ViewHolder private constructor (val binding: ListItemSubjectBinding) : RecyclerView.ViewHolder(binding.root){

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ListItemSubjectBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: Subject, clickListener: SubjectsListener) {

            binding.subject = item

            val res = binding.root.context.resources

            binding.subjectName.text = item.name
            binding.roomNumber.text = item.roomNumber
            binding.teacherOfSubject.text = item.teacherId.toString()

            binding.clickListener = clickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            ListItemTimeSlotBinding.inflate(layoutInflater, parent, false)
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(getItem(position)!!, clickListener)

    }

}

class SubjectsDiffCallback : DiffUtil.ItemCallback<Subject>() {
    override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
        return oldItem.subjectId == newItem.subjectId
    }

    override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
        return ((oldItem.name != newItem.name)
                or (oldItem.roomNumber != newItem.roomNumber)
                or (oldItem.teacherId != newItem.teacherId))

    }
}

class SubjectsListener(val clickListener: (ts: Subject) -> Unit) {

    fun onClick(subject: Subject) = clickListener(subject)

}