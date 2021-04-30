package com.softhouse.workingout.ui.schedules

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softhouse.workingout.data.db.Schedule
import com.softhouse.workingout.databinding.FragmentViewScheduleBinding
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.ui.schedules.dto.ScheduleDTO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_view_schedule.view.*
import java.text.SimpleDateFormat
import java.util.*


class ScheduleRecyclerViewAdapter(
    private val values: List<Schedule>, private val listener: OnScheduleItemClickListener
) : RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentViewScheduleBinding.inflate(layoutInflater)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(values[position])
        holder.itemView.deleteBtn.setOnClickListener {
            Log.d("Event", "Item clicked")
            holder.onClick(holder.itemView)
        }
    }

    override fun getItemCount(): Int = values.size

    interface OnScheduleItemClickListener {
        fun onScheduleActionClick(position: Int)
    }

    inner class ViewHolder(val binding: FragmentViewScheduleBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Log.d("Position", "Index : $adapterPosition")
                listener.onScheduleActionClick(adapterPosition)
            }
        }

        fun bind(data: Schedule) {

            var format = SimpleDateFormat(
                "yyyy-MM-dd", Locale.ENGLISH
            )

            val timeFormat = SimpleDateFormat(
                "HH:mm", Locale.ENGLISH
            )

            val detail = when (data.types) {
                Types.SINGLE -> {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = data.date!!
                    format.format(calendar.time)
                }
                Types.REPEATING_WEEK -> {
                    var weeks = ""
                    data.weeks.forEach {
                        weeks += it.substring(0, 1).toUpperCase() + "|"
                    }
                    weeks
                }
                Types.REPEATING -> "Repeat"
            }

            val startCalendar = Calendar.getInstance()
            val endCalendar = Calendar.getInstance()

            startCalendar.timeInMillis = data.startTime
            endCalendar.timeInMillis = data.stopTime

            binding.dto = ScheduleDTO(
                data.id ?: Constants.INVALID_ID_DB,
                timeFormat.format(startCalendar.time),
                timeFormat.format(endCalendar.time),
                data.types.toString() + " | " + data.mode.toString(),
                detail,
                data.autoStart
            )
            binding.executePendingBindings()
        }
    }
}