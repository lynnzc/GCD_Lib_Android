package app.cheng.gc.PullTool;

/**
 * Created by lynnlyf on 2015/2/2.
 */
/**
 * 本工具参考了网上开源的下拉刷新，上拉加载的demo
 * Created by lynnlyf on 2015/1/10.
 */

public interface Pullable {
    /*运用接口，方便以后扩展功能*/
    /**
     * 判断是否可以上拉，如果不需要上拉功能可以直接return false
     *
     * @return true如果可以上拉,否则返回false
     */
    boolean canPullUp();
}