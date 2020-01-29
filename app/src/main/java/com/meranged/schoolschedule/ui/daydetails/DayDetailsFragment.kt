package com.meranged.schoolschedule.ui.daydetails

import android.os.Bundle
import android.util.Log
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
import com.meranged.schoolschedule.database.TimeSlot
import com.meranged.schoolschedule.databinding.DayDetailsFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DayDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = DayDetailsFragment()
    }

    var subjects_dropdown_list = ArrayList<String>()
    var subjects_ids = ArrayList<Long>()
    var selectednum: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<DayDetailsFragmentBinding>(
            inflater,
            R.layout.day_details_fragment, container, false)

        val application = requireNotNull(this.activity).application
        val arguments = DayDetailsFragmentArgs.fromBundle(arguments!!)

        // Create an instance of the ViewModel Factory.
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = DayDetailsViewModelFactory(arguments!!.timeslotId, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val dayDetailsViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(DayDetailsViewModel::class.java)


        binding.setLifecycleOwner(this)

        val viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.dayDetailsViewModel = dayDetailsViewModel


        dayDetailsViewModel.timeslot.observe(viewLifecycleOwner, Observer {
            Log.i("ttt_TS!!", it.toString())
        })

        dayDetailsViewModel.subjects_list.observe(viewLifecycleOwner, Observer {
            it?.let {
                val l_list = ArrayList<String>()
                val id_list = ArrayList<Long>()

                var subject_id: Long = -1
                var initial_position = -1

                val ts: TimeSlot? = dayDetailsViewModel.timeslot.value
                if (ts != null){
                    subject_id = ts.subject_id
                }

                for (t in it){
                    l_list.add(t.name)
                    id_list.add(t.subjectId)

                    if (t.subjectId == subject_id){
                        initial_position = l_list.lastIndex
                    }

                }
                subjects_dropdown_list = l_list
                subjects_ids = id_list

                val adapter= ArrayAdapter(application,android.R.layout.simple_list_item_1,subjects_dropdown_list)
                binding.subjectName.adapter=adapter
                if (initial_position != -1) {
                    binding.subjectName.setSelection(initial_position)
                }
            }
        })

        //LISTENER
        binding.subjectName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                selectednum = i
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }
        }


        binding.saveButton.setOnClickListener{
            uiScope.launch {
                dayDetailsViewModel.updateTimeSlot(subjects_ids[selectednum])
            }
            view!!.findNavController()
                .navigate(
                    DayDetailsFragmentDirections
                        .actionDayDetailsFragmentToNavigationLessonsSchedule())
        }

        return binding.root
    }

}
