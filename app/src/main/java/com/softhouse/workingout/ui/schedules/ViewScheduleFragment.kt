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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.softhouse.workingout.R
import com.softhouse.workingout.ui.log.list.CyclingLogsFragmentDirections
import com.softhouse.workingout.ui.log.list.CyclingLogsRecyclerViewAdapter
import com.softhouse.workingout.ui.log.list.RunningLogsViewModel
import com.softhouse.workingout.ui.schedules.dummy.DummyContent

class ViewScheduleFragment : Fragment(), ScheduleRecyclerViewAdapter.OnScheduleItemClickListener {

    private var columnCount = 1

    lateinit var fab: FloatingActionButton
    lateinit var list: RecyclerView

    private val viewModel: ScheduleListViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_schedule_list, container, false)

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // Set the adapter
        list = view.findViewById(R.id.list)
        if (list is RecyclerView) {
            initRecyclerViewAdapter(list)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab = view.findViewById(R.id.floating_action_button) as FloatingActionButton
        fab.setOnClickListener {
            val action = ViewScheduleFragmentDirections.actionNavigationScheduleListToNavigationScheduler()
            NavHostFragment.findNavController(this).navigate(action)
        }

    }

    private fun initRecyclerViewAdapter(view: View) {
        // Set the adapter
        if (view is RecyclerView) {
            view.layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            view.adapter = viewModel.schedules.value?.let { ScheduleRecyclerViewAdapter(it, this) }
        }

        // Observe data change
        viewModel.schedules.observe(viewLifecycleOwner, {
            Log.d("Data", "Change before case")
            if (view is RecyclerView) {
                Log.d("Data", "Changed")
                if (view.adapter != null)
                    view.adapter = viewModel.schedules.value?.let { ScheduleRecyclerViewAdapter(it, this) }
                Log.d("Adapter", "notify")
                view.adapter?.notifyDataSetChanged()
            }
        })
    }

    // TODO : As delete button on click
    override fun onScheduleActionClick(position: Int) {
        Log.d("Schedule", "Item in $position clicked")
        viewModel.schedules.value?.get(position)?.let { viewModel.deleteData(it) }
    }

    companion object
}