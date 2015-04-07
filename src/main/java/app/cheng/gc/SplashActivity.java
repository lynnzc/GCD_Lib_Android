package app.cheng.gc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import app.cheng.gc.Data.WebAddress;


/**
* Created by lynnlyf on 2015/2/2.
*/
public class SplashActivity extends Activity {
     private static final int SHOW_TIME = 2000;
     private ClientAPI client = new ClientAPI();
     //开机动画
     @Override
     protected void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);
             //去标题
             this.requestWindowFeature(Window.FEATURE_NO_TITLE);
             setContentView(R.layout.jiemian);

             WebAddress.checkNetwork(this);
             new Thread() {
                 public void run() {
                     client.Get(); //获得网页cookie
                 }
             }.start();
             new Handler().postDelayed(new Runnable() {
                     @Override
                     public void run() {

                             Intent intent = new Intent();
                             intent.setClass(SplashActivity.this, TitleActionBar.class);
                             startActivity(intent);
                             finish();
                             //跳转
                             overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                         }
                 }, SHOW_TIME);
         }
}
