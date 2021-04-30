package com.softhouse.workingout.ui.news

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.softhouse.workingout.R

/**
 * A fragment representing a list of Items.
 */
class NewsList : Fragment(), NewsRecyclerViewAdapter.OnNewsItemClickListener {

    private lateinit var viewModel: NewsListViewModel

    private val columnCount: () -> Int = {
        val orientation = activity?.resources?.configuration?.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 2
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (view is RecyclerView) {
            initRecyclerViewAdapter(view as RecyclerView)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(NewsListViewModel::class.java)
        initRecyclerViewAdapter(view)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        return view
    }

    private fun initRecyclerViewAdapter(view: View) {
        // Set the adapter
        if (view is RecyclerView) {
            view.layoutManager = when {
                columnCount() <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount())
            }
            view.adapter = viewModel.items.value?.let { NewsRecyclerViewAdapter(it, this) }
        }

        // Observe data change
        viewModel.items.observe(viewLifecycleOwner, {
            if (view is RecyclerView) {
                if (view.adapter != null)
                    view.adapter = viewModel.items.value?.let { NewsRecyclerViewAdapter(it, this) }
                view.adapter?.notifyDataSetChanged()
            }
        })
    }

     override fun onNewsClick(position: Int) {
        val url: String = viewModel.items.value!![position].url
        val action = NewsListDirections.actionNavigationNewsToWebFragment(url)
        NavHostFragment.findNavController(this).navigate(action)
    }
}