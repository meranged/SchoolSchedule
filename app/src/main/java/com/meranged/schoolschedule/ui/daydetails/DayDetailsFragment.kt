package com.meranged.schoolschedule.ui.daydetails

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.database.SchoolScheduleDatabase

class DayDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = DayDetailsFragment()
    }

    private lateinit var viewModel: DayDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = DayDetailsViewModelFactory(dataSource, application)
        val dayDetailsViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(DayDetailsViewModel::class.java)

        return inflater.inflate(R.layout.day_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DayDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
