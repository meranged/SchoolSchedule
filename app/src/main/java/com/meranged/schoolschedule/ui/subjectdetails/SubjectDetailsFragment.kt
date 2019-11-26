package com.meranged.schoolschedule.ui.subjectdetails

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.meranged.schoolschedule.R

class SubjectDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = SubjectDetailsFragment()
    }

    private lateinit var viewModel: SubjectDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.subject_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SubjectDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
