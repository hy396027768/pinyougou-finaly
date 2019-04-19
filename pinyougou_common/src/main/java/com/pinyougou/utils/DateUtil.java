package com.pinyougou.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
    /**
     * 获取固定间隔时刻集合
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param interval 时间间隔(单位：小时)
     * @return
     */
    public static List<String> getIntervalTimeList(Date startDate, Date endDate, int interval){
        List<String> list = new ArrayList<>();
        while(startDate.getTime()<=endDate.getTime()){
            list.add(DateUtil.convertDate2String("yyyy-MM-dd",startDate));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.HOUR,interval);
            if(calendar.getTime().getTime()>endDate.getTime()){
                if(!startDate.equals(endDate)){
                    list.add(DateUtil.convertDate2String("yyyy-MM-dd",endDate));
                }
                startDate = calendar.getTime();
            }else{
                startDate = calendar.getTime();
            }

        }
        return list;
    }

    /**
     * 将时间转换成指定的字符串
     * @param format
     * @param date
     * @return
     */
    private static String convertDate2String(String format, Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    private static Date convertString2Date(String format, String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

    }
    //测试用
    //public static void main(String[] args) {
    //    List<String> list = getIntervalTimeList(convertString2Date("yyyy-MM-dd","2019-4-11"), convertString2Date("yyyy-MM-dd","2019-4-17"), 24);
    //    System.out.println(list);
    //}
}
