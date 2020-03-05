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
import com.meranged.schoolschedule.databinding.SubjectDetailsFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SubjectDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = SubjectDetailsFragment()
    }

    var teachers_dropdown_list = ArrayList<String>()
    var teachers_ids = ArrayList<Long>()
    var selectednum: Int = 0

    private lateinit var viewModel: SubjectDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<SubjectDetailsFragmentBinding>(
            inflater,
            R.layout.subject_details_fragment, container, false)

        val application = requireNotNull(this.activity).application
        val arguments = SubjectDetailsFragmentArgs.fromBundle(arguments!!)

        // Create an instance of the ViewModel Factory.
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = SubjectDetailsViewModelFactory(arguments!!.subjectId, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val subjectDetailsViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(SubjectDetailsViewModel::class.java)


        binding.setLifecycleOwner(this)

        var viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.subjectDetailsViewModel = subjectDetailsViewModel

        subjectDetailsViewModel.teachers_list.observe(viewLifecycleOwner, Observer {
            it?.let {
                var l_list = ArrayList<String>()
                var id_list = ArrayList<Long>()

                var teacher_id: Long = -1
                var initial_position = -1

                var subj:Subject? = subjectDetailsViewModel.getSubject().value
                if (subj != null){
                    teacher_id = subj.teacherId
                }


                for (t in it){
                    l_list.add(t.firstName + " " + t.secondName + " " + t.thirdName)
                    id_list.add(t.teacherId)

                    if (t.teacherId == teacher_id){
                        initial_position = l_list.lastIndex
                    }

                }
                teachers_dropdown_list = l_list
                teachers_ids = id_list

                var adapter= ArrayAdapter(application,android.R.layout.simple_list_item_1,teachers_dropdown_list)
                binding.subjectTeacherNameEditText.adapter=adapter
                if (initial_position != -1) {
                    binding.subjectTeacherNameEditText.setSelection(initial_position)
                }
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


        binding.saveButton.setOnClickListener{
            uiScope.launch {
                subjectDetailsViewModel.updateSubject(teachers_ids[selectednum])
            }
            view!!.findNavController()
                .navigate(
                    SubjectDetailsFragmentDirections
                        .actionSubjectDetailsFragmentToNavigationSubjects())
        }


        binding.deleteButton.setOnClickListener{
            uiScope.launch {
                subjectDetailsViewModel.deleteSubject()
            }
            view!!.findNavController()
                .navigate(
                    SubjectDetailsFragmentDirections
                        .actionSubjectDetailsFragmentToNavigationSubjects())
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SubjectDetailsViewModel::class.java)
    }

}
