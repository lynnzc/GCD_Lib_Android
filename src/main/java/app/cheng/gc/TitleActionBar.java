package app.cheng.gc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.cheng.gc.Data.StudentInfo;
import app.cheng.gc.Data.WebAddress;
import app.cheng.gc.Fragment.BookmarkFragment;
import app.cheng.gc.Fragment.BorrowedFragment;
import app.cheng.gc.Fragment.HistoryFragment;
import app.cheng.gc.Fragment.SearchFragment;

/**
 * Created by lynnlyf on 2015/2/2.
 */
public class TitleActionBar extends ActionBarActivity {

    //menu title
    private String[] menuTitle;
    private String displayTitle;
    private String displayDrawerTitle;
    //menu list
    private ListView menuList;
    //menuLayout
    private DrawerLayout menuLayout;
    private RelativeLayout menuLayout_left;
    private ActionBarDrawerToggle menuToggle;
    private ImageButton menu_btn;
    private ImageButton menu_btn_login;

    //用户名
    private TextView stu_info_item;
    private StudentInfo stu_info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        stu_info_item = (TextView)findViewById(R.id.student_info_item);
        displayDrawerTitle = displayTitle = getTitle().toString();
        menuLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        menuLayout_left = (RelativeLayout)findViewById(R.id.menu_layout_left);
        menuList = (ListView)findViewById(R.id.menu_listView);
        menuTitle = getResources().getStringArray(R.array.menu_array);

        //判断登陆
        Intent intent = getIntent();

        if(intent != null) {
                stu_info = (StudentInfo)intent.getSerializableExtra(WebAddress.USER_INFO_TRANS);
                if (stu_info != null) {
                    stu_info_item.setText(stu_info.getName());
                    System.out.println("成功传递: " + stu_info.getName()); //测试
                    WebAddress.state = true;
                }
                else {
                    System.out.println("没有传递或传递失败");
                }
        }

        setAdapter();
        initActionBar();

        menuList.setOnItemClickListener(new DrawerItemClickListener());

        menuToggle = new ActionBarDrawerToggle(this, menuLayout, R.drawable.ic_tab_content_pressed,
                R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View v) {
                getSupportActionBar().setTitle(displayTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View d_v) {
                getSupportActionBar().setTitle(displayDrawerTitle);
                invalidateOptionsMenu();
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                //Left activity从左到右出现
                if(item != null && item.getItemId() == android.R.id.home) {
                    if(menuLayout.isDrawerVisible(GravityCompat.START)) {
                        menuLayout.closeDrawer(GravityCompat.START); //关闭菜单
                    }
                    else {
                        menuLayout.openDrawer(GravityCompat.START); //打开菜单
                    }
                    return true;
                }
                return false;
            }
        };

        menuLayout.setDrawerListener(menuToggle);

        stu_info_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户信息页
                Intent intent = new Intent(TitleActionBar.this, UserInfoPage.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(WebAddress.USER_TRANS, stu_info);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        if(savedInstanceState == null) {
            selectItem(0);
        }
    }

    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();

        // 可以自定义actionbar
        actionbar.setCustomView(R.layout.actionbar_view);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setDisplayShowCustomEnabled(true);

        // 设置Actionbar背景
        actionbar.setBackgroundDrawable(
                getResources().getDrawable(R.drawable.actionbar_bg));

        menu_btn = (ImageButton) findViewById(R.id.left_btn);
        menu_btn_login = (ImageButton)findViewById(R.id.left_nologin_btn);

        //登陆前后
        if(WebAddress.state) {
            menu_btn_login.setVisibility(View.GONE);
            menu_btn.setVisibility(View.VISIBLE); //登陆成功
            menuLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED); //打开手势滑动
            menuLayout.setEnabled(true);
            menu_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (menuLayout.isDrawerVisible(GravityCompat.START)) {
                        menuLayout.closeDrawer(GravityCompat.START);
                    } else {
                        menuLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
        }
        else {
            //没有登陆
            menu_btn.setVisibility(View.GONE);
            menu_btn_login.setVisibility(View.VISIBLE);
            menuLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //关闭手势滑动
            menuLayout.setEnabled(false);
            menu_btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                }
            });
        }
    }

    public void setAdapter() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        for(int i = 0 ; i < menuTitle.length ; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            switch(i) {
                case 0:
                    map.put("item_image", R.drawable.icon1);
                    break;
                case 1:
                    map.put("item_image", R.drawable.icon2);
                    break;
                case 2:
                    map.put("item_image", R.drawable.icon3);
                    break;
                case 3:
                    map.put("item_image", R.drawable.icon4);
                    break;
            }
            map.put("item_text", menuTitle[i]);
            list.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list,
                R.layout.menuitem, new String[] {
                "item_image", "item_text"
        }, new int[] {R.id.menu_image, R.id.menu_item});

        menuList.setAdapter(adapter);
    }

    //<--------menu-------->

    public void setTitle(String title) {
        displayTitle = title;
        getSupportActionBar().setTitle(displayTitle);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        // update selected item and title, then close the drawer
        Fragment fragment = new Fragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch(position) {
            // 0 代表搜书
            // 1 代表借书情况
            // 2 代表历史
            // 3 代表收藏夹
            case 0:
                fragment = new SearchFragment();
                break;
            case 1:
                fragment = new BorrowedFragment();
                break;
            case 2:
                fragment = new HistoryFragment();
                break;
            case 3:
                fragment = new BookmarkFragment();
                break;
            default:
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.menu_main, fragment).commit();
        menuList.setItemChecked(position, true);
        menuLayout.closeDrawer(menuLayout_left);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        menuToggle.syncState();
        //该方法会自动和actionBar关联, 将开关的图片显示在了action上
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.app_item1);
        item.setVisible(false);
        item = menu.findItem(R.id.app_item2);
        item.setVisible(false);
        item = menu.findItem(R.id.app_item3);
        item.setVisible(false);
        item = menu.findItem(R.id.app_item4);
        item.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这个是ActionBar上的图标响应
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (menuToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    //<--------menu-------->

}