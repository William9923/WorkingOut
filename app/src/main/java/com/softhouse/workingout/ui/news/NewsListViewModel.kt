package com.softhouse.workingout.ui.news

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.softhouse.workingout.data.NewsContent
import com.softhouse.workingout.data.remote.NewsApiConfig
import com.softhouse.workingout.data.remote.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class NewsListViewModel : ViewModel() {

//    val _items: MutableLiveData<List<NewsContent.NewsItem>> = MutableLiveData<List<NewsContent.NewsItem>>()

    private val _items = MutableLiveData<List<NewsContent.NewsItem>>().apply {
        value = ArrayList()
    }

//    val items = MutableLiveData<List<NewsContent.NewsItem>>()
    val items: LiveData<List<NewsContent.NewsItem>> = _items

    init {
        loadData()
        Log.i("NewsListViewModel", "NewsListViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("NewsListViewModel", "NewsListViewModel destroyed!")
    }

    // TODO : Make loadData in
    private fun loadData() {
        val api = NewsApiConfig()
        var news = ArrayList<NewsContent.NewsItem>()

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
                                    NewsContent.NewsItem(
                                        index,
                                        title,
                                        description,
                                        element.url,
                                        element.urlToImage!!
                                    )
                                )
                            }
                        }
                        _items.value = news
                    } else {
                        Log.e("ERROR:", "Fetch data not successful")
                    }

                }
            })
    }
}