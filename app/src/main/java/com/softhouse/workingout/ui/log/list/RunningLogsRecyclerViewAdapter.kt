package com.softhouse.workingout.ui.log.list

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.softhouse.workingout.data.db.Running
import com.softhouse.workingout.databinding.FragmentRunningLogsBinding
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.shared.DateTimeUtility
import com.softhouse.workingout.shared.DateTimeUtility.getTimeMeasurementFromMillis
import com.softhouse.workingout.ui.log.dto.RunningDTO
import java.util.*

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
            val startCalendar = DateTimeUtility.getCalenderFromMillis(data.startWorkout)
            val endCalender = DateTimeUtility.getCalenderFromMillis(data.endWorkout)
            binding.data = RunningDTO(
                data.id ?: Constants.INVALID_ID_DB,
                data.steps,
                "${startCalendar.get(Calendar.HOUR_OF_DAY)}:${startCalendar.get(Calendar.MINUTE)}:${
                    startCalendar.get(
                        Calendar.SECOND
                    )
                }",
                "${endCalender.get(Calendar.HOUR_OF_DAY)}:${endCalender.get(Calendar.MINUTE)}:${
                    endCalender.get(
                        Calendar.SECOND
                    )
                }",
                getTimeMeasurementFromMillis(data.endWorkout - data.startWorkout)
            )
            binding.executePendingBindings()
        }
    }
}