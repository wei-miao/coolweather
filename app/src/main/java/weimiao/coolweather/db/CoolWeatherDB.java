package weimiao.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import weimiao.coolweather.model.City;
import weimiao.coolweather.model.County;
import weimiao.coolweather.model.Province;

public class CoolWeatherDB {
    public static  final String DB_NAME = "cool_weather";
    public  static final int VERSON = 1;
    public  static CoolWeatherDB coolWeatherDB;
    public SQLiteDatabase db;

    //CoolWeatherDB实例化
    private CoolWeatherDB(Context context)
    {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSON);
        db = dbHelper.getWritableDatabase();
    }
    //获取CoolWeather的实例化对象
    public synchronized  static CoolWeatherDB getInstance(Context context)
    {
        if(coolWeatherDB == null)
        {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    //存储Province信息到数据库
    public  void saveProvince(Province province)
    {
        if(province != null)
        {
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }
    //从数据库中读取Province信息
    public List<Province> loadProvince()
    {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst())
        {
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while(cursor.moveToNext());
        }
        if(cursor != null)
        {
            cursor.close();
        }
        return list;
    }

    //存储city信息到数据库
    public void saveCity(City city)
    {
        if(city != null)
        {
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City", null, values);
        }
    }
    //从数据库中读取City信息
    public List<City> loadCity(int provinceId)
    {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City",null,"province_id = ? ",new String[]{String.valueOf(provinceId)},null,null,null);
        if(cursor.moveToFirst())
        {
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            }while (cursor.moveToNext());

        }
        if (cursor != null)
        {
            cursor.close();
        }
        return list;
    }

    //保存County到数据库
    public void saveCounty(County county)
    {
        if(county != null)
        {
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountycode());
            values.put("city_id",county.getCityId());
            db.insert("County",null,values);
        }
    }
    //从数据库中读取county
    public  List<County> loadCounty(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_id = ? ", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountycode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(county);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
