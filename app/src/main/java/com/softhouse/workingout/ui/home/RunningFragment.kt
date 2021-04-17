package com.softhouse.workingout.ui.home

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.softhouse.workingout.R

class RunningFragment : Fragment() {

    companion object {
        fun newInstance() = RunningFragment()
    }

    private lateinit var viewModel: RunningViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_running, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RunningViewModel::class.java)
        // TODO: Use the ViewModel
    }

}