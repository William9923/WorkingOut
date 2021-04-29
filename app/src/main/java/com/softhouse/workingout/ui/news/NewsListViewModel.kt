package com.softhouse.workingout.ui.news

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.softhouse.workingout.data.NewsItem
import com.softhouse.workingout.data.remote.NewsApiConfig
import com.softhouse.workingout.data.remote.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NewsListViewModel : ViewModel() {

    private val _items = MutableLiveData<List<NewsItem>>().apply {
        value = ArrayList()
    }

    val items: MutableLiveData<List<NewsItem>> = _items

    init {
        loadData()
    }

    private fun loadData() {
        val api = NewsApiConfig()
        var news = ArrayList<NewsItem>()

        api.getService().getNews(api.country, api.category, api.key)
            .enqueue(object : Callback<NewsResponse> {
                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    Log.e("ERROR", t.message!!)
                }

                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (response.isSuccessful) {
                        val data = response.body()!!
                        val articles = data.articles!!
                        articles.forEachIndexed { index, element ->
                            // TODO : Make a sample image for null reference image and null reference site
                            if (element!!.urlToImage != null && element.url != null) {
                                val title: String = element.title ?: "No Title"
                                val description: String =
                                    element.description ?: "No Desc"

                                news.add(
                                    NewsItem(
                                        index,
                                        title,
                                        if (description.length <= 100) description else description.substring(
                                            0,
                                            100
                                        ) + "...",
                                        element.url,
                                        element.urlToImage!!,
                                        element.source?.name ?: "",
                                        element.author ?: "",
                                        element.publishedAt?.substring(0,10) ?: ""
                                    )
                                )
                            }
                        }
                        _items.postValue(news)
                    } else {
                        Log.e("ERROR:", "Fetch data not successful")
                    }

                }
            })
    }
}