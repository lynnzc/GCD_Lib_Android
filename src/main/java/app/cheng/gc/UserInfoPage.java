package app.cheng.gc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import app.cheng.gc.Data.StudentInfo;
import app.cheng.gc.Data.WebAddress;

/**
 * Created by lynnlyf on 2015/2/4.
 */
public class UserInfoPage extends ActionBarActivity {
    private StudentInfo stu_info;
    private TextView stu_name, stu_cardnumber, stu_type,
            stu_college, stu_borrowstate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfolayout);

        stu_cardnumber = (TextView)findViewById(R.id.user_cardname_text);
        stu_name = (TextView)findViewById(R.id.user_name_text);
        stu_type = (TextView)findViewById(R.id.user_type_text);
        stu_college = (TextView)findViewById(R.id.user_college_text);
        stu_borrowstate = (TextView)findViewById(R.id.user_borrowstate_text);

        Intent intent = getIntent();
        if(intent != null) {
            stu_info = (StudentInfo)intent.getSerializableExtra(WebAddress.USER_TRANS);
            if(stu_info != null) {
                stu_cardnumber.setText(stu_info.getCardNumber());
                stu_name.setText(stu_info.getName());
                stu_type.setText(stu_info.getType());
                stu_college.setText(stu_info.getCollege());
                stu_borrowstate.setText(stu_info.getBorrowState());
            }
        }

        initActionBar();
    }

    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();

        // 可以自定义actionbar
        actionbar.setCustomView(R.layout.actionbar_view_user);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setDisplayShowCustomEnabled(true);

        // 设置Actionbar背景
        actionbar.setBackgroundDrawable(
                getResources().getDrawable(R.drawable.actionbar_bg));
    }
}
