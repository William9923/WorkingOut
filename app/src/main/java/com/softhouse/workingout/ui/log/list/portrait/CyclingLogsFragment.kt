package com.softhouse.workingout.ui.log.list.portrait

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.softhouse.workingout.R
import com.softhouse.workingout.ui.log.list.CyclingLogsViewModel
import com.softhouse.workingout.ui.log.list.RunningLogsViewModel
import com.softhouse.workingout.ui.log.list.portrait.dummy.DummyContent
import dagger.hilt.android.AndroidEntryPoint

/**
 * A fragment representing a list of Items.
 */
@AndroidEntryPoint
class CyclingLogsFragment : Fragment(), CyclingLogsRecyclerViewAdapter.OnCyclingRecordClickListener {

    private var columnCount = 1
    private val args: CyclingLogsFragmentArgs by navArgs()
    private val viewModel: CyclingLogsViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initDate(args.day, args.month, args.year)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cycling_logs_list, container, false)
        // Set the adapter
        initRecyclerViewAdapter(view)

        // TODO : Change biar bisa ganti orientation
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        return view
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (view is RecyclerView) {
            // TODO : Navigate to the landscape fragment
        }
    }

    private fun initRecyclerViewAdapter(view: View) {
        // Set the adapter
        if (view is RecyclerView) {
            view.layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            view.adapter = viewModel.records.value?.let { CyclingLogsRecyclerViewAdapter(it, this) }
        }

        // Observe data change
        viewModel.records.observe(viewLifecycleOwner, {
            Log.d("Data", "Change before case")
            if (view is RecyclerView) {
                Log.d("Data", "Changed")
                if (view.adapter != null)
                    view.adapter = viewModel.records.value?.let { CyclingLogsRecyclerViewAdapter(it, this) }
                Log.d("Adapter", "notify")
                view.adapter?.notifyDataSetChanged()
            }
        })
    }

    override fun onRecordClick(position: Int) {
        Log.d("Detect", "Item in $position clicked")
        val id: Long = viewModel.records.value!![position].id!!
        val action = CyclingLogsFragmentDirections.actionNavigationLogsCyclingToNavigationCyclingDetail(id)
        NavHostFragment.findNavController(this).navigate(action)
    }

    companion object
}