package com.meranged.schoolschedule.ui.callsschedule

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.meranged.schoolschedule.R

class CallsScheduleFragment : Fragment() {

    companion object {
        fun newInstance() = CallsScheduleFragment()
    }

    private lateinit var viewModel: CallsScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.calls_schedule_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CallsScheduleViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
