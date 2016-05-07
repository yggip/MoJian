package net.roocky.mojian;

import android.app.Application;
import android.content.Context;

import java.util.Calendar;

/**
 * Created by roocky on 04/02.
 *
 */
public class Mojian extends Application {
    public static Context context;

    public static String[] numbers;
    //当前日期&时间
    public static int year;
    public static int month;
    public static int day;
    public static int hour;
    public static int minute;
    //RecyclerView刷新类型
    public static final int FLUSH_ADD = 0;
    public static final int FLUSH_REMOVE = 1;
    public static final int FLUSH_ALL = 2;
    //天气
    public static final int WEATHER_SUN = 0;
    public static final int WEATHER_CLOUD = 1;
    public static final int WEATHER_RAIN = 2;
    public static final int WEATHER_SNOW = 3;
    //当前日记是否为锁定状态
    public static boolean isLocked = true;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        numbers = getResources().getStringArray(R.array.number_array);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

//        setBaichuanFb();
    }

    //百川反馈
//    private void setBaichuanFb() {
//        FeedbackAPI.initAnnoy(this, getString(R.string.baichuan_app_key));
//        Map<String, String> fbSetting = new ArrayMap<>();
//        fbSetting.put("enableAudio", "0");  //关闭语音
//        fbSetting.put("toAvatar", "http://xroocky.github.io/avatar.png");
//        fbSetting.put("bgColor", "#9e9e9e");
//        fbSetting.put("themeColor", "#9e9e9e");
//        FeedbackAPI. setUICustomInfo(fbSetting);
//    }

    //获取Context
    public static Context getContext() {
        return context;
    }

    //刷新时间
    public static void flushTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }
}
