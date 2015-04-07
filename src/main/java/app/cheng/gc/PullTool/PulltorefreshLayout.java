package app.cheng.gc.PullTool;

/**
 * Created by lynnlyf on 2015/2/2.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import app.cheng.gc.R;

/**
 * 自定义的布局，管理子控件，
 * 一个是上拉头，
 * 一个是包含内容的pullableView（可以是实现Pullable接口的的任何View）
 * Created by lynnlyf on 2015/1/10.
 */

public class PulltorefreshLayout extends RelativeLayout {
    // 初始状态
    public static final int INIT = 0;
    // 释放加载
    public static final int RELEASE_TO_LOAD = 3;
    // 正在加载
    public static final int LOADING = 4;
    // 操作完毕
    public static final int DONE = 5;

    // 当前状态,开始为初始状态
    private int state = INIT;

    // 刷新回调接口
    private OnRefreshListener mListener;
    //加载刷新成功
    public static final int SUCCEED = 0;
    //加载刷新失败
    public static final int FAIL = 1;

    // 按下Y坐标，上一个事件点Y坐标
    private float downY, lastY;

    // 上拉的距离
    private float pullUpY = 0;
    // 释放加载的距离
    private float loadmoreDist = 200;

    private MyTimer timer;
    // 回滚速度
    public float MOVE_SPEED = 8;
    // 第一次执行布局
    private boolean isLayout = false;
    // 在刷新过程中滑动操作
    private boolean isTouch = false;
    // 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化
    private float radio = 2;

    // 上拉头
    private View loadmoreView;
    // 上拉的箭头
    private View pullUpView;
    // 正在加载的图标
    private View loadingView;
    // 加载结果图标
    private View loadStateImageView;
    // 加载结果：成功或失败
    private TextView loadStateTextView;

    // 实现了Pullable接口的View
    private View pullableView;
    // 过滤多点触碰
    private int mEvents;
    //控制是否可以上拉
    private boolean canPullUp = true;

    // 箭头的转180°动画
    private RotateAnimation rotateAnimation;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;

    //构造方法
    public PulltorefreshLayout(Context context) {
        super(context);
        initView(context);
    }

    public PulltorefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PulltorefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            MOVE_SPEED = (float) (8 + 5 * Math.tan(Math.PI / 2
                    / getMeasuredHeight() * (Math.abs(pullUpY)))); //上拉回弹速度
            if (!isTouch) {
                //拉动
                if (state == LOADING && -pullUpY <= loadmoreDist) {
                    pullUpY = -loadmoreDist;
                    timer.cancel(); //清空
                }
            }

            if (pullUpY < 0) {
                pullUpY += MOVE_SPEED;
            }

            if (pullUpY > 0) {
                //结束拉动
                pullUpY = 0;

                pullUpView.clearAnimation();

                if (state != LOADING)
                    changeState(INIT);
                timer.cancel(); //清空
            }
            // 刷新布局,会自动调用onLayout
            requestLayout();
        }
    };

    //初始化布局
    private void initView() {
        // 初始化上拉布局
        pullUpView = loadmoreView.findViewById(R.id.pullup_icon);
        loadStateTextView = (TextView) loadmoreView
                .findViewById(R.id.loadstate_tv);
        loadingView = loadmoreView.findViewById(R.id.loading_icon);
        loadStateImageView = loadmoreView.findViewById(R.id.loadstate_iv);
    }

    private void initView(Context context) {
        timer = new MyTimer(updateHandler);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                context, R.anim.reverse_anim);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                context, R.anim.rotating);
        // 添加匀速转动动画
        LinearInterpolator lir = new LinearInterpolator();
        rotateAnimation.setInterpolator(lir);
        refreshingAnimation.setInterpolator(lir);
    }

    /**
     * 加载完毕，显示加载结果。
     * 注意：加载完成后一定要调用这个方法
     * @param refreshResult
     *            PullToRefreshLayout.SUCCEED代表成功，PullToRefreshLayout.FAIL代表失败
     */
    public void loadmoreFinish(int refreshResult) {
        loadingView.clearAnimation(); //清除加载动画
        loadingView.setVisibility(View.GONE); //加载中图片不可见
        switch (refreshResult) { //切换加载后状态
            case SUCCEED:
                // 加载成功
                loadStateImageView.setVisibility(View.VISIBLE);
                loadStateTextView.setText(R.string.load_succeed);
                loadStateImageView.setBackgroundResource(R.drawable.load_succeed);
                break;
            case FAIL:
            default:
                // 加载失败
                loadStateImageView.setVisibility(View.VISIBLE);
                loadStateTextView.setText(R.string.load_fail);
                loadStateImageView.setBackgroundResource(R.drawable.load_failed);
                break;
        }
        // 刷新结果停留1秒
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                changeState(DONE);
                hide();
            }
        }.sendEmptyMessageDelayed(0, 1000);
    }

    //改变状态
    private void changeState(int to) {
        state = to; //需要改变的状态
        switch (state) {
            case INIT:
                // 上拉布局初始状态
                loadStateImageView.setVisibility(View.GONE);
                loadStateTextView.setText(R.string.pullup_to_load);
                pullUpView.clearAnimation();
                pullUpView.setVisibility(View.VISIBLE);
                break;
            case RELEASE_TO_LOAD:
                // 释放加载状态
                loadStateTextView.setText(R.string.release_to_load);
                pullUpView.startAnimation(rotateAnimation);
                break;
            case LOADING:
                // 正在加载状态
                pullUpView.clearAnimation();
                loadingView.setVisibility(View.VISIBLE);
                pullUpView.setVisibility(View.INVISIBLE);
                loadingView.startAnimation(refreshingAnimation);
                loadStateTextView.setText(R.string.loading);
                break;
            case DONE:
                // 刷新或加载完毕，啥都不做
                break;
        }
    }

    /*
	 * （非 Javadoc）由父控件决定是否分发事件，防止事件冲突
	 * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
	 */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                lastY = downY;
                timer.cancel();
                mEvents = 0; //触控状态，如果多点触控则置-1
                releasePull();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                // 过滤多点触碰
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mEvents == 0) {
                    if (pullUpY < 0
                            || (((Pullable) pullableView).canPullUp() && canPullUp)) {
                        // 可以上拉
                        pullUpY = pullUpY + (event.getY() - lastY) / radio;
                        if (pullUpY > 0) {
                            pullUpY = 0;
                            canPullUp = false;
                        }
                        if (pullUpY < -getMeasuredHeight()) {
                            pullUpY = -getMeasuredHeight();
                        }
                        if (state == LOADING) {
                            // 正在加载的时候触摸移动
                            isTouch = true;
                        }
                    } else {
                        releasePull();
                    }
                } else {
                    mEvents = 0;
                }
                lastY = event.getY();
                // 根据上拉距离改变比例
                radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
                        * ( Math.abs(pullUpY))));
                requestLayout();

                if (pullUpY < 0) {
                    //判断上拉加载，注意pullUpY是负值
                    //上拉释放后，若大于可加载高度则释放加载，否则还原
                    if (-pullUpY <= loadmoreDist
                            && (state == RELEASE_TO_LOAD || state == DONE)) {
                        changeState(INIT);
                    }
                    // 上拉操作
                    if (-pullUpY >= loadmoreDist && state == INIT) {
                        changeState(RELEASE_TO_LOAD);
                    }

                }
                // Math.abs(pullUpY) ,pullUpY是负值
                if ((Math.abs(pullUpY) ) > 8) {
                    // 防止上拉后加载过程中误触发长按事件或者点击事件
                    event.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (-pullUpY > loadmoreDist) {
                    // 正在加载时往上拉，释放后上拉头不隐藏
                    isTouch = false;
                }
                if (state == RELEASE_TO_LOAD) {
                    changeState(LOADING);
                    // 加载操作
                    if (mListener != null)
                        mListener.onLoadMore(this);
                }
                hide();
            default:
                break;
        }
        // 事件分发交给父类
        super.dispatchTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!isLayout) {
            // 这里是第一次进来的时候做一些初始化
            pullableView = getChildAt(0);
            loadmoreView = getChildAt(1);
            isLayout = true;
            initView();
            loadmoreDist = ((ViewGroup) loadmoreView).getChildAt(0)
                    .getMeasuredHeight();
        }
        // 改变子控件的布局
        pullableView.layout(0, (int) ( pullUpY),
                pullableView.getMeasuredWidth(), (int) (pullUpY)
                        + pullableView.getMeasuredHeight());

        loadmoreView.layout(0,
                (int) (pullUpY) + pullableView.getMeasuredHeight(),
                loadmoreView.getMeasuredWidth(),
                (int) (pullUpY) + pullableView.getMeasuredHeight()
                        + loadmoreView.getMeasuredHeight());
    }

    private void hide() {
        timer.schedule(5);
    }

    /**
     * 不限制上拉
     */
    private void releasePull() {
        canPullUp = true;
    }

    class MyTimer {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        public MyTimer(Handler handler) {
            this.handler = handler;
            timer = new Timer();
        }

        public void schedule(long period) {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);
        }

        public void cancel() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
        }

        class MyTask extends TimerTask {
            private Handler handler;

            public MyTask(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                handler.obtainMessage().sendToTarget();
            }

        }
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    /**
     * 刷新加载回调接口
     */
    public interface OnRefreshListener {
        /**
         * 加载操作
         */
        void onLoadMore(PulltorefreshLayout pulltorefreshLayout);
    }

}

