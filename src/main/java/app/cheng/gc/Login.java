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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.client.CookieStore;

import app.cheng.gc.Data.StudentInfo;
import app.cheng.gc.Data.WebAddress;


/**
 * Created by lynnlyf on 2015/2/2.
 */
public class Login extends Activity {
    private EditText username, password;
    private CheckBox rem_pw;
    private String userValue, passValue, checkcodeValue;
    private SharedPreferences sp;

    private EditText codeinput;
    private ImageView checkcode;
    private Button login_btn;

    private byte[] imagedata = null; //checkcode image

    private ClientAPI client = new ClientAPI();

    private final static int MSG_SUCCESS = 1; //获取验证码成功
    private final static int MSG_FAILURE = 0; //获取验证码失败

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //去标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loginlayout);

        sp = this.getSharedPreferences("cookie", Context.MODE_APPEND);

        username = (EditText)findViewById(R.id.user_login);
        password = (EditText)findViewById(R.id.password_login);
        rem_pw = (CheckBox)findViewById(R.id.r_pw);
        login_btn = (Button)findViewById(R.id.button_login);

        codeinput = (EditText)findViewById(R.id.codeinput);
        checkcode = (ImageView)findViewById(R.id.checkcode);

        new Thread(runnable) {
        }.start();

        if(sp.getBoolean("ISCHECK", true)) {
            rem_pw.setChecked(true);
            username.setText(sp.getString("username", userValue));
            password.setText(sp.getString("password", passValue));
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
                                    SharedPreferences sp = getSharedPreferences("client", MODE_APPEND);
                                    SharedPreferences.Editor edit = sp.edit();
                                    edit.putString("username", userValue);
                                    edit.putString("password", passValue);
                                    edit.commit();

                                } else {
                                    SharedPreferences sp = getSharedPreferences("client", MODE_APPEND);
                                    sp.edit().clear();
                                }

                                System.out.println(stu_info.getName()+ "/姓名");
                                Intent intent = new Intent(Login.this, TitleActionBar.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(WebAddress.USER_INFO_TRANS, stu_info);
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
                    System.out.println("记住密码已选");
                    sp.edit().putBoolean("ISCHECK", true).commit();
                }
                else {
                    System.out.println("记住密码没有选中");
                    sp.edit().putBoolean("ISCHECK", false).commit();
                }
            }

        });

        checkcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(runnable) {
                }.start();
            }
        });

    }

    Runnable runnable = new Runnable() {
        public void run() {
            try {
                //client.Get(); //取得cookie

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
