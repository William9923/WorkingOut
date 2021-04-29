package com.softhouse.workingout.ui.log.list

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.softhouse.workingout.data.db.Cycling
import com.softhouse.workingout.databinding.FragmentCyclingLogsBinding
import com.softhouse.workingout.shared.Constants
import com.softhouse.workingout.shared.DateTimeUtility
import com.softhouse.workingout.ui.log.dto.CyclingDTO
import com.softhouse.workingout.ui.log.dto.RunningDTO
import java.util.*

class CyclingLogsRecyclerViewAdapter(
    private val values: List<Cycling>, private val listener: OnCyclingRecordClickListener
) : RecyclerView.Adapter<CyclingLogsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FragmentCyclingLogsBinding.inflate(layoutInflater)
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

    interface OnCyclingRecordClickListener {
        fun onRecordClick(position: Int)
    }

    inner class ViewHolder(val binding: FragmentCyclingLogsBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        override fun toString(): String {
            return "Cycling Record no : $adapterPosition"
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Log.d("Position", "Index : $adapterPosition")
                listener.onRecordClick(adapterPosition)
            }
        }

        fun bind(data: Cycling) {
            val startCalendar = DateTimeUtility.getCalenderFromMillis(data.startWorkout)
            val endCalender = DateTimeUtility.getCalenderFromMillis(data.endWorkout)
            binding.data = CyclingDTO(
                data.id ?: Constants.INVALID_ID_DB,
                data.distanceInMeters,
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
                DateTimeUtility.getTimeMeasurementFromMillis(data.endWorkout - data.startWorkout)
            )
            binding.executePendingBindings()
        }
    }
}