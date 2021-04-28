package com.softhouse.workingout.ui.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.softhouse.workingout.alarm.Utils
import com.softhouse.workingout.alarm.bind
import com.softhouse.workingout.data.Alarm
import com.softhouse.workingout.databinding.AlarmViewBinding

class AlarmAdapter(private val listener: AlarmListener) :
    ListAdapter<Alarm, AlarmViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Alarm>() {
        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem.alarmId == newItem.alarmId
        }

        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AlarmViewBinding.inflate(inflater, parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
}

class AlarmViewHolder(private val binding: AlarmViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(alarm: Alarm, listener: AlarmListener) {
        binding.bind(alarm)
        updateViewsWithAlarmStatus(alarm.enabled)
        binding.root.setOnClickListener {
            listener.onClick(alarm.alarmId)
        }
        binding.alarmSwitch.setOnCheckedChangeListener { _, b ->
            updateViewsWithAlarmStatus(b)
            listener.onAlarmEnableStatusChanged(alarm.alarmId, b)
        }
    }

    private fun updateViewsWithAlarmStatus(enabled: Boolean) {
        val alpha = if (enabled) Utils.ENABLED_ALPHA else Utils.DISABLED_ALPHA
        for (v in binding.constraintLayout.children) {
            v.alpha = alpha
        }
    }
}

interface AlarmListener {
    fun onClick(alarmId: Int)
    fun onAlarmEnableStatusChanged(alarmId: Int, enabled: Boolean)
}
