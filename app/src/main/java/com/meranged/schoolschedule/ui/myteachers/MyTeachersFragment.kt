package com.meranged.schoolschedule.ui.myteachers

import android.app.TimePickerDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.database.Teacher
import com.meranged.schoolschedule.database.TimeSlot
import com.meranged.schoolschedule.databinding.CallsScheduleFragmentBinding
import com.meranged.schoolschedule.databinding.MyTeachersFragmentBinding
import com.meranged.schoolschedule.ui.callsschedule.CallsScheduleAdapter
import com.meranged.schoolschedule.ui.callsschedule.CallsScheduleListener
import com.meranged.schoolschedule.ui.callsschedule.CallsScheduleViewModel
import com.meranged.schoolschedule.ui.callsschedule.CallsScheduleViewModelFactory
import com.meranged.schoolschedule.ui.teacherdetails.TeacherDetailsFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyTeachersFragment : Fragment() {

    companion object {
        fun newInstance() = MyTeachersFragment()
    }

    private lateinit var viewModel: MyTeachersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<MyTeachersFragmentBinding>(
            inflater,
            R.layout.my_teachers_fragment, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = MyTeachersViewModelFactory(dataSource, application)
        viewModel =
            ViewModelProviders.of(
                this, viewModelFactory
            ).get(MyTeachersViewModel::class.java)

        binding.myTeachersViewModel = viewModel
        binding.setLifecycleOwner(this)

        val adapter = MyTeachersAdapter(MyTeachersListener { teacher ->

            onClickShowDetails(teacher)
        })

        binding.myTeachersList.adapter = adapter

        viewModel.teachers_list.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        val manager = LinearLayoutManager(activity)
        binding.myTeachersList.layoutManager = manager

        return binding.root
    }

    fun onClickShowDetails(item: Teacher) {

        view!!.findNavController()
            .navigate(
                MyTeachersFragmentDirections
                    .actionNavigationMyTeachersToTeacherDetailsFragment(item.teacherId))
    }

}