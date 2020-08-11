package com.avvnapps.unigrocretail.utils

import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtils{
    companion object {
        var TAG = "DATE_TIME_UTILS"
        var dateFormatter = SimpleDateFormat("dd MMM")
        var dateTimeFormatter = SimpleDateFormat("dd MMM, hh:mm")

        fun get24hrTime(): Int {
            val calendar = Calendar.getInstance()
            return calendar.get(Calendar.HOUR_OF_DAY)
        }

        fun getPreferredDeliveryDate(preferredDate: Long, preferredSlot: Int): String {
            val preferredCalendar = Calendar.getInstance()
            preferredCalendar.timeInMillis = preferredDate
            val currentCalendar = Calendar.getInstance()

            val preferredDay = preferredCalendar.get(Calendar.DAY_OF_YEAR)
            val currentDay = currentCalendar.get(Calendar.DAY_OF_YEAR)
            val preferredYear = preferredCalendar.get(Calendar.YEAR)
            val currentYear = currentCalendar.get(Calendar.YEAR)


            val preferredDate = dateFormatter.format(preferredDate)

            var preferredTimeSlot = getTimeSlot(preferredSlot)

            if (preferredYear == currentYear) {
                if (currentDay == preferredDay) {
                    return "Today, $preferredTimeSlot"
                } else if (currentDay == preferredDay - 1) {
                    return "Tomorrow, $preferredTimeSlot"
                }

            }
            return "$preferredDate, $preferredTimeSlot"

        }

        private fun getTimeSlot(preferredSlot: Int): String {
            when (preferredSlot) {
                0 -> return "by 11AM"
                1 -> return "by 1PM"
                2 -> return "by 5PM"
                3 -> return "by 7PM"
                4 -> return "by 9PM"

            }
            return "by 11AM"
        }


    }
}