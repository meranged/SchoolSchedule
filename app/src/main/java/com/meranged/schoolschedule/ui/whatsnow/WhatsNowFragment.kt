package com.meranged.schoolschedule.ui.whatsnow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.meranged.schoolschedule.database.SchoolScheduleDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import com.meranged.schoolschedule.R
import com.meranged.schoolschedule.databinding.WhatsNowFragmentBinding


class WhatsNowFragment : Fragment() {

    companion object {
        fun newInstance() = WhatsNowFragment()
    }

    private lateinit var whatsNowViewModel: WhatsNowViewModel
    lateinit var l_imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = DataBindingUtil.inflate<WhatsNowFragmentBinding>(
            inflater,
            R.layout.whats_now_fragment, container, false)

        val application = requireNotNull(this.activity).application

        // Create an instance of the ViewModel Factory.
        val dataSource = SchoolScheduleDatabase.getInstance(application).dao
        val viewModelFactory = WhatsNowViewModelFactory(dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        whatsNowViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(WhatsNowViewModel::class.java)


        binding.setLifecycleOwner(this)

        var viewModelJob = Job()

        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.whatsNowViewModel = whatsNowViewModel

        return binding.root
    }

}