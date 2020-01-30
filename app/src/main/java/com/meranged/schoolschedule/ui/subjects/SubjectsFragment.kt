package com.meranged.schoolschedule.ui.subjects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.ScrollAwareFABBehavior
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import com.meranged.schoolschedule.database.SubjectWithTeacher
import com.meranged.schoolschedule.databinding.SubjectsFragmentBinding

class SubjectsFragment : Fragment() {

    companion object {
        fun newInstance() = SubjectsFragment()
    }

    private lateinit var viewModel: SubjectsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<SubjectsFragmentBinding>(
            inflater,
            R.layout.subjects_fragment, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = SubjectsViewModelFactory(dataSource, application)
        viewModel =
            ViewModelProviders.of(
                this, viewModelFactory
            ).get(SubjectsViewModel::class.java)

        binding.subjectsViewModel = viewModel
        binding.setLifecycleOwner(this)

        val adapter = SubjectsAdapter(SubjectsListener { subject ->

            onClickShowDetails(subject)
        })

        adapter.viewModelOuter = viewModel

        binding.subjectsList.adapter = adapter

        viewModel.subjects_with_teachers_list.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.timeslots_list_gen.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.timeslots_list = it
                adapter.notifyDataSetChanged()
            }
        })

        val manager = LinearLayoutManager(activity)
        binding.subjectsList.layoutManager = manager

        binding.fab.setOnClickListener{
            view!!.findNavController()
                .navigate(
                    SubjectsFragmentDirections
                        .actionNavigationSubjectsToSubjectDetailsNewFragment())
        }

        ScrollAwareFABBehavior(binding.subjectsList, binding.fab).start()

        return binding.root
    }

    fun onClickShowDetails(item: SubjectWithTeacher) {

        view!!.findNavController()
            .navigate(
                SubjectsFragmentDirections
                    .actionNavigationSubjectsToSubjectDetailsFragment(item.subject.subjectId))
    }

}
