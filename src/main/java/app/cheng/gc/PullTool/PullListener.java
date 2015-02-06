package app.cheng.gc.PullTool;

import android.os.Handler;
import android.os.Message;

/**
 * Created by lynnlyf on 2015/2/2.
 */
public class PullListener implements PulltorefreshLayout.OnRefreshListener {
    @Override
    public void onLoadMore(final PulltorefreshLayout pulltorefreshLayout) {
        // 加载操作
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 告诉控件加载完毕
                pulltorefreshLayout.loadmoreFinish(PulltorefreshLayout.SUCCEED);
            }
        }.sendEmptyMessageDelayed(0, 5000);
    }
}