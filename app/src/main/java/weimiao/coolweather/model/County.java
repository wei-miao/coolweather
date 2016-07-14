package weimiao.coolweather.model;

/**
 * Created by tianzhao on 2016/5/3.
 */
public class County {
    public  int id;
    public String countyName;
    public String countycode;
    public int cityId;

    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }

    public  String getCountyName()
    {
        return countyName;
    }
    public  void setCountyName(String countyName)
    {
        this.countyName = countyName;
    }

    public  String getCountycode()
    {
        return countycode;
    }
    public  void setCountycode(String countycode)
    {
        this.countycode = countycode;
    }

    public  int getCityId()
    {
        return cityId;
    }
    public  void setCityId(int cityId)
    {
        this.cityId = cityId;
    }
}
