package com.meranged.schoolschedule.ui.subjectdetails

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController

import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.database.Subject
import com.meranged.schoolschedule.database.Teacher
import com.meranged.schoolschedule.databinding.SubjectDetailsNewFragmentBinding
import com.meranged.schoolschedule.databinding.TeacherDetailsNewFragmentBinding
import com.meranged.schoolschedule.ui.teacherdetails.TeacherDetailsNewFragmentDirections
import kotlinx.coroutines.*

class SubjectDetailsNewFragment :  Fragment()  {


    var selectednum: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<SubjectDetailsNewFragmentBinding>(
            inflater,
            R.layout.subject_details_new_fragment, container, false)



        binding.setLifecycleOwner(this)



        var viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        binding.saveButton.setOnClickListener{

            if (binding.subjectNameEditText.text.isNotEmpty()){
                var subject = Subject()
                subject.name = binding.subjectNameEditText.text.toString()
                subject.roomNumber = binding.subjectRoomNoEditText.text.toString()
//                subject.teacherId = teachers_list.get(selected_teacher_num).teacherId

                uiScope.launch {
                    insertSubject(subject)
                }
                view!!.findNavController()
                    .navigate(
                        SubjectDetailsNewFragmentDirections
                            .actionSubjectDetailsNewFragmentToNavigationSubjects())
            }
        }

        fillNickNames()


//        val application = requireNotNull(this.activity).application
//
//        var adapter= ArrayAdapter(application,android.R.layout.simple_list_item_1,teachers_dropdown_list)
//        binding.subjectTeacherNameEditText.adapter=adapter
//
//        //LISTENER
//        binding.subjectTeacherNameEditText.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
//                selected_teacher_num = i
//            }
//            override fun onNothingSelected(adapterView: AdapterView<*>) {
//            }
//        }



        return binding.root
    }

    suspend fun insertSubject(subject: Subject){
        val application = requireNotNull(this.activity).application
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao

        withContext(Dispatchers.IO) {
            dataSource.insert(subject)
        }
    }

    fun fillNickNames(): ArrayList<String>{

        val application = requireNotNull(this.activity).application
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao


        val teachers_list = dataSource.getAllTeachersAsList()
        var teachers_dropdown_list = ArrayList<String>()

        var l_teacherNick: String

        for (teacher in teachers_list){
            l_teacherNick = teacher.firstName + " " + teacher.secondName + " " + teacher.thirdName
            teachers_dropdown_list.add(l_teacherNick)
        }

        return teachers_dropdown_list
    }

}