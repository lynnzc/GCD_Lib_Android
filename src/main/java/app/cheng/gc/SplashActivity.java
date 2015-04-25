package app.cheng.gc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import app.cheng.gc.Data.WebAddress;


/**
* Created by lynnlyf on 2015/2/2.
*/
public class SplashActivity extends BaseActivity {
     private static final int SHOW_TIME = 2000;
     private ClientAPI client = new ClientAPI();
     //开机动画
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.jiemian);
         getSupportActionBar().hide();

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
                         //跳转
                         overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                     }
             }, SHOW_TIME);
     }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }
}
