package weimiao.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import weimiao.coolweather.R;
import weimiao.coolweather.util.HttpCallbackLinster;
import weimiao.coolweather.util.HttpUtil;
import weimiao.coolweather.util.Utility;

public class LookWeather extends Activity implements View.OnClickListener{
    private TextView title;
    private TextView punish_time;
    private TextView data;
    private TextView detail;
    private  TextView low_temp;
    private TextView high_temp;
    private Button switch_bt;
    private Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cheeked_weather);
        findviews();
        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode))
        {
            Log.i("countyCode",countyCode);
           queryWeatherCode(countyCode);
        }
        else
        {
            showWeather();
        }
        switch_bt.setOnClickListener(this);
        refresh.setOnClickListener(this);
    }
    private void queryWeatherCode(String countyCode)
    {
       String address = "http://www.weather.com.cn/data/list3/city" + countyCode +".xml";
        queryFromService(address, "countyCode");
    }
    private void queryInfo(String weatherCode)
    {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode +".html";
        queryFromService(address,"weatherCode");
    }
    private void queryFromService(final String address, final String type)
    {
        HttpUtil.sendRequstWithURLConnection(address, new HttpCallbackLinster() {
            @Override
            public void onFinish(String response) {
                if("countyCode".equals(type))
                {
                    if(!TextUtils.isEmpty(response))
                    {
                        String[] array = response.split("\\|");
                        if(array != null && array.length == 2)
                        {
                            String weatherCode = array[1];

                            queryInfo(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(type))
                {

                    Utility.handleWeatherResorce(LookWeather.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        punish_time.setText("更新失败");

                    }
                });
            }
        });
    }
    private void showWeather()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(LookWeather.this);
        title.setText(pref.getString("city_name",""));
        punish_time.setText("今天"+pref.getString("punish_time","")+"发布");
        data.setText(pref.getString("data",""));
        detail.setText(pref.getString("weather",""));
        low_temp.setText(pref.getString("low_temp",""));
        high_temp.setText(pref.getString("high_temp",""));
    }
    private void findviews()
    {
        title = (TextView)findViewById(R.id.weather_title);
        punish_time = (TextView)findViewById(R.id.punish_time);
        data = (TextView)findViewById(R.id.data);
        detail = (TextView)findViewById(R.id.weather_detail);
        low_temp = (TextView)findViewById(R.id.low_temp);
        high_temp = (TextView)findViewById(R.id.high_temp);
        switch_bt = (Button)findViewById(R.id.switch_bt);
        refresh = (Button)findViewById(R.id.refresh);
    }

    @Override
    public void onClick(View v) {
    switch (v.getId())
    {
        case R.id.switch_bt:
            Intent intent = new Intent(LookWeather.this,ChooseArea.class);
            intent.putExtra("from_weather_activity", true);
            startActivity(intent);
            finish();
            break;
        case R.id.refresh:
            title.setText("正在同步中...");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String weatherCode = sp.getString("city_code","");
            if(! TextUtils.isEmpty(weatherCode))
            {
                queryInfo(weatherCode);
            }
            break;
        default:
            break;
    }
    }
}
