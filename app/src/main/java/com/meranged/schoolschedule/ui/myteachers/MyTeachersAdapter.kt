package com.meranged.schoolschedule.ui.myteachers

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.meranged.schoolschedule.database.Teacher
import com.meranged.schoolschedule.database.TeacherWithSubjects
import com.meranged.schoolschedule.databinding.ListItemTeacherBinding

class  MyTeachersAdapter(val clickListener: MyTeachersListener): ListAdapter<TeacherWithSubjects, MyTeachersAdapter.ViewHolder>(MyTeachersDiffCallback()) {

    class ViewHolder private constructor (val binding: ListItemTeacherBinding) : RecyclerView.ViewHolder(binding.root){

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ListItemTeacherBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(item: TeacherWithSubjects, clickListener: MyTeachersListener) {

            binding.teacher = item.teacher

            binding.teacherFIO.text = item.teacher.firstName + " " + item.teacher.secondName + " " + item.teacher.thirdName
            binding.teacherSubjectsList.text = ""

            if (item.teacher.photo_path.isNotEmpty()){
                binding.teacherImageView.setImageURI(Uri.parse(item.teacher.photo_path))
            }

            var l_subjs = ""

            for(subj in item.subjects){
                l_subjs = l_subjs + subj.name + ", "
            }

            if (l_subjs.length > 2){
                l_subjs = l_subjs.substring(0, l_subjs.length-2)
                binding.teacherSubjectsList.visibility = View.VISIBLE
            } else {
                binding.teacherSubjectsList.visibility = View.INVISIBLE
            }

            binding.teacherSubjectsList.text = l_subjs

            binding.clickListener = clickListener
        }

        var subjects = binding.teacherSubjectsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(getItem(position)!!, clickListener)
    }
}

class MyTeachersDiffCallback : DiffUtil.ItemCallback<TeacherWithSubjects>() {
    override fun areItemsTheSame(oldItem: TeacherWithSubjects, newItem: TeacherWithSubjects): Boolean {
        return oldItem.teacher.teacherId == newItem.teacher.teacherId
    }

    override fun areContentsTheSame(oldItem: TeacherWithSubjects, newItem: TeacherWithSubjects): Boolean {
        return ((oldItem.teacher.firstName != newItem.teacher.firstName)
                or (oldItem.teacher.secondName != newItem.teacher.secondName)
                or (oldItem.teacher.thirdName != newItem.teacher.thirdName)
                or (oldItem.teacher.nickName != newItem.teacher.nickName))
    }
}

class MyTeachersListener(val clickListener: (ts: Teacher) -> Unit) {

    fun onClick(teacher: Teacher) = clickListener(teacher)

}

