package app.cheng.gc.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.cheng.gc.ClientAPI;
import app.cheng.gc.Data.BorrowedBookInfo;
import app.cheng.gc.R;
import app.cheng.gc.SearchItem;

/**
 * Created by lynnlyf on 2015/3/8.
 */
public class BookmarkFragment extends Fragment {
    private View bookmarkView;
    private ListView list_bookmark;
    private Button pre_btn, next_btn;
    private ClientAPI client = new ClientAPI();
    private LoadBookmarkInfo loadBookmarkInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
        bookmarkView = inflater.inflate(R.layout.bookmarklayout, container, false);

        pre_btn = (Button)bookmarkView.findViewById(R.id.pre_page_btn);
        next_btn = (Button)bookmarkView.findViewById(R.id.next_page_btn);
        list_bookmark = (ListView)bookmarkView.findViewById(R.id.bookmarklist);

        if(client != null) {
            pre_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadBookmarkInfo = new LoadBookmarkInfo();
                    loadBookmarkInfo.execute("下一页", "上一页");
                }
            });

            next_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadBookmarkInfo = new LoadBookmarkInfo();
                    loadBookmarkInfo.execute("下一页");
                }
            });

            loadBookmarkInfo = new LoadBookmarkInfo();
            loadBookmarkInfo.execute();
        }

        return bookmarkView;
    }

    class LoadBookmarkInfo extends AsyncTask<String, String, List<BorrowedBookInfo>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<BorrowedBookInfo> doInBackground(String[] params) {
            if(params.length == 2) {
                return client.nextBookmarkPage();
            }
            else if(params.length == 3) {
                return client.preBookmarkPage();
            }
            return client.getBookmarkInfo();
        }

        @Override
        protected void onPostExecute(List<BorrowedBookInfo> result) {
            List<Map<String, String>> list_book = new ArrayList<Map<String,
                    String>>();
            Iterator<BorrowedBookInfo> it = result.iterator();
            while(it.hasNext()) {
                Map<String, String> map = new HashMap<String, String>();
                BorrowedBookInfo bookmarkBookInfo = it.next();
                map.put("bookmark_num", bookmarkBookInfo.getLogin_num());
                map.put("bookmark_name", bookmarkBookInfo.getBookname());
                map.put("bookmark_marktime", bookmarkBookInfo.getBorrow_date());
                map.put("bookmark_searchnum", bookmarkBookInfo.getSearchnum()); //取得地址后
                list_book.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(),
                    list_book, R.layout.bookmarkitemlayout, new String[] {"bookmark_searchnum", "bookmark_num",
                    "bookmark_name", "bookmark_marktime"}, new int[] {R.id.bookmark_searchnum, R.id.bookmark_num,
                    R.id.bookmark_name, R.id.bookmark_marktime});

            list_bookmark.setAdapter(adapter);

            list_bookmark.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                    Intent intent = new Intent();
                    intent.putExtra("serialnum", ((TextView)arg1.findViewById
                            (R.id.bookmark_searchnum)).getText());
                    intent.putExtra("bookname", ((TextView)arg1.findViewById
                            (R.id.bookmark_name)).getText());
                    intent.putExtra("bookpress", ""); //暂时
                    intent.setClass(getActivity().getApplicationContext(),
                            SearchItem.class);
                    startActivity(intent);
                }
            });

            super.onPostExecute(result);
        }
    }
}
