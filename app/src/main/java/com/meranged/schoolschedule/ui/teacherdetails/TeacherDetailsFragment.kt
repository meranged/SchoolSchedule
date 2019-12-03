package com.meranged.schoolschedule.ui.teacherdetails

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.databinding.CallsScheduleFragmentBinding
import com.meranged.schoolschedule.databinding.TeacherDetailsFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TeacherDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = TeacherDetailsFragment()
    }

    private lateinit var viewModel: TeacherDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<TeacherDetailsFragmentBinding>(
            inflater,
            R.layout.teacher_details_fragment, container, false)

        val application = requireNotNull(this.activity).application
        val arguments = TeacherDetailsFragmentArgs.fromBundle(arguments!!)

        // Create an instance of the ViewModel Factory.
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = TeacherDetailsViewModelFactory(arguments!!.teacherId, dataSource, application)

        // Get a reference to the ViewModel associated with this fragment.
        val teacherDetailsViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(TeacherDetailsViewModel::class.java)


        binding.setLifecycleOwner(this)

        var viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.teacherDetailsViewModel = teacherDetailsViewModel

        binding.saveButton.setOnClickListener{
            uiScope.launch {
                teacherDetailsViewModel.updateTeacher()
            }
            view!!.findNavController()
                .navigate(TeacherDetailsFragmentDirections
                    .actionTeacherDetailsFragmentToNavigationMyTeachers())
        }


        binding.deleteButton.setOnClickListener{
            uiScope.launch {
                teacherDetailsViewModel.deleteTeacher()
            }
            view!!.findNavController()
                .navigate(TeacherDetailsFragmentDirections
                    .actionTeacherDetailsFragmentToNavigationMyTeachers())
        }



        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TeacherDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
