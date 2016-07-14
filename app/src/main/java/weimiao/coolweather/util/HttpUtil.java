package weimiao.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tianzhao on 2016/5/5.
 */
public class HttpUtil {
    //向服务器请求数据
    public  static void sendRequstWithURLConnection(final String address,final HttpCallbackLinster linster)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while((line =reader.readLine() )!= null)
                    {
                        response.append(line);
                    }
                    if(linster != null)
                    {
                        linster.onFinish(response.toString());
                    }
                }catch (Exception e)
                {
                    if(linster != null)
                    {
                        linster.onError(e);
                    }
                }finally {
                    if(connection != null)
                    {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
