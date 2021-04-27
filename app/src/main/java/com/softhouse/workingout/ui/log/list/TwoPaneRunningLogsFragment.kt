package com.softhouse.workingout.ui.log.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.softhouse.workingout.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TwoPaneRunningLogsFragment : Fragment() {

    private val viewModel: RunningLogsViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_two_pane_running_logs, container, false)
    }

    companion object
}