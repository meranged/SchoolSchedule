package com.meranged.schoolschedule.ui.lessonsschedule

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.database.TimeSlotWithSubjects
import com.meranged.schoolschedule.databinding.LessonsScheduleFragmentBinding

class LessonsScheduleFragment : Fragment() {

    companion object {
        fun newInstance() = LessonsScheduleFragment()
    }

    private lateinit var viewModel: LessonsScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<LessonsScheduleFragmentBinding>(
            inflater,
            R.layout.lessons_schedule_fragment, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = LessonsScheduleViewModelFactory(dataSource, application)
        viewModel =
            ViewModelProviders.of(
                this, viewModelFactory
            ).get(LessonsScheduleViewModel::class.java)

        binding.lessonsScheduleViewModel = viewModel
        binding.setLifecycleOwner(this)

        val adapter = LessonsScheduleAdapter(LessonsScheduleListener { tsws ->

            onClickShowDetails(tsws)
        })

        binding.lessonsList.adapter = adapter

        viewModel.time_slots_with_subjects.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.teachers_list.observe(viewLifecycleOwner, Observer {
            it?.let {
                LessonsScheduleAdapter.ViewHolder.setTeachersList(it)
            }
        })


        val manager = LinearLayoutManager(activity)
        binding.lessonsList.layoutManager = manager

        return binding.root
    }

    fun onClickShowDetails(item: TimeSlotWithSubjects) {

        view!!.findNavController()
            .navigate(
                LessonsScheduleFragmentDirections
                    .actionNavigationLessonsScheduleToDayDetailsFragment(item.timeSlot.timeslotId))
    }
}
