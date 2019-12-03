package com.meranged.schoolschedule.ui.teacherdetails

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController

import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.database.Teacher
import com.meranged.schoolschedule.databinding.TeacherDetailsFragmentBinding
import com.meranged.schoolschedule.databinding.TeacherDetailsNewFragmentBinding
import kotlinx.coroutines.*


class TeacherDetailsNewFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<TeacherDetailsNewFragmentBinding>(
            inflater,
            R.layout.teacher_details_new_fragment, container, false)


        binding.setLifecycleOwner(this)



        var viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        binding.saveButton.setOnClickListener{

            if (binding.name1EditText.text.isNotEmpty()
            or binding.name2EditText.text.isNotEmpty()
            or binding.name3EditText.text.isNotEmpty()){
                var teacher = Teacher()
                teacher.firstName = binding.name1EditText.text.toString()
                teacher.secondName = binding.name2EditText.text.toString()
                teacher.thirdName = binding.name3EditText.text.toString()

                uiScope.launch {
                    insertTeacher(teacher)
                }
                view!!.findNavController()
                    .navigate(TeacherDetailsNewFragmentDirections
                        .actionTeacherDetailsNewFragmentToNavigationMyTeachers())
            }
        }


        return binding.root
    }

    suspend fun insertTeacher(teacher: Teacher){
        val application = requireNotNull(this.activity).application
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao

        withContext(Dispatchers.IO) {
            dataSource.insert(teacher)
        }
    }

}
