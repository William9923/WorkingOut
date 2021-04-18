package com.softhouse.workingout.ui.home

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.softhouse.workingout.R
import com.softhouse.workingout.shared.addChildFragment
import com.softhouse.workingout.ui.sensor.CompassFragment

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add the child fragment here...
        val fieldFragment = CompassFragment()
        addChildFragment(fieldFragment, R.id.parent_fragment_container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RunningViewModel::class.java)
        // TODO: Use the ViewModel
    }

}