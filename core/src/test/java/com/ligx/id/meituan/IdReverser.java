package com.ligx.id.meituan;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Author: ligongxing.
 * Date: 2018年08月19日.
 */
public class IdReverser
{
    public static long getTimestamp(final long uid) {
        if (uid < 99999999999999L) {
            return 0L;
        }
        return uid >> 22;
    }

    public static Date getDateTime(final long uid) {
        final long ts = getTimestamp(uid);
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ts);
        return c.getTime();
    }

    public static String getTimeString(final long uid) {
        return getTimeString(uid, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getTimeString(final long uid, final String format) {
        final Date dt = getDateTime(uid);
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(dt);
    }

    public static void main(final String[] args) {
        System.out.println(getTimeString(5863966960605200601L));
    }
}
