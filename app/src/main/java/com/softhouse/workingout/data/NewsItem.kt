package com.softhouse.workingout.data

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
