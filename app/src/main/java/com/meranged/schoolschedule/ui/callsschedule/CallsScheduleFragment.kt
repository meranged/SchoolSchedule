package com.meranged.schoolschedule.ui.callsschedule

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.database.TimeSlot
import com.meranged.schoolschedule.databinding.CallsScheduleFragmentBinding
import com.meranged.schoolschedule.ui.daydetails.DayDetailsViewModel
import com.meranged.schoolschedule.ui.daydetails.DayDetailsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class CallsScheduleFragment : Fragment() {

    companion object {
        fun newInstance() = CallsScheduleFragment()
    }

    private lateinit var viewModel: CallsScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<CallsScheduleFragmentBinding>(
            inflater,
            R.layout.calls_schedule_fragment, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = CallsScheduleViewModelFactory(dataSource, application)
        viewModel =
            ViewModelProviders.of(
                this, viewModelFactory
            ).get(CallsScheduleViewModel::class.java)

        binding.callsScheduleViewModel = viewModel
        binding.setLifecycleOwner(this)

        val adapter = CallsScheduleAdapter(CallsScheduleListener { timeslot, ftype ->
            onClickShowTimePicker(timeslot, ftype)
        })

        binding.slotsList.adapter = adapter

        viewModel.etalon_slots.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        val manager = GridLayoutManager(activity, 2)
        binding.slotsList.layoutManager = manager

        return binding.root
    }

    fun onClickShowTimePicker(item: TimeSlot, fieldtype: Int) {


        var hour: Int
        var min: Int

        if (fieldtype == 1) {
            hour = item.startTimeHours
            min = item.startTimeMinutes
        } else {
            hour = item.finishTimeHours
            min = item.finishTimeMinutes
        }


        val listener =
            TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hour: Int, min: Int ->
                if (fieldtype == 1) {
                    item.startTimeHours = hour
                    item.startTimeMinutes = min
                } else {
                    item.finishTimeHours = hour
                    item.finishTimeMinutes = min
                }

                uts(item)
            }

        val timePicker = TimePickerDialog(this.context, listener, hour, min, true).show()
    }

    fun uts(item: TimeSlot) {
        val viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        uiScope.launch {
            viewModel.updateWeekTimeSlot(item)
        }
    }

}

