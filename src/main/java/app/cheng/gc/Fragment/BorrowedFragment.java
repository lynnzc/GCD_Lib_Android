package app.cheng.gc.Fragment;

import android.content.SharedPreferences;
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
import app.cheng.gc.Data.BookInfo;
import app.cheng.gc.Data.BorrowedBookInfo;
import app.cheng.gc.Data.WebAddress;
import app.cheng.gc.R;

/**
 * Created by lynnlyf on 2015/2/2.
 */
public class BorrowedFragment extends Fragment {
    private View borrowedView;
    private ListView borrowed_list;
    private ClientAPI client = new ClientAPI();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        borrowedView = inflater.inflate(R.layout.borrowedlayout, container, false);

        borrowed_list = (ListView)borrowedView.findViewById(R.id.borrowed_list);

        if(client != null) {
            LoadBorrowedBookInfo loadBorrowedBookInfo = new LoadBorrowedBookInfo();
            loadBorrowedBookInfo.execute();
        }
        return borrowedView;
    }

    class LoadBorrowedBookInfo extends AsyncTask<Void, String, List<BorrowedBookInfo>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<BorrowedBookInfo> doInBackground(Void[] params) {
            return client.getBorrowedContent();
        }

        @Override
        protected void onPostExecute(List<BorrowedBookInfo> result) {

            List<Map<String, String>> list_borrowedbook = new ArrayList<Map<String,
                    String>>();
            Iterator<BorrowedBookInfo> it = result.iterator();
            while(it.hasNext()) {
                Map<String, String> map = new HashMap<String, String>();
                BorrowedBookInfo borrowedBookInfo = it.next();
                map.put("bookname", borrowedBookInfo.getBookauthor());
                map.put("borrowtime", borrowedBookInfo.getBorrow_date());
                map.put("returntime", borrowedBookInfo.getReturn_date());
                list_borrowedbook.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(),
                    list_borrowedbook, R.layout.borrowedbookitemlayout, new String[] {"bookname",
                    "borrowtime", "returntime"}, new int[] {R.id.bookname,
                            R.id.borrowtime, R.id.returntime});
            borrowed_list.setAdapter(adapter);

            super.onPostExecute(result);
        }
    }
}