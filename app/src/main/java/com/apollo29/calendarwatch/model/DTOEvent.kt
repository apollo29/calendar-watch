package com.apollo29.calendarwatch.model

data class DTOEvent(
    var id: String,
    var title: String,
    var startDate: Long,
    var endDate: Long,
    var allDay: Boolean = false,
    var calendar_id: String,
    var attend_status: Int
) {
    val alertsList: MutableList<DTOAlert> = mutableListOf()
}