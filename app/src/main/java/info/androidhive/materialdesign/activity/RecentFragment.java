package info.androidhive.materialdesign.activity;

/**
 * Created by Ravi on 29/07/15.
 */
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.adapter.ContactAdapter;
import info.androidhive.materialdesign.adapter.DividerItemDecoration;
import info.androidhive.materialdesign.adapter.RecyclerTouchListener;
import info.androidhive.materialdesign.adapter.contacts;
import info.androidhive.materialdesign.app.Config;
import info.androidhive.materialdesign.db.DBHelper;

public class RecentFragment extends Fragment {
    private List<contacts> contactList = new ArrayList<>();
    private List<Integer> ids = new ArrayList<Integer>();
    private int startID = -1;
    private RecyclerView recyclerView;
    private ContactAdapter mAdapter;
    private View rootView;
    DBHelper mydb;

    public RecentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void prepareData(final ContactAdapter myadapter) {

        SQLiteDatabase db = mydb.getReadableDatabase();
            String sql = "select recent.id as id, recent.phone as phone, recent.start as start, recent.end as end, members.fname as fname, members.mname as mname, members.surname as surname, members.phone_1 as phone_1, members.phone_2 as phone_2, command.name as command, rank.abbr as rank, department.abbr as department, position.abbr as position from recent left join command on command.id = recent.command left join rank on rank.id = recent.rank left join department on department.id = recent.department left join position on position.id = recent.position left join members on recent.member = members.id WHERE recent.id > "+startID+" ORDER BY recent.id ASC LIMIT 500";

            Cursor res = db.rawQuery(sql, null);
            res.moveToFirst();
            boolean have = false;

            while (res.isAfterLast() == false) {
                contacts x = new contacts();
                int id = res.getInt(res.getColumnIndex("id"));;
                if(ids.contains(id)){
                    res.moveToNext();
                    continue;
                }

                have = true;
                x.setFname(res.getString(res.getColumnIndex("fname")));
                x.setMname(res.getString(res.getColumnIndex("mname")));
                x.setSurname(res.getString(res.getColumnIndex("surname")));
                x.setPhone(res.getString(res.getColumnIndex("phone")));
                x.setRank(res.getString(res.getColumnIndex("rank")));
                x.setStartTime(res.getLong(res.getColumnIndex("start")));
                x.setCommand(res.getString(res.getColumnIndex("command")));
                x.setPosition(res.getString(res.getColumnIndex("position")));
                x.setDepartment(res.getString(res.getColumnIndex("department")));
                contactList.add(0, x);

                if(id > startID)
                    startID = id;
                ids.add(id);
                res.moveToNext();
            }

        mydb.close();

//        contacts b = new contacts("Ndako", "Ours", "2015","Pos", "Rank", "name0", "james","07034634717","" );
//        contactList.add(b);




        if(getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myadapter.notifyDataSetChanged();
                }
            });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            rootView = inflater.inflate(R.layout.fragment_recent, container, false);
            final View v = rootView;

            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

            mAdapter = new ContactAdapter(contactList, R.layout.recent_list_row);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), LinearLayoutManager.VERTICAL));

            recyclerView.setAdapter(mAdapter);

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(v.getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    contacts contact = contactList.get(position);
                    if (!contact.getPhone().isEmpty()) {
                        try {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:+" + contact.getPhone().toString()));
                            startActivity(callIntent);
                        } catch (ActivityNotFoundException activityException) {
                            Log.e("Calling a Phone Number", "Call failed", activityException);
                        }
                    }


                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            new sync(mAdapter).start();

            mydb = new DBHelper(getContext());
        }else{
            container.removeView(rootView);
        }
        // Inflate the layout for this fragment
        return rootView;
    }

    private class sync extends Thread implements Runnable{
        private ContactAdapter ca;

        public sync(ContactAdapter ca){
            this.ca = ca;
        }
        @Override
        public void run() {
            while(true){
                prepareData(ca);
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
