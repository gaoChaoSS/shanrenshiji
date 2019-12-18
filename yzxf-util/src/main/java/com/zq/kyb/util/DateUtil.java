package com.zq.kyb.util;

import java.util.Calendar;

/**
 * Created by hujoey on 16/5/19.
 */
public class DateUtil {
    //获取一天是星期几
    public static int getWeekDay(Calendar c) {
        boolean isFirstSunday = (c.getFirstDayOfWeek() == Calendar.SUNDAY);
        int weekDay = c.get(Calendar.DAY_OF_WEEK);
        //若一周第一天为星期天，则-1,否则不减
        if (isFirstSunday) {
            //weekDay = weekDay - 1;
            if (weekDay == 0) {
                weekDay = 7;
            }
        }
        return weekDay;
    }
}
