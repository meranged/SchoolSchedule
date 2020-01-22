package com.meranged.schoolschedule.ui.myteachers

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meranged.schoolschedule.database.Teacher
import com.meranged.schoolschedule.databinding.ListItemTeacherBinding
import com.meranged.schoolschedule.databinding.ListItemTimeSlotBinding
import java.io.ByteArrayInputStream

class  MyTeachersAdapter(val clickListener: MyTeachersListener): ListAdapter<Teacher, MyTeachersAdapter.ViewHolder>(MyTeachersDiffCallback()) {

    class ViewHolder private constructor (val binding: ListItemTeacherBinding) : RecyclerView.ViewHolder(binding.root){

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ListItemTeacherBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: Teacher, clickListener: MyTeachersListener) {

            binding.teacher = item

            val res = binding.root.context.resources

            binding.teacherFIO.text = item.firstName + " " + item.secondName + " " + item.thirdName
            binding.teacherSubjectsList.text = ""

            if (item.photo != null) {
                val arrayInputStream = ByteArrayInputStream(item.photo)
                binding.teacherImageView.setImageBitmap(BitmapFactory.decodeStream(arrayInputStream))
            }

            binding.clickListener = clickListener
        }

        var fio = binding.teacherFIO
        var subjects = binding.teacherSubjectsList
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

class MyTeachersDiffCallback : DiffUtil.ItemCallback<Teacher>() {
    override fun areItemsTheSame(oldItem: Teacher, newItem: Teacher): Boolean {
        return oldItem.teacherId == newItem.teacherId
    }

    override fun areContentsTheSame(oldItem: Teacher, newItem: Teacher): Boolean {
        return ((oldItem.firstName != newItem.firstName)
                or (oldItem.secondName != newItem.secondName)
                or (oldItem.thirdName != newItem.thirdName)
                or (oldItem.nickName != newItem.nickName))

    }
}

class MyTeachersListener(val clickListener: (ts: Teacher) -> Unit) {

    fun onClick(teacher: Teacher) = clickListener(teacher)

}

