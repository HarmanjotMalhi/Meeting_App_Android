package com.example.scheduler.data;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;

public class DateUtils {

    @SuppressLint("NewApi")
    public static Calendar getNextWeekday(Calendar currentDate) {
        Calendar nextDate = (Calendar) currentDate.clone();

        nextDate.add(Calendar.DAY_OF_MONTH, 1);

        while (nextDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || nextDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            nextDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        return nextDate; // Return the next weekday
    }
}
