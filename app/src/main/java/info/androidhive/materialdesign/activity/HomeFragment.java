package info.androidhive.materialdesign.activity;

/**
 * Created by Ravi on 29/07/15.
 */
import android.app.Activity;
import android.app.Dialog;
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
import info.androidhive.materialdesign.helper.MyDialog;
import info.androidhive.materialdesign.helper.MyDialogDialer;
import info.androidhive.materialdesign.helper.PrefManager;

public class HomeFragment extends Fragment {
    private List<contacts> contactList = new ArrayList<>();
    private List<Integer> ids = new ArrayList<Integer>();
    private RecyclerView recyclerView;
    private ContactAdapter mAdapter;
    private int startID = -1;
    private View rootView;
    private DBHelper mydb;
    private PrefManager pref;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private MyDialog dd;
    private void prepareData() {
        if(!pref.isLoggedIn()){
            if(dd == null && getActivity() != null){
                dd = new MyDialog(getActivity()){
                    @Override
                    protected void onButtonClick(View v) {
                        super.onButtonClick(v);
                        Intent i = new Intent(getActivity(), LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        getActivity().finish();
                    }
                };
                if(getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dd.showDialog("You have been logout.\nReason: Duplicate Login.\nPlease re-login");
                        }
                    });

            }
            return;
        }
             SQLiteDatabase db = mydb.getReadableDatabase();

            String sql = "select members.id as id, members.row_version as row_version, members.fname as fname, members.mname as mname, members.surname as surname, members.phone_1 as phone_1, members.phone_2 as phone_2, command.name as command, rank.abbr as rank, department.abbr as department, position.abbr as position from members left join command on command.id = members.command left join rank on rank.id = members.rank left join department on department.id = members.department left join position on position.id = members.position WHERE members.row_version > "+startID+" ORDER BY members.surname ASC";
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

                int row_version = res.getInt(res.getColumnIndex("row_version"));

                x.setFname(res.getString(res.getColumnIndex("fname")));
                x.setMname(res.getString(res.getColumnIndex("mname")));
                x.setSurname(res.getString(res.getColumnIndex("surname")));
                x.setPhone1(res.getString(res.getColumnIndex("phone_1")));
                x.setPhone2(res.getString(res.getColumnIndex("phone_2")));
                x.setRank(res.getString(res.getColumnIndex("rank")));
                x.setCommand(res.getString(res.getColumnIndex("command")));
                x.setPosition(res.getString(res.getColumnIndex("position")));
                x.setDepartment(res.getString(res.getColumnIndex("department")));
                contactList.add(x);
                ids.add(id);

                if(row_version > startID)
                    startID = row_version;

                res.moveToNext();
            }


        mydb.close();


        if(have && getActivity() != null)
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            if (savedInstanceState == null) {
                rootView = inflater.inflate(R.layout.fragment_home, container, false);


                final View v = rootView;

                recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
                SearchView searchView = (SearchView) getActivity().findViewById(R.id.search_view);
                searchView.setOnQueryTextListener(new MyQueryListener());

                mAdapter = new ContactAdapter(contactList, R.layout.contact_list_row);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
        //        recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), LinearLayoutManager.VERTICAL));

                recyclerView.setAdapter(mAdapter);

                recyclerView.addOnItemTouchListener(new RecyclerTouchListener(v.getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        contacts contact = contactList.get(position);
                        if(contact.getPhone2().isEmpty()){
                            try {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:+"+contact.getPhone1().toString()));
                                startActivity(callIntent);
                            } catch (ActivityNotFoundException activityException) {
                                Log.e("Calling a Phone Number", "Call failed", activityException);
                            }
                        }else{
        //                    final CharSequence colors[] = new CharSequence[] {contact.getPhone1(),contact.getPhone2()};
                            MyDialogDialer dialer = new MyDialogDialer(getActivity());
                            dialer.showDialog(contact.getPhone1(), contact.getPhone2());
                        }

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true){
                            prepareData();
                            try {
                                Thread.currentThread().sleep(Config.MINIMUM_SLEEP);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

                mydb = new DBHelper(getContext());
                pref = new PrefManager(rootView.getContext());
            } else {
//                ((ViewGroup) rootView.getParent()).removeView(rootView);
                container.removeView(rootView);
            }
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onResume() {
//        Toast.makeText(getContext(),"This is my home fragmetn",Toast.LENGTH_LONG).show();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private class MyQueryListener implements SearchView.OnQueryTextListener{

        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            Toast.makeText(rootView.getContext(),"Hello" + s, Toast.LENGTH_LONG);
            mAdapter.filter(s);
            return false;
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
