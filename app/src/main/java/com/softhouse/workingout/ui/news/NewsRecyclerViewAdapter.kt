package com.softhouse.workingout.ui.news

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentNewsListBinding
import com.softhouse.workingout.shared.ImageLoader
import com.softhouse.workingout.ui.news.item.NewsContent
import org.jetbrains.annotations.NotNull

class NewsRecyclerViewAdapter(
    private val values: List<NewsContent.NewsItem>, private val listener: OnNewsItemClickListener
) : RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentNewsListBinding.inflate(layoutInflater)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(values[position])
        holder.itemView.setOnClickListener {
            holder.onClick(holder.itemView)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val binding: FragmentNewsListBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        override fun toString(): String {
            return "NewsItem no : $adapterPosition"
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onNewsClick(adapterPosition)
            }
        }

        fun bind(data: NewsContent.NewsItem) {
            binding.data = data
            binding.executePendingBindings()
        }
    }

    interface OnNewsItemClickListener {
        fun onNewsClick(position: Int)
    }

    companion object {

        // Bind adapter to load image

        @JvmStatic
        @BindingAdapter("loadImage")
        fun loadImage(imageView: ImageView, imageUrl: String) {
            Glide.with(imageView)
                .load(imageUrl)
                .optionalCenterCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(imageView)
        }
    }
}