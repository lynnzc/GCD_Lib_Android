package app.cheng.gc;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.cheng.gc.Data.SearchBookInfo;

/**
 * Created by lynnlyf on 2015/2/2.
 */
public class SearchItem extends ActionBarActivity {
    private ListView bookItemList;
    private TextView book_item_name;
    private TextView book_item_num;
    private TextView book_item_publish;
    private TextView title;
    private Intent intent;
    private List<Map<String, String>> datalist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchbookitemlayout);

        intent = getIntent();
        System.out.println("成功传递: " + intent.getStringExtra("serialnum")); //测试

        initActionBar();

        book_item_num = (TextView)findViewById(R.id.book_item_num);
        book_item_name = (TextView)findViewById(R.id.book_item_name);
        book_item_publish = (TextView)findViewById(R.id.book_item_publish);

        book_item_num.setText(intent.getStringExtra("serialnum"));
        book_item_name.setText(intent.getStringExtra("bookname"));
        book_item_publish.setText(intent.getStringExtra("bookpress"));

        bookItemList = (ListView)findViewById(R.id.searchitem_list);

        BookItemBorrowedInfo bookItemBorrowedInfo = new BookItemBorrowedInfo();
        bookItemBorrowedInfo.execute();
    }

    private void initActionBar() {
        ActionBar actionbar = getSupportActionBar();

        // 可以自定义actionbar
        actionbar.setCustomView(R.layout.actionbar_view_user);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setDisplayShowCustomEnabled(true);

        title = (TextView)actionbar.getCustomView().findViewById(R.id.title);
        title.setText("书籍详情");

        // 设置Actionbar背景
        actionbar.setBackgroundDrawable(
                getResources().getDrawable(R.drawable.actionbar_bg));
    }

    class BookItemBorrowedInfo extends AsyncTask<Void, ListView, List<SearchBookInfo>> {
        @Override
        protected List<SearchBookInfo> doInBackground(Void... params) {
            ClientAPI client = new ClientAPI();
            return client.SearchBookInfo(intent.getStringExtra("serialnum"));
        }

        @Override
        protected void onPostExecute(List<SearchBookInfo> searchBookInfoList) {
            datalist = new ArrayList<Map<String, String>>();

            Iterator<SearchBookInfo> it = searchBookInfoList.iterator();

            super.onPostExecute(searchBookInfoList);
            while(it.hasNext()) {
                Map<String, String> map = new HashMap<String, String>();
                SearchBookInfo searchBookInfo = it.next();
                map.put("address", searchBookInfo.getAddress());
                map.put("fetchnumber", searchBookInfo.getFetchnumber());
                map.put("isLent", searchBookInfo.IsLent());
                datalist.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(SearchItem.this, datalist,
                    R.layout.searchbookitem, new String[]{"address", "fetchnumber", "islent"}, new int []{
                    R.id.book_item_address, R.id.book_item_fetchnumber, R.id.book_item_isLent});
            bookItemList.setAdapter(adapter);
            super.onPostExecute(searchBookInfoList);
        }
    }
}

