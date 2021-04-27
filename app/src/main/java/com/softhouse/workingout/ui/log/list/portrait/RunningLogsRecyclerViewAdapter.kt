package com.softhouse.workingout.ui.log.list.portrait

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.softhouse.workingout.R
import com.softhouse.workingout.data.NewsItem
import com.softhouse.workingout.data.db.Running
import com.softhouse.workingout.databinding.FragmentNewsItemBinding
import com.softhouse.workingout.databinding.FragmentRunningLogsBinding
import com.softhouse.workingout.databinding.FragmentRunningLogsListBinding

import com.softhouse.workingout.ui.log.list.portrait.dummy.DummyContent.DummyItem
import com.softhouse.workingout.ui.news.NewsRecyclerViewAdapter

class RunningLogsRecyclerViewAdapter(
    private val values: List<Running>, private val listener: OnRunningRecordClickListener
) : RecyclerView.Adapter<RunningLogsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentRunningLogsBinding.inflate(layoutInflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
        holder.itemView.setOnClickListener {
            Log.d("Event", "Item clicked")
            holder.onClick(holder.itemView)
        }
    }

    override fun getItemCount(): Int = values.size

    interface OnRunningRecordClickListener {
        fun onRecordClick(position: Int)
    }

    inner class ViewHolder(val binding: FragmentRunningLogsBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        override fun toString(): String {
            return "Running Record no : $adapterPosition"
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Log.d("Position", "Index : $adapterPosition")
                listener.onRecordClick(adapterPosition)
            }
        }

        fun bind(data: Running) {
            binding.data = data
            binding.executePendingBindings()
        }
    }
}