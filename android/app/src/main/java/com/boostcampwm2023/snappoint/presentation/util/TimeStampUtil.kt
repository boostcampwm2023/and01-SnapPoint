package com.boostcampwm2023.snappoint.presentation.util

import android.content.res.Resources
import android.icu.text.SimpleDateFormat
import android.net.ParseException
import com.boostcampwm2023.snappoint.R
import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeStampUtil {

    fun stringToMills(timestamp: String): Long {
        if (timestamp == "") return 0L
        var mills = 0L
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ", Locale.KOREAN)
        try {
            val date = sdf.parse(timestamp)
            mills = date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return mills
    }

    fun getTimeStamp(at: Long, resources: Resources): String {
        var timestamp = ""
        val diffMills = (System.currentTimeMillis() - at) / 1000
        when {
            diffMills < 60 -> {
                timestamp = resources.getString(R.string.moments_ago)
            }

            diffMills < 60 * 60 -> {
                timestamp = TimeUnit.SECONDS.toMinutes(diffMills)
                    .toString() + resources.getString(R.string.minutes_ago)
            }

            diffMills < 60 * 60 * 24 -> {
                timestamp = TimeUnit.SECONDS.toHours(diffMills)
                    .toString() + resources.getString(R.string.hours_ago)
            }

            diffMills < 60 * 60 * 24 * 7 -> {
                timestamp = TimeUnit.SECONDS.toDays(diffMills)
                    .toString() + resources.getString(R.string.days_ago)
            }

            diffMills < 60 * 60 * 24 * 28 -> {
                timestamp =
                    (TimeUnit.SECONDS.toDays(diffMills) / 7).toString() + resources.getString(R.string.weeks_ago)
            }

            diffMills < 31556952 -> {
                timestamp =
                    (TimeUnit.SECONDS.toDays(diffMills) / 30).toString() + resources.getString(R.string.months_ago)
            }

            else -> {
                timestamp =
                    (TimeUnit.SECONDS.toDays(diffMills) / 365).toString() + resources.getString(R.string.years_ago)
            }
        }
        return timestamp
    }
}