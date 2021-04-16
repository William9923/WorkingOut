package com.softhouse.workingout.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.softhouse.workingout.R
import com.softhouse.workingout.shared.ImageLoader
import com.softhouse.workingout.ui.news.item.NewsContent

class NewsRecyclerViewAdapter(
    private val values: List<NewsContent.NewsItem>, private val clickListener: (String) -> Unit
) : RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_news_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = values[position]

        holder.itemView.setOnClickListener {
            clickListener.invoke(item.url)
        }

        ImageLoader(holder.imageView).execute(item.imageURL)
        holder.titleView.text = item.title.substring(0, 10) + "..."

        holder.subtitleView.text = "..." + item.title.substring(10)

        if (item.desc.length > 20) {
            holder.contentView.text = item.desc.substring(0, 20) + "..."
        } else {
            holder.contentView.text = item.desc
        }

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.title_card)
        val subtitleView: TextView = view.findViewById(R.id.subtitle_card)
        val imageView: ImageView = view.findViewById(R.id.image_card)
        val contentView: TextView = view.findViewById(R.id.content_card)

        override fun toString(): String {
            return super.toString() + " '" + titleView.text + "'"
        }
    }
}