package app.cheng.gc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.CookieStore;

import app.cheng.gc.Data.StudentInfo;
import app.cheng.gc.Data.WebAddress;


/**
 * Created by lynnlyf on 2015/2/2.
 */
public class Login extends BaseActivity {
    private final static String SAVE_PASSWORD = "app.cheng.gc.save_password";
    private final static int MSG_SUCCESS = 1; //获取验证码成功
    private final static int MSG_FAILURE = 0; //获取验证码失败
    //账号，密码输入框
    private EditText username, password;
    //记住密码选择框
    private CheckBox rem_pw;
    //验证码输入框
    private EditText codeinput;
    //验证码显示
    private ImageView checkcode;
    //登陆按钮
    private Button login_btn;
    //actionBar组件
    //标题
    private TextView title;
    //回退按钮
    private ImageButton back_btn;

    //记录输入值
    private String userValue, passValue, checkcodeValue;
    //记录账号密码
    //key-value
    private SharedPreferences sp;

    //checkcode image
    private byte[] imagedata = null;

    private ClientAPI client = new ClientAPI();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginlayout);

        username = (EditText)findViewById(R.id.user_login);
        password = (EditText)findViewById(R.id.password_login);
        rem_pw = (CheckBox)findViewById(R.id.r_pw);
        login_btn = (Button)findViewById(R.id.button_login);

        codeinput = (EditText)findViewById(R.id.codeinput);
        checkcode = (ImageView)findViewById(R.id.checkcode);

        initActionBar();

        new Thread(runnable) {
        }.start();

        checkcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击验证码图片，重新获取验证码
                new Thread(runnable) {
                }.start();
            }
        });

        sp = this.getSharedPreferences(SAVE_PASSWORD, Context.MODE_APPEND);
        if(sp.getBoolean("ISCHECK", true)) {
            rem_pw.setChecked(true);
            username.setText(sp.getString("username", ""));
            password.setText(sp.getString("password", ""));
        }

        //登陆监听
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userValue = username.getText().toString().trim();
                passValue = password.getText().toString().trim();
                checkcodeValue = codeinput.getText().toString().trim();

                if (userValue.equals("") || passValue.equals("")) {
                    Toast.makeText(Login.this, "请输入密码或账号", Toast.LENGTH_LONG).show();
                } else if (checkcodeValue.equals("")) {
                    Toast.makeText(Login.this, "请输入验证码", Toast.LENGTH_LONG).show();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            StudentInfo stu_info = new StudentInfo();
                            client.setParams(userValue, passValue, checkcodeValue);
                            //client.Get(); //获取cookie
                            int statue = client.Login();

                            if (statue == 1) {

                                stu_info = client.GetUser(); //获得用户

                                if (rem_pw.isChecked()) {
                                    //记住用户密码
                                    SharedPreferences.Editor edit = sp.edit();
                                    edit.putString("username", userValue);
                                    edit.putString("password", passValue);
                                    edit.commit();
                                } else {
                                    sp.edit().clear();
                                }

                                Intent intent = new Intent(Login.this, TitleActionBar.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(WebAddress.USER_INFO_TRANS, stu_info);
                                WebAddress.state = true;
                                intent.putExtras(bundle); //绑定
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                            else {
                                mHandler.obtainMessage(statue).sendToTarget(); //获取失败
                            }

                            Looper.prepare();

                        }
                    }.start();

                    Looper.loop();
                }
            }
        });

        //监听记住密码选框
        rem_pw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(rem_pw.isChecked()) {
                    //System.out.println("记住密码已选");
                    sp.edit().putBoolean("ISCHECK", true).commit();
                }
                else {
                    //System.out.println("记住密码没有选中");
                    sp.edit().putBoolean("ISCHECK", false).commit();
                }
            }

        });

    }

    Runnable runnable = new Runnable() {
        public void run() {
            try {
                if(!WebAddress.isCookieGet) {
                    WebAddress.state = true;
                    client.Get(); //如果没有获取cookie,先获取网页cookie
                }

                imagedata = client.GetCode(); //获得验证码
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        imagedata, 0, imagedata.length);

                mHandler.obtainMessage(MSG_SUCCESS, bitmap).sendToTarget(); //获取成功
            }
            catch(Exception e) {
                mHandler.obtainMessage(MSG_FAILURE).sendToTarget(); //获取失败
                e.printStackTrace();
            }
        }
    };

    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();

        // 可以自定义actionbar
        actionbar.setCustomView(R.layout.actionbar_view_user);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setDisplayShowCustomEnabled(true);

        title = (TextView)actionbar.getCustomView().findViewById(R.id.title);
        title.setText("登陆");

        back_btn = (ImageButton)actionbar.getCustomView().findViewById(R.id.left_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }); //backspace

        // 设置Actionbar背景
        actionbar.setBackgroundDrawable(
                getResources().getDrawable(R.drawable.actionbar_bg));
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_SUCCESS:
                    checkcode.setImageBitmap((Bitmap)msg.obj); //显示出验证码
                    Toast.makeText(getApplicationContext(),
                            "获取验证码成功", Toast.LENGTH_LONG).show();
                    break;
                case MSG_FAILURE:
                    Toast.makeText(getApplicationContext(),
                            "获取验证码失败", Toast.LENGTH_LONG).show();
                    break;
                case -1:
                    Toast.makeText(getApplicationContext(),
                            "输入有误", Toast.LENGTH_LONG).show();
                    break;
                case 10:
                    Toast.makeText(getApplicationContext(),
                            "获取失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        this.finish();
        super.onStop();
    }
}
