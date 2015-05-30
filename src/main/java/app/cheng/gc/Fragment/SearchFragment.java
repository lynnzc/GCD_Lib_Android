package app.cheng.gc.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.cheng.gc.ClientAPI;
import app.cheng.gc.Data.BookInfo;
import app.cheng.gc.PullTool.PulltorefreshLayout;
import app.cheng.gc.R;
import app.cheng.gc.SearchItem;
import app.cheng.gc.Utils;

/**
 * Created by lynnlyf on 2015/2/2.
 */
public class SearchFragment extends Fragment {
    //搜索框
    private EditText bookSearch;
    //搜索按钮
    private Button searchButton;
    //书籍列表
    private ListView bookList;
    //下载的书籍信息
    private List<Map<String, String>> datalist;
    //API
    private ClientAPI client;
    //page
    private int page = 1;

    //num
    private double num;
    private int currentnum;
    int lastItem = 0; //记住上一次最后一项位置
    //当前加载为最后一页
    private boolean isLastpage = false;
    //首次加载
    private boolean firstload = false;
    //适配器
    SimpleAdapter adapter;
    private View searchView;

    PulltorefreshLayout pulltorefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        searchView = inflater.inflate(R.layout.searchlayout, container, false); //主要布局
        bookSearch = (EditText)searchView.findViewById(R.id.search_text);
        searchButton = (Button)searchView.findViewById(R.id.search_button);

        bookList = (ListView)searchView.findViewById(R.id.content_view);

        pulltorefreshLayout = (PulltorefreshLayout)
                searchView.findViewById(R.id.refresh_view);

        pulltorefreshLayout.setOnRefreshListener(new PullListener()); //底部加载

        bookSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER &&
                        event.getAction() == KeyEvent.ACTION_DOWN) {
                    try {
                        String search = new String(bookSearch.getText().toString()
                                .trim().getBytes(), "ISO-8859-1");

                        if(search.equals("")) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "请输入搜索内容", Toast.LENGTH_LONG).show();
                        }
                        else {
                            isLastpage = false; //是否最后一页初始化
                            firstload = true; //是否第一次加载
                            currentnum = 0; //当前加载数
                            page = 1; //初始化查找第一页

                            LoadBookTask loadBook = new LoadBookTask();
                            loadBook.execute(bookSearch.getText().toString().trim()); //异步搜索
                        }
                    }
                    catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    InputMethodManager imm = (InputMethodManager) v.
                            getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String search = new String(bookSearch.getText().toString()
                            .trim().getBytes(), "ISO-8859-1");

                    if(search.equals("")) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "请输入搜索内容", Toast.LENGTH_LONG).show();
                    }
                    else {
                        isLastpage = false; //最后一页初始化
                        firstload = true; //第一次加载
                        currentnum = 0; //当前加载数
                        page = 1; //初始化第一页

                        LoadBookTask loadBook = new LoadBookTask();
                        loadBook.execute(bookSearch.getText().toString().trim()); //异步搜索
                    }
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        return searchView;
    }

    class LoadBookTask extends AsyncTask<String, ListView, List<BookInfo>> {
        @Override
        protected void onPreExecute() {

            InputMethodManager im = (InputMethodManager)getActivity().getSystemService(
                    getActivity().INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(bookSearch.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            super.onPreExecute();
        }

        @Override
        protected List<BookInfo> doInBackground(String... params) {
            client = new ClientAPI();

            if(params.length == 2) {
                page++; //下一页
                return client.searchBook("anywords", params[0], page);
            }
            else {
                return client.searchBook("anywords", params[0]);
            }
        }

        @Override
        protected void onPostExecute(List<BookInfo> bookInfoList) {
            if(bookInfoList != null) {
                if (firstload) {
                    //首次加载刷新列表
                    num = Utils.getSearchNum(); //总数
                    currentnum = 20 > (int) num ? (int) num : 20; //加载最多20条，少于20条加载完
                    System.out.println("num" + (int) num);//测试

                    datalist = new ArrayList<Map<String, String>>();
                    adapter = new SimpleAdapter(getActivity().getApplicationContext(),
                            datalist,
                            R.layout.list_layout,
                            new String[]{"serialnum", "bookname", "bookauthor", "publisher", "publishyear"
                                    , "collectnum", "booksize", "canborrow"}, new int[]{R.id.booknum,
                            R.id.bookname, R.id.bookauthor, R.id.bookpress, R.id.place}
                    );
                    lastItem = 0; //首次加载，上一次最后一项位置置0
                    firstload = false;
                } else {
                    currentnum += 20; //每次最多加载20条，少于20条全部加载出来
                }

                System.out.println("currentnum" + currentnum);//测试
                if ((int) num - currentnum <= 0) {
                    Toast.makeText(getActivity()
                            , "最后一页了", Toast.LENGTH_LONG).show();
                    isLastpage = true;
                }

                if (!firstload) {
                    //非首次搜索加载
                    // 告诉控件加载完毕
                    pulltorefreshLayout.loadmoreFinish(PulltorefreshLayout.SUCCEED);
                }

                Iterator<BookInfo> it = bookInfoList.iterator();
                while (it.hasNext()) {
                    Map<String, String> map = new HashMap<String, String>();
                    BookInfo bookinfo = it.next();
                    map.put("serialnum", bookinfo.getSerialnum());
                    map.put("bookname", bookinfo.getBookname());
                    map.put("bookauthor", bookinfo.getBookauthor());
                    map.put("publisher", bookinfo.getPublisher());
                    map.put("publishyear", bookinfo.getPublishyear());
                    map.put("collectnum", bookinfo.getCollectnum());
                    map.put("booksize", bookinfo.getBooksize());
                    map.put("canborrow", bookinfo.getCanborrow());
                    datalist.add(map);
                }

                adapter.notifyDataSetChanged();
                bookList.setAdapter(adapter);
                bookList.setSelection(lastItem);
                lastItem = adapter.getCount() - 1;
                bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        Intent intent = new Intent();
                        intent.putExtra("serialnum", ((TextView) arg1.findViewById
                                (R.id.booknum)).getText());
                        intent.putExtra("bookname", ((TextView) arg1.findViewById
                                (R.id.bookname)).getText());
                        intent.putExtra("bookpress", ((TextView) arg1.findViewById
                                (R.id.bookpress)).getText());
                        intent.setClass(getActivity().getApplicationContext(), SearchItem.class);
                        startActivity(intent);
                    }
                });
            }
            else {
                Toast.makeText(getActivity().getApplicationContext(),
                        "没有找到合适的书籍", Toast.LENGTH_LONG).show();
            }

            super.onPostExecute(bookInfoList);
        }
    }

    public class PullListener implements PulltorefreshLayout.OnRefreshListener {
        @Override
        public void onLoadMore(final PulltorefreshLayout pulltorefreshLayout) {
            // 加载操作
            if(!isLastpage) {
                LoadBookTask loadBook = new LoadBookTask();
                loadBook.execute(bookSearch.getText().toString().trim(), "下一页");
                //pulltorefreshLayout.loadmoreFinish(PulltorefreshLayout.SUCCEED);
            }
            else {
                Toast.makeText(getActivity()
                        , "最后一页了", Toast.LENGTH_LONG).show();
                pulltorefreshLayout.loadmoreFinish(PulltorefreshLayout.FAIL);
            }
        }
    }

}
