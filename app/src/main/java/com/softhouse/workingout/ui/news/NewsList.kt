package com.softhouse.workingout.ui.news

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softhouse.workingout.R
import com.softhouse.workingout.data.remote.NewsApiConfig
import com.softhouse.workingout.data.NewsContent
import com.softhouse.workingout.data.remote.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        Log.d("NewsListViewModel", "Provider called")
        viewModel = ViewModelProvider(requireActivity()).get(NewsListViewModel::class.java)
        initRecyclerViewAdapter(view)

//        // TODO : Refactoring how to get data
//        if (NewsContent.ITEMS.size <= 0)
//            loadData(view)
        return view
    }

    // TODO : Make loadData in
//    private fun loadData(view: View) {
//        val api = NewsApiConfig()
//
//        api.getService().getNews(api.country, api.category, api.key)
//            .enqueue(object : Callback<NewsResponse> {
//                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
//                    Log.e("ERROR", t.message!!)
//                }
//
//                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
//                    if (response.isSuccessful) {
//                        val data = response.body()!!
//                        val articles = data.articles!!
//                        articles.forEachIndexed { index, element ->
//
//                            // TODO : Make a sample image for null reference image and null reference site
//
//                            if (element!!.urlToImage != null && element.url != null) {
//
//                                val title: String = element.title ?: "No Title"
//                                val description: String =
//                                    element.description ?: "No Desc"
//                                NewsContent.addItem(
//                                    NewsContent.NewsItem(
//                                        index,
//                                        title,
//                                        description,
//                                        element.url,
//                                        element.urlToImage!!
//                                    )
//                                )
//                            }
//                        }
//
//                        if (view is RecyclerView) {
//                            view.adapter?.notifyDataSetChanged()
//                            Log.d("Notify", "Data Changed")
//                        }
//
//                        Log.d("SIZE:", NewsContent.ITEMS.size.toString())
//
//                    } else {
//                        Log.e("ERROR:", "Fetch data not successful")
//                    }
//
//                }
//            })
//    }

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
        viewModel.items.observe(requireActivity(), {
            Log.d("Observable", "Data changed")
            if (view is RecyclerView) {
                if (view?.adapter != null)
                    view.adapter = viewModel.items.value?.let { NewsRecyclerViewAdapter(it, this) }
                view?.adapter?.notifyDataSetChanged()
                Log.d("Notify", "Data Changed")
            }
        })
    }

    override fun onNewsClick(position: Int) {
        val url: String = viewModel.items.value!![position].url ?: ""
        val action = NewsListDirections.actionNavigationNewsToWebFragment(url)
        NavHostFragment.findNavController(this).navigate(action)
    }
}