package com.meranged.schoolschedule.ui.lessonsschedule

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.meranged.schoolschedule.R

class LessonsShceduleFragment : Fragment() {

    companion object {
        fun newInstance() = LessonsShceduleFragment()
    }

    private lateinit var viewModel: LessonsShceduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.lessons_shcedule_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LessonsShceduleViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
