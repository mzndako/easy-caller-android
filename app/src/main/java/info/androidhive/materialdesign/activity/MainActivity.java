package info.androidhive.materialdesign.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.adapter.NotifyAdapter;
import info.androidhive.materialdesign.db.AndroidDatabaseManager;
import info.androidhive.materialdesign.db.DBHelper;
import info.androidhive.materialdesign.db.database;
import info.androidhive.materialdesign.helper.AnimateUtil;
import info.androidhive.materialdesign.helper.MyDialog;
import info.androidhive.materialdesign.helper.PrefManager;
import info.androidhive.materialdesign.service.HttpService;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentAdapterClass fragmentAdapter;
    DBHelper dbHelper;
    private PrefManager pref;
    private TabLayout.Tab notifyTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // display the first navigation drawer view on app launch
//        displayView(0);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout1);
        viewPager = (ViewPager) findViewById(R.id.pager1);

        notifyTab = tabLayout.newTab().setText("Notification").setCustomView(R.layout.notification_tab);


        tabLayout.addTab(tabLayout.newTab().setText("Contacts").setIcon(R.drawable.contacts));
        tabLayout.addTab(tabLayout.newTab().setText("Recents").setIcon(R.drawable.missed_call));
        tabLayout.addTab(notifyTab);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        fragmentAdapter = new FragmentAdapterClass(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(fragmentAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        final SearchView searchView = (SearchView) findViewById(R.id.search_view);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab LayoutTab) {

                if(LayoutTab.getPosition() == 0){
                    searchView.setVisibility(View.VISIBLE);
                }else{
                    searchView.setVisibility(View.GONE);
                }
                viewPager.setCurrentItem(LayoutTab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab LayoutTab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab LayoutTab) {

            }
        });


        pref = new PrefManager(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initializeView();
        }
    }

    private void initializeView() {

                startService(new Intent(getApplicationContext(), HttpService.class));
//                startService(new Intent(getApplicationContext(), FloatingViewService.class));
//                finish();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initializeView();
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. We cant identify incoming or outgoing calls",
                        Toast.LENGTH_LONG).show();

//                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        if(id == R.id.action_search){
            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void showDialogAlert(String message, String action){
        final String x = action;
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.layout.dialog_ok);

        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(message);

        String positiveText = action == ""?"OK":"Yes";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(x.equalsIgnoreCase("send_otp")){
//                            requestForOtp(inputEmail.getText().toString());
                        }
                    }
                });

        String negativeText = "No";
        if(action != "") {
            builder.setNegativeButton(negativeText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // negative button logic
                        }
                    });
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {

        if(position == 0){
            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(i);
        }

        if(position == 1){
            MyDialog dialog = new MyDialog(this){
                @Override
                protected void onButtonClick(View v) {
                    if(v.getId() == MyDialog.YES_BUTTON){
                        View progress = (View) findViewById(R.id.progress_overlay);
                        progress.setVisibility(View.VISIBLE);

                        pref.setToken("");
                        pref.setIsWaitingForEmail(false);
                        pref.setEmail("");
                        stopService(new Intent(getApplicationContext(), HttpService.class));
                        database db = new database(MainActivity.this);
                        String[] list = new String[]{"members", "command","rank","position","department","recent","notification"};

                        for(int i =0; i < list.length; i++){
                            db.delete(list[i],null,null);
                        }
                        db.close();

                        progress.setVisibility(View.GONE);
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                    super.onButtonClick(v);
                }
            };
            dialog.enableYesAndNo();
            dialog.showDialog("Logout???");

        }


    }


    /**
     * Created by JUNED on 5/30/2016.
     */
    HomeFragment tab1 = new HomeFragment();
    RecentFragment tab2 = new RecentFragment();
    NotifyFragment        tab3 = new NotifyFragment(notifyTab);
    public class FragmentAdapterClass extends FragmentStatePagerAdapter {

        int TabCount;

//        RecentFragment tab2;
//        NotifyFragment tab3;

        public FragmentAdapterClass(FragmentManager fragmentManager, int CountTabs) {
            super(fragmentManager);

            this.TabCount = CountTabs;
        }

        @Override
        public Fragment getItem(int position) {


            switch (position) {
                case 0:
                    if(tab1 == null)
                     tab1 = new HomeFragment();
                    return tab1;

                case 1:
                    if(tab2 == null)
                    tab2 = new RecentFragment();
                    return tab2;

                case 2:
                    if(tab3 == null)
                    tab3 = new NotifyFragment(notifyTab);
                    return tab3;

                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            return TabCount;
        }
    }
}