package com.softhouse.workingout.ui.log

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.softhouse.workingout.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailRunningFragment : Fragment() {

    private val viewModel: DetailRunningViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_running, container, false)
    }

}