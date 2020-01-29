package com.meranged.schoolschedule.ui.subjectdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.database.Subject
import com.meranged.schoolschedule.databinding.SubjectDetailsNewFragmentBinding
import kotlinx.coroutines.*

class SubjectDetailsNewFragment :  Fragment()  {


    var teachers_dropdown_list = ArrayList<String>()
    var teachers_ids = ArrayList<Long>()
    var selectednum: Int = 0

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

        val application = requireNotNull(this.activity).application
        val arguments = SubjectDetailsFragmentArgs.fromBundle(arguments!!)

        // Create an instance of the ViewModel Factory.
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = SubjectDetailsNewViewModelFactory(dataSource, application)

        // Get a reference to the ViewModel associated with this fragment.
        val subjectDetailsNewViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(SubjectDetailsNewViewModel::class.java)

        binding.setLifecycleOwner(this)

        var viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        binding.saveButton.setOnClickListener{

            if (binding.subjectNameEditText.text.isNotEmpty()){
                var subject = Subject()
                subject.name = binding.subjectNameEditText.text.toString()
                subject.roomNumber = binding.subjectRoomNoEditText.text.toString()
                if (selectednum >0){
                    subject.teacherId = teachers_ids.get(selectednum)
                }

                uiScope.launch {
                    insertSubject(subject)
                }
                view!!.findNavController()
                    .navigate(
                        SubjectDetailsNewFragmentDirections
                            .actionSubjectDetailsNewFragmentToNavigationSubjects())
            }
        }

        subjectDetailsNewViewModel.teachers_list.observe(viewLifecycleOwner, Observer {
            it?.let {
                var l_list = ArrayList<String>()
                var id_list = ArrayList<Long>()
                for (t in it){
                    l_list.add(t.firstName + " " + t.secondName + " " + t.thirdName)
                    id_list.add(t.teacherId)
                }
                teachers_dropdown_list = l_list
                teachers_ids = id_list

                var adapter= ArrayAdapter(application,android.R.layout.simple_list_item_1,teachers_dropdown_list)
                binding.subjectTeacherNameEditText.adapter=adapter
            }
        })



        //LISTENER
        binding.subjectTeacherNameEditText.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                selectednum = i
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }
        }

        return binding.root
    }

    suspend fun insertSubject(subject: Subject){
        val application = requireNotNull(this.activity).application
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao

        withContext(Dispatchers.IO) {
            dataSource.insert(subject)
        }
    }

}