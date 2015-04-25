package app.cheng.gc;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by lynnlyf on 2015/4/25.
 */
public class BaseActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationStack.getInstance().addActivity(this);
    }
}
