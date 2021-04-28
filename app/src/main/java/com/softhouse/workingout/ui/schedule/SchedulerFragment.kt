package com.softhouse.workingout.ui.schedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.softhouse.androidutils.EventObserver
import com.softhouse.workingout.listener.AlarmReceiver
import com.softhouse.workingout.databinding.FragmentAlarmBinding
import com.softhouse.workingout.alarm.setNearestAlarm
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.component.KoinApiExtension

class SchedulerFragment : Fragment(), AlarmListener {

    private lateinit var alarmAdapter: AlarmAdapter
    private var _binding: FragmentAlarmBinding? = null
    private val binding: FragmentAlarmBinding get() = _binding!!

    val viewModel: SchedulerViewModel by sharedViewModel()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            viewModel.refreshData()
        }
    }

    @KoinApiExtension
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater)
        requireActivity().registerReceiver(receiver, IntentFilter(AlarmReceiver.ACTION_FINISH))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmAdapter = AlarmAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = alarmAdapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        viewModel.alarms.observe(viewLifecycleOwner) {
            val list = it ?: emptyList()
            alarmAdapter.submitList(list)
            binding.recyclerView.isVisible = list.isNotEmpty()
            binding.emptyGroup.isVisible = list.isEmpty()
        }

        viewModel.nearestAlarm.observe(viewLifecycleOwner) {
            binding.setNearestAlarm(it)
        }

        viewModel.goToEditPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                navigateToEditFragment(it)
            }
        )

        viewModel.goToPreferencesPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                navigateToPreferencesFragment()
            }
        )

        binding.fab.setOnClickListener {
            viewModel.onAlarmSelected()
        }

        binding.preferencesButton.setOnClickListener {
            viewModel.onPreferencesButtonClicked()
        }
        viewModel.refreshData()
    }

    override fun onClick(alarmId: Int) {
        viewModel.onAlarmSelected(alarmId)
    }

    override fun onAlarmEnableStatusChanged(alarmId: Int, enabled: Boolean) {
        viewModel.onAlarmEnableStatusChanged(alarmId, enabled)
    }

    private fun navigateToEditFragment(id: Int) {
        val direction = SchedulerFragmentDirections.actionMainFragmentToAlarmEditFragment(id)
        findNavController().navigate(direction)
    }

    private fun navigateToPreferencesFragment() {
        val direction = SchedulerFragmentDirections.actionMainFragmentToPreferencesFragment()
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(receiver)
        _binding = null
    }
}
