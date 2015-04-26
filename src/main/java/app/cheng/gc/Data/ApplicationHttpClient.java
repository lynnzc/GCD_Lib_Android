package app.cheng.gc.Data;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 单例实现图书馆账号的登录
 * Created by lynnlyf on 2015/2/5.
 */
public class ApplicationHttpClient {
    private static HttpClient client;

    public ApplicationHttpClient() {

    }

    public static HttpClient getHttpClient() {
        if(client == null) {
            client = new DefaultHttpClient();
        }
        return client;
    }
}
