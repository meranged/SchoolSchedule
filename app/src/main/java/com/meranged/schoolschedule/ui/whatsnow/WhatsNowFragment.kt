package com.meranged.schoolschedule.ui.whatsnow

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.meranged.schoolschedule.R

class WhatsNowFragment : Fragment() {

    companion object {
        fun newInstance() = WhatsNowFragment()
    }

    private lateinit var viewModel: WhatsNowViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.whats_now_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WhatsNowViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
