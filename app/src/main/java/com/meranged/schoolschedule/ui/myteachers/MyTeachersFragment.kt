package com.meranged.schoolschedule.ui.myteachers

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.meranged.schoolschedule.R

class MyTeachersFragment : Fragment() {

    companion object {
        fun newInstance() = MyTeachersFragment()
    }

    private lateinit var viewModel: MyTeachersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_teachers_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyTeachersViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
