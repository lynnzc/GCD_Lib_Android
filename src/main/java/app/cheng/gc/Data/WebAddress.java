package app.cheng.gc.Data;

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

    public final static String USER_INFO_TRANS = "stu_info"; //用于传递用户信息
    public final static String USER_TRANS = "user_info";
    public final static String COOKIE_STORE = "cookie_store"; //cookie
    public static Boolean state = false; //登录状态

}
