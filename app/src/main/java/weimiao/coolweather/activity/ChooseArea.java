package weimiao.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import weimiao.coolweather.R;
import weimiao.coolweather.db.CoolWeatherDB;
import weimiao.coolweather.model.City;
import weimiao.coolweather.model.County;
import weimiao.coolweather.model.Province;
import weimiao.coolweather.util.HttpCallbackLinster;
import weimiao.coolweather.util.HttpUtil;
import weimiao.coolweather.util.Utility;

public class ChooseArea extends Activity {
    public  static  final  int LEVEL_PROVINCE = 0; //省级
    public  static  final  int LEVEL_CITY = 1; //市级
    public  static  final  int LEVEL_COUNTY = 2; //县级
    private ProgressDialog progressDialog;
    private ListView listView; //显示省市县的数据
    private TextView title_text; //标题内容
    private ArrayAdapter<String> adapter;  //listView 的适配器
    private List<String> datalist = new ArrayList<String>();
    private List<Province> provinceList; //省列表
    private List<City> cityList; //市列表
    private List<County> countyList; //县列表
    private CoolWeatherDB coolWeatherDB;
    private Province selectProvince; //选中的省
    private City selectCity; //选中的市
    private  int currentLevel; //选中的县
    private Boolean isFromWeatherActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ChooseArea.this);
        if(pref.getBoolean("city_cheeked",false)&& !isFromWeatherActivity )
        {
            Intent intent = new Intent(this,LookWeather.class);
            startActivity(intent);
            finish();
        }
        listView = (ListView)findViewById(R.id.list_view);
        title_text = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(ChooseArea.this,android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE)
                {
                    selectProvince = provinceList.get(position);
                    querCity(); //选中省，则查找该省的市
                }
                else if(currentLevel == LEVEL_CITY) {
                    selectCity = cityList.get(position);
                    querCounty(); //选中市，则查找该市的县
                }
                else if(currentLevel == LEVEL_COUNTY) //选中县，则查找该县所对应的天气
                {
                    String countyCode = countyList.get(position).getCountycode();
                    Intent intent = new Intent(ChooseArea.this,LookWeather.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        querProvince();
    }
    //查找省，如果在数据库中没有找到，则在服务器上找
    private  void querProvince()
    {
      provinceList = coolWeatherDB.loadProvince();
        if(provinceList.size() > 0)
        {
            datalist.clear();
            for(Province province : provinceList)
            {
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            title_text.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }
        else
        {
            //在服务器上查找
            querfromServer(null, "province");
        }
    }
    //查找市，如果在数据库中没有找到，则在服务器上找
    private  void  querCity()
    {
        cityList = coolWeatherDB.loadCity(selectProvince.getId());
        if(cityList.size() > 0)
        {
            datalist.clear();
            for(City city : cityList)
            {
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            title_text.setText(selectProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }
        else
        {
            querfromServer(selectProvince.getProvinceCode(),"city");
        }
    }
    //查找县，如果在数据库中没有找到，则在服务器上找
    private  void  querCounty()
    {
        countyList = coolWeatherDB.loadCounty(selectCity.getId());
        if(countyList.size() > 0)
        {
            datalist.clear();
            for(County county : countyList)
            {
                datalist.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            title_text.setText(selectCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }
        else
        {
            querfromServer(selectCity.getCityCode(),"county");
        }
    }
    //根据传入的代号和类型在服务器上查找数据
    private void  querfromServer(final String code,final  String type)
    {
        String address;
        if(!TextUtils.isEmpty(code))
        {
            address = "http://www.weather.com.cn/data/list3/city" + code +".xml";
        }
        else
        {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendRequstWithURLConnection(address, new HttpCallbackLinster() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvince(coolWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCity(coolWeatherDB, response, selectProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCounty(coolWeatherDB, response, selectCity.getId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                querProvince();
                            } else if ("city".equals(type)) {
                                querCity();
                            } else if ("county".equals(type)) {
                                querCounty();
                            }
                        }
                    });
                }
            }
                public void  onError(Exception e)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(ChooseArea.this,"加载失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
        });

    }
    private void  showProgressDialog()
    {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(ChooseArea.this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();

    }
    private  void  closeProgressDialog()
    {
        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }
    //捕获返回键，来判断此时应直接返回省，市列表还是返回到显示天气的页面
    public  void onBackPressed()
    {
        if(currentLevel == LEVEL_COUNTY)
        {
            querCity();
        }else if(currentLevel == LEVEL_CITY) {
            querProvince();
        }else {
            if (isFromWeatherActivity) {
                Intent intent = new Intent(ChooseArea.this, LookWeather.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
