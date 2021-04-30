package com.softhouse.workingout.ui.schedules

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.softhouse.workingout.R
import com.softhouse.workingout.ui.log.list.CyclingLogsFragmentDirections
import com.softhouse.workingout.ui.log.list.CyclingLogsRecyclerViewAdapter
import com.softhouse.workingout.ui.schedules.dummy.DummyContent

class ViewScheduleFragment : Fragment() {

    private var columnCount = 1

    lateinit var fab: FloatingActionButton
    lateinit var list: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_schedule_list, container, false)

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // Set the adapter
//        initRecyclerViewAdapter(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = view.findViewById(R.id.list)
        // Set the adapter, TODO : replace implementation
        if (list is RecyclerView) {
            Log.d("View", "Adapter init")
            with(list) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = ScheduleRecyclerViewAdapter(DummyContent.ITEMS)
            }
        }
        fab = view.findViewById(R.id.floating_action_button) as FloatingActionButton
        fab.setOnClickListener {
            val action = ViewScheduleFragmentDirections.actionNavigationScheduleListToNavigationScheduler()
            NavHostFragment.findNavController(this).navigate(action)
        }

    }

    private fun initRecyclerViewAdapter(view: View) {
        // Set the adapter
//        if (view is RecyclerView) {
//            view.layoutManager = when {
//                columnCount <= 1 -> LinearLayoutManager(context)
//                else -> GridLayoutManager(context, columnCount)
//            }
//            view.adapter = viewModel.records.value?.let { CyclingLogsRecyclerViewAdapter(it, this) }
//        }
//
//        // Observe data change
//        viewModel.records.observe(viewLifecycleOwner, {
//            Log.d("Data", "Change before case")
//            if (view is RecyclerView) {
//                Log.d("Data", "Changed")
//                if (view.adapter != null)
//                    view.adapter = viewModel.records.value?.let { CyclingLogsRecyclerViewAdapter(it, this) }
//                Log.d("Adapter", "notify")
//                view.adapter?.notifyDataSetChanged()
//            }
//        })
    }

    // TODO : As delete button on click
    fun onRecordClick(position: Int) {
        Log.d("Schedule", "Item in $position clicked")
    }

    companion object
}