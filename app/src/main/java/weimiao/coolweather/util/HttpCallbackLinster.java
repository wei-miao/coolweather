package weimiao.coolweather.util;

public interface HttpCallbackLinster {
    void onFinish(String response);
    void onError(Exception e);
}
