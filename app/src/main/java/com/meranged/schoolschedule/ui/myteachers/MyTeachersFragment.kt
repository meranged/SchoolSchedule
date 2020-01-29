package com.meranged.schoolschedule.ui.myteachers

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
import com.meranged.schoolschedule.database.Teacher
import com.meranged.schoolschedule.databinding.MyTeachersFragmentBinding

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

        viewModel.teachers_with_subjects_list.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        val manager = LinearLayoutManager(activity)
        binding.myTeachersList.layoutManager = manager

        binding.fab.setOnClickListener{
            view!!.findNavController()
                .navigate(
                    MyTeachersFragmentDirections
                        .actionNavigationMyTeachersToTeacherDetailsNewFragment())
        }

        ScrollAwareFABBehavior(binding.myTeachersList, binding.fab).start()

        return binding.root
    }

    fun onClickShowDetails(item: Teacher) {

        view!!.findNavController()
            .navigate(
                MyTeachersFragmentDirections
                    .actionNavigationMyTeachersToTeacherDetailsFragment(item.teacherId))
    }



}