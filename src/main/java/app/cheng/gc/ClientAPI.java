package app.cheng.gc;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import app.cheng.gc.Data.ApplicationHttpClient;
import app.cheng.gc.Data.BookInfo;
import app.cheng.gc.Data.BorrowedBookInfo;
import app.cheng.gc.Data.SearchBookInfo;
import app.cheng.gc.Data.StudentInfo;
import app.cheng.gc.Data.WebAddress;

/**
 * Created by lynnlyf on 2015/2/2.
 */

public class ClientAPI {
    private String username;
    private String password;
    private String checkcode;

    private List<Cookie> list_cookie;
    private static CookieStore cookiestore;

    private int statue;

    private String result;

    private int historypage = 1;
    private int bookmarkpage = 1;

    private int n = 1; // searchpage count

    private String method;
    private String word;

    //form信息
    private static String __VIEWSTATE;
    private static String __EVENTVALIDATION;

    private HttpClient hc = new DefaultHttpClient(); //主要
    public ApplicationHttpClient app_client;

    public ClientAPI(String username, String password) {
        setHttpClient();
        this.username = username;
        this.password = password;
    }

    public ClientAPI() {
        setHttpClient();
    }

    public void setHttpClient() {
        hc = app_client.getHttpClient();
    }

    public void setParams(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setParams(String username, String password, String checkcode) {
        this.username = username;
        this.password = password;
        this.checkcode = checkcode;
    }

    public void getCookieStore() {
        cookiestore = ((AbstractHttpClient) hc).getCookieStore(); //网页Cookie
    }

    public void setCookieStore() {
        ((AbstractHttpClient) hc).getCookieStore().clear();

        ((AbstractHttpClient) hc).setCookieStore(cookiestore);
        System.out.println( ((AbstractHttpClient) hc).getCookieStore().getCookies() + "/cookie");
    }

    public byte[] GetCode() {
        //HttpClient hc = new DefaultHttpClient();
        byte[] data = new byte[1024];
        byte[] imagedata = null;
        //验证码图片源

        try {
            HttpGet get = new HttpGet("http://lib.gdufe.edu.cn/OPAC/gencheckcode.aspx");
            get.addHeader("Accept", "text/html, application/xhtml+xml, */*");
            get.addHeader("Connection", "Keep-Alive");
            get.addHeader("Host", "lib.gdufe.edu.cn");
            get.addHeader("Accept-Encoding", "gzip, deflate");
            get.addHeader("Accept-Language", "zh-CN");
            //get.addHeader("Cookie", cookie_id);

            HttpResponse response = hc.execute(get);
            System.out.println(response.getStatusLine().getStatusCode() + " /code");
            if(response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                InputStream input = entity.getContent();
                int read = 0;
                while((read = input.read(data)) != -1) {
                    outputStream.write(data, 0, read); //写入内存
                }
                imagedata = outputStream.toByteArray();

                input.close();

                return imagedata;
            }
            else {
                System.out.println("获取验证码失败//");
            }
        }catch(ClientProtocolException e){
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void Get() {
        //获得登陆页

        HttpGet get = new HttpGet(WebAddress.LOGIN_PAGE);

        get.addHeader("Accept", "text/html, application/xhtml+xml, */*");
        get.addHeader("Connection", "Keep-Alive");
        get.addHeader("Host", "lib.gdufe.edu.cn");
        get.addHeader("Accept-Encoding", "gzip, deflate");
        get.addHeader("Accept-Language", "zh-CN");

        try {
            HttpResponse hr = hc.execute(get);
            HttpEntity h_entity = hr.getEntity();

            result = EntityUtils.toString(h_entity); //结果页
            Utils.getViewState(result);

            __VIEWSTATE = Utils.get__VIEWSTATE();
            __EVENTVALIDATION = Utils.get__EVENTVALIDATION();

            //System.out.println(result + "/result 1  "); //测试

            System.out.println(__VIEWSTATE + "/ __VIEWSTATE"); //测试
            System.out.println(__EVENTVALIDATION + "/__EVENTVALIDATION"); //测试



        }
        catch(ClientProtocolException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public StudentInfo GetUser() {
        //HttpClient hc = new DefaultHttpClient();
        HttpGet get = new HttpGet(WebAddress.USER_INFO);
        addHttpGetHeader(get);
        StudentInfo sif = new StudentInfo();
        try {
            HttpResponse hr = hc.execute(get);
            HttpEntity h_entity = hr.getEntity();

            //System.out.println(h_entity + "/html"); //测试
            result = EntityUtils.toString(h_entity); //结果页
            //System.out.println(result + "/html"); //测试
            sif = Utils.getInfo(result);
        }
        catch(ClientProtocolException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return sif;
    }

    public HttpGet addHttpGetHeader(HttpGet get) {
        get.addHeader("Accept", "text/html, application/xhtml+xml, */*");
        get.addHeader("Connection", "Keep-Alive");
        get.addHeader("Cache-Control", "no-cache");
        get.addHeader("Host", "lib.gdufe.edu.cn");
        get.addHeader("Accept-Encoding", "gzip, deflate");
        get.addHeader("Accept-Language", "zh-CN");
        return get;
    }

    public int Login() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$txtUsername_Lib", username)); //登陆账户
        params.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$txtPas_Lib", password)); //密码
        params.add(new BasicNameValuePair("__EVENTARGUMENT", "")); //default
        params.add(new BasicNameValuePair("__EVENTTARGET", "")); //default
        params.add(new BasicNameValuePair("__LASTFOCUS", ""));
        params.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$txtCode", checkcode)); //验证码
        System.out.println(checkcode + "/checkcode"); //测试
        params.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$btnLogin_Lib", "登录")); //登录
        params.add(new BasicNameValuePair("ctl00$ContentPlaceHolder1$txtlogintype", "0"));
        params.add(new BasicNameValuePair("__EVENTVALIDATION", __EVENTVALIDATION));
        params.add(new BasicNameValuePair("__VIEWSTATE", __VIEWSTATE));

        try {
            UrlEncodedFormEntity url_entity;
            url_entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);

            HttpPost post = new HttpPost(WebAddress.LOGIN_PAGE); //传送登陆包

            post.addHeader("Referer", "http://lib.gdufe.edu.cn/opac/login.aspx");
            post.addHeader("Accept", "text/html, application/xhtml+xml, */*");
            post.addHeader("Connection", "Keep-Alive");
            post.addHeader("Cache-Control", "no-cache");
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            post.addHeader("Host", "lib.gdufe.edu.cn");
            post.addHeader("Accept-Encoding", "gzip, deflate");
            post.addHeader("Accept-Language", "zh-CN");

            post.setEntity(url_entity); //设置表单

            hc.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false); //禁止重定向
            HttpResponse he = hc.execute(post); //执行请求

            HttpEntity result_entity = he.getEntity(); //返回结果

            result = EntityUtils.toString(result_entity); //结果页
            //System.out.println(result + " /result 2  "); //测试

            statue = he.getStatusLine().getStatusCode(); //状态码,302为成功,500失败，200账号密码错误

            System.out.println(statue + " /statue"); //测试

            if(statue == 302) {
                System.out.println("成功登陆"); //测试

            }
            else if(statue == 200) {
                //输入有误，连接成功

                return -1;
            }
            else {
                System.out.println("不成功 " + result + "/result");
                return 10;
            }

        }
        catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch(ClientProtocolException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int modifyPassword(String old_pw, String new_pw, String repeat_pw) {
        HttpClient hc = new DefaultHttpClient();
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("__EVENTTARGET", ""));
        params.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
        params.add(new BasicNameValuePair("__VIEWSTATE", __VIEWSTATE));

        params.add(new BasicNameValuePair("__EVENTVALIDATION", __EVENTVALIDATION));
        params.add(new BasicNameValuePair("ctl00$cpRight$txtOldPass", old_pw));
        params.add(new BasicNameValuePair("ctl00$cpRight$txtNewPass", new_pw));
        params.add(new BasicNameValuePair("ctl00$cpRight$txtRepNewPass", repeat_pw));
        params.add(new BasicNameValuePair("ctl00$cpRight$btnSubmit", "提交"));

        HttpPost post = new HttpPost(WebAddress.MODIFY_PAS);
        UrlEncodedFormEntity entity;

        try {
            entity = new UrlEncodedFormEntity(params, "utf-8");


            post.addHeader("Accept", "text/html, application/xhtml+xml, */*");
            post.addHeader("Connection", "Keep-Alive");
            post.addHeader("Cache-Control", "no-cache");
            post.addHeader("Content-Type", "");
            post.addHeader("Accept-Encoding", "gzip, deflate");
            post.addHeader("Accept-Language", "zh-CN");
            post.addHeader("Host", "lib.gdufe.edu.cn");
            post.addHeader("Original", "http://lib.gdufe.edu.cn");
            post.setEntity(entity);

            HttpResponse h_re = hc.execute(post);

            return h_re.getStatusLine().getStatusCode();
        }
        catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch(ClientProtocolException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<BorrowedBookInfo> getBorrowedContent() {
        HttpGet get = new HttpGet(WebAddress.BOOKBORROWED);
        addHttpGetHeader(get);

        try {
            HttpResponse h_re = hc.execute(get);
            HttpEntity entity = h_re.getEntity();
            return Utils.getBorrowed(EntityUtils.toString(entity));
        }
        catch(ClientProtocolException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BorrowedBookInfo> getBorrowedHistory() {
        HttpGet get = new HttpGet(WebAddress.BOOKBORROWEDHISTORY + "?page=" + historypage);
        addHttpGetHeader(get);
        try {
            HttpResponse hr = hc.execute(get);
            HttpEntity entity = hr.getEntity();
            String result = EntityUtils.toString(entity);

            return Utils.getHistory(result);
        }
        catch(ClientProtocolException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BorrowedBookInfo> nextHistoryPage() {
        if(historypage < Utils.getBorrowedMax()) {
            historypage++;
        }

        return getBorrowedHistory();
    }

    public List<BorrowedBookInfo> preHistroryPage() {
        if(historypage > 1) {
            historypage--;
        }
        return getBorrowedHistory();
    }

    public List<BorrowedBookInfo> getBookmarkInfo() {
        HttpGet get = new HttpGet(WebAddress.BOOKMARK  + "?page=" + bookmarkpage);
        addHttpGetHeader(get);
        try {
            HttpResponse hr = hc.execute(get);
            HttpEntity entity = hr.getEntity();
            String result = EntityUtils.toString(entity);

            return Utils.getBookmark(result);
        }
        catch(ClientProtocolException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BorrowedBookInfo> nextBookmarkPage() {
        if(bookmarkpage < Utils.getBorrowedMax()) {
            bookmarkpage++;
        }

        return getBookmarkInfo();
    }

    public List<BorrowedBookInfo> preBookmarkPage() {
        if(bookmarkpage > 1) {
            bookmarkpage--;
        }
        return getBookmarkInfo();
    }

    public List<BookInfo> searchBook(String method, String word) {
        return searchBook(method, word, 1); //first page
    }

    public List<BookInfo> searchBook(String method, String word, int page) {
        this.method = method;
        this.word = word;

        HttpClient hc = new DefaultHttpClient();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(this.method, this.word));
        params.add(new BasicNameValuePair("dt", "ALL"));
        params.add(new BasicNameValuePair("cl", "ALL"));
        params.add(new BasicNameValuePair("dp", "20")); //a page display the number of book
        params.add(new BasicNameValuePair("sf", "M_PUB_YEAR"));
        params.add(new BasicNameValuePair("ob", "DESC"));
        params.add(new BasicNameValuePair("sm", "table"));
        params.add(new BasicNameValuePair("dept", "ALL"));
        params.add(new BasicNameValuePair("page", page+"")); //n-th page
        URI uri;

        try {
            uri = URIUtils.createURI("http", WebAddress.HOST,
                    -1, "/searchresult.aspx", URLEncodedUtils.format(params, "GBK"), null);
            HttpGet get = new HttpGet(uri);
            //System.out.println(get.getURI());//测试
            addHttpGetHeader(get);
            HttpResponse hr = hc.execute(get);
            HttpEntity entity = hr.getEntity();
            //System.out.println(EntityUtils.toString(entity)); //测试
            return Utils.getSearch(EntityUtils.toString(entity));
        }
        catch(ClientProtocolException e) {
            e.printStackTrace();
        }
        catch(URISyntaxException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<SearchBookInfo> SearchBookInfo(String num) {
        HttpClient hc = new DefaultHttpClient();
        HttpGet get = new HttpGet(WebAddress.SEARCHBOOK_INFO + num);
        addHttpGetHeader(get);
        try {
            HttpResponse hr = hc.execute(get);
            HttpEntity entity = hr.getEntity();

            return Utils.getSearchBookInfo(EntityUtils.toString(entity));
        }
        catch(ClientProtocolException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
