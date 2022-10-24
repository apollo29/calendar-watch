package com.apollo29.calendarwatch.model

data class DTOEvent(
    var alertsList: List<DTOAlert> = emptyList(),
    var allDay: Boolean = false,
    var attend_status: Int,
    var calendar_id: String,
    var endDate: Long,
    var id: String,
    var startDate: Long,
    var title: String
)