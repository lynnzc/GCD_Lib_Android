package app.cheng.gc.PullTool;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by lynnlyf on 2015/2/2.
 */
public class PullableListView extends ListView implements Pullable {

    //构造方法
    public PullableListView(Context context)
    {
        super(context);
    }

    public PullableListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PullableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*实例接口方法,上拉加载*/
    @Override
    public boolean canPullUp() {
        if (getCount() == 0) {
            // 没有item的时候设置不可以上拉加载
            return false;
        } else if (getLastVisiblePosition() == (getCount() - 1)) {
            // 滑到底部了
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
                    && getChildAt(
                    getLastVisiblePosition()
                            - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }
}

