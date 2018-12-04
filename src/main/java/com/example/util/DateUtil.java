package com.example.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);

    static {
        format.setLenient(false);
    }

    public static Timestamp toTimestamp(String time) throws ParseException {
        Date date = format.parse(time);
        return new Timestamp(date.getTime());
    }

    /**
     * 将时间转化为指定格式的字符串输出
     * @param time 时间
     */
    public static String string(Timestamp time) {
        if (time == null)
            return null;
        return format.format(time);
    }

}
