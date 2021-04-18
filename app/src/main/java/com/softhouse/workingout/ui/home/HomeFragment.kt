package com.softhouse.workingout.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.softhouse.workingout.databinding.FragmentHomeBinding
import com.softhouse.workingout.ui.sensor.CompassFragment
import com.softhouse.workingout.ui.sensor.RunningFragment

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Observer
        homeViewModel.text.observe(viewLifecycleOwner, {
        })

        binding.homeTabLayout.setupWithViewPager(binding.tabViewpager)
        setupViewPager(binding.tabViewpager)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.homeTabLayout.setupWithViewPager(binding.tabViewpager)
        setupViewPager(binding.tabViewpager)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = PageAdapter(childFragmentManager)

        Log.i("PAGER", "Setup tab")

        adapter.addFragment(RunningFragment(), "\uD83D\uDC5F Running")
//        adapter.addFragment(CompassFragment(), "\uD83D\uDEB4 Cycling")

        viewPager.adapter = adapter
    }
}