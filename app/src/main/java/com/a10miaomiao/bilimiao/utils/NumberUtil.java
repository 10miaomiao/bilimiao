package com.a10miaomiao.bilimiao.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hcc on 2016/10/11 14:35
 * 100332338@qq.com
 */

public class NumberUtil {

    public static String converString(int num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        String unit = "万";
        double newNum = num / 10000.0;

        String numStr = String.format("%." + 1 + "f", newNum);
        return numStr + unit;
    }

    public static String converDuration(int duration) {
        String s = String.valueOf(duration % 60);
        String min = String.valueOf(duration / 60);
        if (s.length() == 1)
            s = "0" + s;
        if (min.length() == 1)
            min = "0" + min;
        return min + ":" + s;
    }

    public static String converCTime(Long ctime) {
        Date date = new Date(ctime * 1000);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(date);
    }
}
