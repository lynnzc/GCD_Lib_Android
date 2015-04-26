package app.cheng.gc.Data;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import app.cheng.gc.ClientAPI;

/**
 * Created by lynnlyf on 2015/2/2.
 */
public class WebAddress {
    public final static String HOST = "lib.gdufe.edu.cn/opac";
    public final static String LOGIN_PAGE = "http://lib.gdufe.edu.cn/opac/login.aspx";
    //public final static String LOGIN = "http://lib.gdufe.edu.cn/OPAC/internal_login.aspx";
    //public final static String LOGIN_URL = "http://lib.gdufe.edu.cn/OPAC/login.aspx?ReturnUrl=%2fOPAC%2fuser%2fuserinfo.aspx";
    public final static String SEARCH_PAGE = "http://lib.gdufe.edu.cn/opac/search.aspx";
    public final static String SEARCHBOOK_INFO = "http://lib.gdufe.edu.cn/opac/bookinfo.aspx?ctrlno=";
    public final static String USER_INFO = "http://lib.gdufe.edu.cn/opac/user/userinfo.aspx";
    public final static String MODIFY_PAS = "http://lib.gdufe.edu.cn/opac/changepas.aspx";
    public final static String BOOKBORROWED = "http://lib.gdufe.edu.cn/opac/user/bookborrowed.aspx";
    public final static String BOOKBORROWEDHISTORY = "http://lib.gdufe.edu.cn/opac/user/bookborrowedhistory.aspx";
    public final static String BOOKMARK = "http://lib.gdufe.edu.cn/opac/user/mybookshelf.aspx";
    //用于传递数据的Key
    public final static String USER_INFO_TRANS = "stu_info"; //用于传递用户信息
    public final static String USER_TRANS = "user_info";
    //public final static String COOKIE_STORE = "cookie_store"; //cookie

    public static Boolean state = false; //登录状态
    public static Boolean isCookieGet = false; //是否获取了cookie

    //检查网络连接状态
    private static boolean isOpenNetwork(final Activity activity) {
        Context context = activity.getApplicationContext();
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    public static void checkNetwork(final Activity activity) {
        if(!isOpenNetwork(activity)) {
            Toast.makeText(activity,
                    "当前网络网络异常，请检查网络设置", Toast.LENGTH_LONG).show();
        }
    }
}
