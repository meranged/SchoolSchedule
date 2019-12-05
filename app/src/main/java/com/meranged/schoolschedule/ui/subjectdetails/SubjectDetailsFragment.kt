package com.meranged.schoolschedule.ui.subjectdetails

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController

import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.databinding.SubjectDetailsFragmentBinding
import com.meranged.schoolschedule.databinding.TeacherDetailsFragmentBinding
import com.meranged.schoolschedule.ui.teacherdetails.TeacherDetailsFragmentArgs
import com.meranged.schoolschedule.ui.teacherdetails.TeacherDetailsFragmentDirections
import com.meranged.schoolschedule.ui.teacherdetails.TeacherDetailsViewModel
import com.meranged.schoolschedule.ui.teacherdetails.TeacherDetailsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SubjectDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = SubjectDetailsFragment()
    }

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

        binding.saveButton.setOnClickListener{
            uiScope.launch {
                subjectDetailsViewModel.updateSubject()
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
        // TODO: Use the ViewModel
    }

}
