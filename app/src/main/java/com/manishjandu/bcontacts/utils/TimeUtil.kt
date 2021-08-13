package com.manishjandu.bcontacts.utils

object TimeUtil {

    fun getDateInString(day: Int, month: Int, year: Int): String {
        return "$day-$month-$year"
    }

    fun getTimeInAmPm(hour: Int, minute: Int): String {
        return if (hour > 12) {
            String.format("%02d", hour - 12) + ":" + String.format(
                "%02d",
                minute
            ) + "PM"
        } else {
            String.format("%02d", hour) + ":" + String.format(
                "%02d",
                minute
            ) + "AM"
        }
    }

}