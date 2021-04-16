package com.softhouse.workingout.ui.news.item

import java.util.*

object NewsContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<NewsItem> = ArrayList()

    fun addItem(item: NewsItem) {
        ITEMS.add(item)
    }

    /**
     * A data item representing a piece of news.
     */
    data class NewsItem(
        val id: Int,
        val title: String,
        val desc: String,
        val url: String,
        val imageURL: String
    ) {
        override fun toString(): String = title + "\n" + desc
    }

}
