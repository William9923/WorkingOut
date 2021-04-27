package com.softhouse.workingout.ui.log.list

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentDetailCyclingBinding
import com.softhouse.workingout.databinding.FragmentTwoPaneRunningLogsBinding
import com.softhouse.workingout.ui.log.DetailRunningFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TwoPaneRunningLogsFragment : Fragment(), RunningLogsRecyclerViewAdapter.OnRunningRecordClickListener {

    private val viewModel: RunningLogsViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )
    private var columnCount = 1

    lateinit var binding: FragmentTwoPaneRunningLogsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTwoPaneRunningLogsBinding.inflate(inflater, container, false)
        binding.slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED
        initRecyclerViewAdapter(binding.listPane)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        return binding.root
    }

    private fun initRecyclerViewAdapter(view: View) {
        // Set the adapter
        if (view is RecyclerView) {
            view.layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            view.adapter = viewModel.records.value?.let { RunningLogsRecyclerViewAdapter(it, this) }
        }

        // Observe data change
        viewModel.records.observe(viewLifecycleOwner, {
            Log.d("Data", "Change before case")
            if (view is RecyclerView) {
                view.adapter = viewModel.records.value?.let { RunningLogsRecyclerViewAdapter(it, this) }
                view.adapter?.notifyDataSetChanged()
            }
        })
    }

    override fun onRecordClick(position: Int) {
        Log.d("Detect", "Item in $position clicked")
        val id: Long = viewModel.records.value!![position].id!!
        viewModel.initSpecificData(id)
        val newFragment = DetailRunningFragment()
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.detail_container,
                newFragment
            )
            if (binding.slidingPaneLayout.isOpen) {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }
        }
        binding.slidingPaneLayout.open()
    }

    companion object
}