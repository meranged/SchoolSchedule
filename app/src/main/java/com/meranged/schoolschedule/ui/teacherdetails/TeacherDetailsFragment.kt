package com.meranged.schoolschedule.ui.teacherdetails

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.databinding.CallsScheduleFragmentBinding
import com.meranged.schoolschedule.databinding.TeacherDetailsFragmentBinding

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

        binding.

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TeacherDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
