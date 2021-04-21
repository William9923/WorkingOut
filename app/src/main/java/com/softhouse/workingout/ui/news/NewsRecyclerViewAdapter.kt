package com.softhouse.workingout.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.softhouse.workingout.R
import com.softhouse.workingout.databinding.FragmentNewsItemBinding
import com.softhouse.workingout.data.NewsContent

class NewsRecyclerViewAdapter(
    private val values: List<NewsContent.NewsItem>, private val listener: OnNewsItemClickListener
) : RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentNewsItemBinding.inflate(layoutInflater)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(values[position])
        holder.itemView.setOnClickListener {
            holder.onClick(holder.itemView)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(val binding: FragmentNewsItemBinding) : RecyclerView.ViewHolder(binding.root),
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

    fun setData(values: List<NewsContent.NewsItem>) {

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