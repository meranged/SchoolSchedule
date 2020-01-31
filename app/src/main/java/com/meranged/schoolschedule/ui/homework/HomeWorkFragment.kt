package com.meranged.schoolschedule.ui.homework

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.databinding.HomeWorkFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeWorkFragment : Fragment() {

    companion object {
        fun newInstance() = HomeWorkFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<HomeWorkFragmentBinding>(
            inflater,
            R.layout.home_work_fragment, container, false)

        val application = requireNotNull(this.activity).application
        val arguments = HomeWorkFragmentArgs.fromBundle(arguments!!)

        // Create an instance of the ViewModel Factory.
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = HomeWorkViewModelFactory(arguments.timeslotId, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val homeWorkViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(HomeWorkViewModel::class.java)


        binding.setLifecycleOwner(this)

        val viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.homeWorkViewModel = homeWorkViewModel


        binding.saveButton.setOnClickListener{
            uiScope.launch {
                homeWorkViewModel.updateTimeSlot()
            }
            view!!.findNavController()
                .navigate(
                    HomeWorkFragmentDirections
                        .actionHomeWorkFragmentToNavigationLessonsSchedule())
        }

        binding.changeSubjectButton.setOnClickListener{
            view!!.findNavController()
                .navigate(
                    HomeWorkFragmentDirections
                        .actionHomeWorkFragmentToDayDetailsFragment(arguments.timeslotId)
                )
        }

        return binding.root
    }

}
