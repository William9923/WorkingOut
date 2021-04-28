package com.softhouse.workingout.ui.log.list

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
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
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.ui.log.DetailRunningFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TwoPaneRunningLogsFragment : Fragment(), RunningLogsRecyclerViewAdapter.OnRunningRecordClickListener {

    private val viewModel: RunningLogsViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    lateinit var binding: FragmentTwoPaneRunningLogsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTwoPaneRunningLogsBinding.inflate(inflater, container, false)
        initRecyclerViewAdapter(binding.listPane)
        return binding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (requireActivity().resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val action =
                TwoPaneRunningLogsFragmentDirections.actionNavigationTwoPaneRunningLogsToNavigationLogsRunning()
            NavHostFragment.findNavController(this).navigate(action)
        }
    }

    private fun initRecyclerViewAdapter(view: View) {
        // Set the adapter
        if (view is RecyclerView) {
            view.layoutManager = LinearLayoutManager(context)
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
        replaceSidePane(Constants.INVALID_ID_DB)
    }

    override fun onRecordClick(position: Int) {
        Log.d("Detect", "Item in $position clicked")
        val id: Long = viewModel.records.value!![position].id!!
        replaceSidePane(id)
    }

    private fun replaceSidePane(id: Long) {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.detail_container,
                DetailRunningPaneFragment(id)
            )
        }
    }

    companion object
}