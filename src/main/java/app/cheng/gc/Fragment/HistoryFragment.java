package app.cheng.gc.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.cheng.gc.ClientAPI;
import app.cheng.gc.Data.BorrowedBookInfo;
import app.cheng.gc.Data.WebAddress;
import app.cheng.gc.R;

/**
 * Created by lynnlyf on 2015/2/2.
 */
public class HistoryFragment extends Fragment {
    private View historyView;
    private ListView list_historybook;
    private Button pre_btn, next_btn;
    private ClientAPI client = new ClientAPI();
    private LoadHistoryBookInfo loadHistoryBookInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        historyView = inflater.inflate(R.layout.historylayout, container, false);

        pre_btn = (Button)historyView.findViewById(R.id.pre_page_btn);
        next_btn = (Button)historyView.findViewById(R.id.next_page_btn);

        list_historybook = (ListView)historyView.findViewById(R.id.historylist);

        if(client != null) {
            pre_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadHistoryBookInfo = new LoadHistoryBookInfo();
                    loadHistoryBookInfo.execute(WebAddress.SEARCH_PRE);
                }
            });

            next_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadHistoryBookInfo = new LoadHistoryBookInfo();
                    loadHistoryBookInfo.execute(WebAddress.SEARCH_NEXT);
                }
            });
            loadHistoryBookInfo = new LoadHistoryBookInfo();
            loadHistoryBookInfo.execute(WebAddress.SEARCH_INIT);
        }

        return historyView;
    }

    class LoadHistoryBookInfo extends AsyncTask<Integer, String, List<BorrowedBookInfo>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<BorrowedBookInfo> doInBackground(Integer... params) {
            if(params[0] == WebAddress.SEARCH_NEXT) {
                return client.nextHistoryPage();
            }
            else if(params[0] == WebAddress.SEARCH_PRE) {
                return client.preHistroryPage();
            }
            return client.getBorrowedHistory(); //SEARCH_INIT
        }

        @Override
        protected void onPostExecute(List<BorrowedBookInfo> result) {
            List<Map<String, String>> list_book = new ArrayList<Map<String,
                    String>>();
            Iterator<BorrowedBookInfo> it = result.iterator();
            while(it.hasNext()) {
                Map<String, String> map = new HashMap<String, String>();
                BorrowedBookInfo borrowedBookInfo = it.next();
                map.put("bookname", borrowedBookInfo.getBookauthor());
                map.put("borrowtime", borrowedBookInfo.getBorrow_date());
                map.put("returntime", borrowedBookInfo.getReturn_date());
                map.put("booktype", borrowedBookInfo.getType());
                map.put("loginnum", borrowedBookInfo.getLogin_num());
                list_book.add(map);
            }

            SimpleAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(),
                    list_book, R.layout.historybookitemlayout, new String[] {"bookname",
                    "borrowtime", "returntime", "booktype", "loginnum"}, new int[] {R.id.bookname,
                    R.id.borrowtime, R.id.returntime, R.id.type, R.id.loginnum});

            list_historybook.setAdapter(adapter);

            super.onPostExecute(result);
        }
    }
}