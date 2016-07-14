package weimiao.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import weimiao.coolweather.db.CoolWeatherDB;
import weimiao.coolweather.model.City;
import weimiao.coolweather.model.County;
import weimiao.coolweather.model.Province;

/**
 * Created by tianzhao on 2016/5/5.
 */
public class Utility {
    //解析Province返回数据
    public synchronized   static   boolean handleProvince(CoolWeatherDB coolWeatherDB,String response)
    {
        if(!TextUtils.isEmpty(response))
        {
            String allProvince[] = response.split(",");
            if(allProvince != null && allProvince.length > 0)
            {
                for(String p : allProvince)
                {
                    String array[] = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceName(array[1]);
                    province.setProvinceCode(array[0]);
                    coolWeatherDB.saveProvince(province);
                }
                return  true;
            }

        }
        return false;
    }
    //解析City返回数据
    public synchronized  static boolean handleCity(CoolWeatherDB coolWeatherDB,String response,int provinceId)
    {
        if(!TextUtils.isEmpty(response))
        {
            String allCity[] = response.split(",");
            if(allCity != null && allCity.length > 0)
            {
                for(String p : allCity)
                {
                    String array[] = p.split("\\|");
                    City city = new City();
                    city.setCityName(array[1]);
                    city.setCityCode(array[0]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return  true;
            }

        }
        return  false;
    }
    //解析County返回数据
    public synchronized  static boolean handleCounty(CoolWeatherDB coolWeatherDB,String response,int cityId)
    {
        if(!TextUtils.isEmpty(response))
        {
            String allCounty[] = response.split(",");
            if(allCounty != null && allCounty.length > 0)
            {
                for (String p : allCounty) {
                    String array[] = p.split("\\|");
                    County county = new County();
                    county.setCountycode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }

        }
        return  false;
    }
    //解析返回的json数据
    public static  void handleWeatherResorce(Context context,String response)
    {
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherinfo.getString("city");
            String cityCode = weatherinfo.getString("cityid");
            String low_temp = weatherinfo.getString("temp1");
            String high_temp = weatherinfo.getString("temp2");
            String weather_detail = weatherinfo.getString("weather");
            String punish_time = weatherinfo.getString("ptime");
            saveWeatherInfo(context, cityName, cityCode, low_temp, high_temp, weather_detail, punish_time);
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public  static  void saveWeatherInfo(Context context,String cityName,String cityCode,String temp1,String temp2,String weather,String punish_time)
    {
        SimpleDateFormat sd = new SimpleDateFormat("yyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_cheeked",true);
        editor.putString("city_name", cityName);
        editor.putString("city_code", cityCode);
        editor.putString("low_temp", temp1);
        editor.putString("high_temp", temp2);
        editor.putString("weather", weather);
        editor.putString("punish_time", punish_time);
        editor.putString("data",sd.format(new Date()));
        editor.commit();
    }
}
