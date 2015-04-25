package app.cheng.gc;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;

import app.cheng.gc.Data.ApplicationHttpClient;

/**
 * Created by lynnlyf on 2015/4/25.
 */
public class ApplicationStack extends Application {
    private ArrayList<Activity>  activity_stack = new ArrayList<Activity>();
    private static ApplicationStack instance;

    public ApplicationStack() {

    }

    //单例实现activity stack
    public static ApplicationStack getInstance() {
        if(instance == null) {
            instance = new ApplicationStack();
        }
        return instance;
    }

    //activity stack中添加activity
    public void addActivity(Activity activity) {
        activity_stack.add(activity);
    }

    //退出所有activity
    public void exit() {
        try {
            for (Activity activity : activity_stack) {
                activity.finish();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            //退出程序
            System.exit(0);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
