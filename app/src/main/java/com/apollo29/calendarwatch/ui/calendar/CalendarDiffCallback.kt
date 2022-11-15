package com.apollo29.calendarwatch.ui.calendar

import androidx.recyclerview.widget.DiffUtil
import com.apollo29.calendarwatch.model.DTOCalendar

class CalendarDiffCallback : DiffUtil.ItemCallback<DTOCalendar>() {
    override fun areItemsTheSame(
        oldItem: DTOCalendar,
        newItem: DTOCalendar
    ): Boolean = oldItem.account == newItem.account

    override fun areContentsTheSame(
        oldItem: DTOCalendar,
        newItem: DTOCalendar
    ): Boolean = oldItem.account == newItem.account
}