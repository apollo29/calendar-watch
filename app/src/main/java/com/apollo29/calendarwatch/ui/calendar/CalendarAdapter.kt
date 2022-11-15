package com.apollo29.calendarwatch.ui.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apollo29.calendarwatch.databinding.ViewCalendarItemBinding
import com.apollo29.calendarwatch.model.DTOCalendar
import com.apollo29.calendarwatch.repository.Preferences

class CalendarAdapter(val preferences: Preferences) :
    ListAdapter<DTOCalendar, CalendarAdapter.ViewHolder>(CalendarDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ViewCalendarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(val binding: ViewCalendarItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = itemView.apply {
            val calendar = getItem(position)
            binding.calendarTitle.text = calendar.title
            if (position == 0 || calendar.account != getItem(position - 1).account) {
                binding.accountBackground.visibility = View.VISIBLE
                binding.accountName.text = calendar.account
                binding.divider.visibility = View.GONE
            } else {
                binding.accountBackground.visibility = View.GONE
                binding.divider.visibility = View.VISIBLE
            }

            val checked = preferences.calendarEnabled(calendar.id)
            binding.calendarSwitch.isChecked = checked
            binding.calendarSwitch.setOnCheckedChangeListener { _, isChecked ->
                preferences.calendarEnabled(calendar.id, isChecked)
            }
        }
    }
}