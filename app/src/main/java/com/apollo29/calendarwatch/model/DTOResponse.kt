package com.apollo29.calendarwatch.model

class DTOResponse<T> {
    var data: T? = null
    var errorCode = 0

    companion object {
        const val FAIL = 0
        const val SUCCESS = 1
    }
}