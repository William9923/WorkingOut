package com.softhouse.workingout.ui.home

import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.softhouse.workingout.R
import com.softhouse.workingout.ui.sensor.CompassFragment

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var mViewPager: ViewPager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // Observer
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
        })

        val tabLayout = root.findViewById<TabLayout>(R.id.home_tab_layout)

        val tabViewPager = root.findViewById<ViewPager>(R.id.tab_viewpager)

        tabLayout?.setupWithViewPager(tabViewPager)
        setupViewPager(tabViewPager!!)

        return root
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = PageAdapter(activity!!.supportFragmentManager)

        Log.i("PAGER", "Setup tab")

        adapter.addFragment(RunningFragment(), "\uD83D\uDC5F Running")
        adapter.addFragment(CompassFragment(), "\uD83D\uDEB4 Cycling")

        viewPager.adapter = adapter
    }
}