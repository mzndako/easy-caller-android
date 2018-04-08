package info.androidhive.materialdesign.activity;

/**
 * Created by Ravi on 29/07/15.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.adapter.NotifyAdapter;
import info.androidhive.materialdesign.adapter.DividerItemDecoration;
import info.androidhive.materialdesign.adapter.RecyclerTouchListener;
import info.androidhive.materialdesign.adapter.notifications;
import info.androidhive.materialdesign.adapter.notifications;
import info.androidhive.materialdesign.db.DBHelper;
import info.androidhive.materialdesign.db.database;
import info.androidhive.materialdesign.helper.AnimateUtil;

public class NotifyFragment extends Fragment {
    private List<notifications> notifyList = new ArrayList<>();
    DBHelper mydb;
    private RecyclerView recyclerView;
    private int startID = -1;
    private NotifyAdapter mAdapter;
    private View rootView;
    TabLayout.Tab notifyTab;

    public NotifyFragment(){

    }

    @SuppressLint("ValidFragment")
    public NotifyFragment(TabLayout.Tab tab) {
        super();
        notifyTab = tab;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void prepareData() {

        SQLiteDatabase db = mydb.getReadableDatabase();
            String sql = "select * from notification where row_version > "+startID+" order by date DESC LIMIT 500";
            Cursor res = db.rawQuery(sql, null);
            res.moveToFirst();
            boolean have = false;

            while (res.isAfterLast() == false) {
                have = true;
                notifications x = new notifications();
                int row_v = res.getInt(res.getColumnIndex("row_version"));

                x.setId(res.getInt(res.getColumnIndex("id")));
                x.setContent(res.getString(res.getColumnIndex("content")));
                x.setPostedBy(res.getString(res.getColumnIndex("posted_by")));
                x.setCategory(res.getString(res.getColumnIndex("category")));
                x.setDate(res.getInt(res.getColumnIndex("date")));
                int read = res.getInt(res.getColumnIndex("read"));
                x.setRead(read);
                x.setImageLink(res.getString(res.getColumnIndex("image_link")));
                x.setLink(res.getString(res.getColumnIndex("link")));

                notifyList.add(x);

                if(startID < row_v)
                    startID = row_v;
                res.moveToNext();
            }

        mydb.close();

        countNotify();

        if(have && getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });



    }

    public int lastread = -1;

    private void countNotify(){
        int unread = 0;
        for(int i = 0; i < notifyList.size(); i++){
            notifications x = (notifications) notifyList.get(i);
            if(x.getRead() == 0)
                unread++;
        }

        if(lastread != unread && unread > 0){
            final int r = unread;
            if(getActivity() != null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView t = (TextView) notifyTab.getCustomView().findViewById(R.id.text_alert_count);
                        if(r != 0){
                            t.setVisibility(View.VISIBLE);
                            t.setText(r+"");
                        }else{
                            t.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            lastread = unread;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            rootView = inflater.inflate(R.layout.fragment_messages, container, false);
            final View v = rootView;

            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

            mAdapter = new NotifyAdapter(notifyList, R.layout.notification_list_row);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), LinearLayoutManager.VERTICAL));

            recyclerView.setAdapter(mAdapter);

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(v.getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {

                    notifications contact = notifyList.get(position);
                    TextView myTextView = (TextView) view.findViewById(R.id.content);

                    ViewGroup.LayoutParams params = myTextView.getLayoutParams();
                    if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                        if(contact.getHeight() != 0)
                            params.height = contact.getHeight();
                    } else {
                        contact.setHeight(myTextView.getHeight());
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                    myTextView.setLayoutParams(params);

                    if (contact.getRead() == 0) {
                        contact.setRead(1);
                        database db = new database(getContext());
                        db.put("read", 1 + "");
                        db.update("notification", "id = ?", new String[]{contact.getId() + ""});
                        db.close();
                        mAdapter.notifyDataSetChanged();
                        countNotify();
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                    return;
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        prepareData();
                        try {
                            Thread.currentThread().sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();

            mydb = new DBHelper(getContext());
        }else{
            container.removeView(rootView);
        }
        // Inflate the layout for this fragment
        return rootView;
    }

// if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//        tvDocument.setText(Html.fromHtml(bodyData,Html.FROM_HTML_MODE_LEGACY));
//    } else {
//        tvDocument.setText(Html.fromHtml(bodyData));
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
