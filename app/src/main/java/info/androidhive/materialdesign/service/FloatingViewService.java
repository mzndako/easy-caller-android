package info.androidhive.materialdesign.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.adapter.contacts;
import info.androidhive.materialdesign.db.DBHelper;

/**
 * Created by HP ENVY on 5/18/2017.
 */

public class FloatingViewService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.contact_widget, null);

//        initialize();
        //Add the view to the window.


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        intent.getExtras().getString("phone_number");
        String x = intent.getExtras().getString("phone_number");
        initialize(x);
        return START_STICKY;
    }

    public void initialize(String number){

        if(number.isEmpty())
            return;
        number = number.replace(" ", "").replace("+","");
        if(number.indexOf("0") == 0){
            number = "234"+number.substring(1);
        }

        //COMMENT THIS OUT FOR NOW
        if(number.indexOf("234") == 0){
//            number = "0"+number.substring(3);
        }

        DBHelper mydb = new DBHelper(mFloatingView.getContext());
        SQLiteDatabase db = mydb.getReadableDatabase();
        mFloatingView.getContext();
            String sql = "select members.fname as fname, members.mname as mname, members.surname as surname, members.phone_1 as phone_1, members.phone_2 as phone_2, command.name as command, rank.abbr as rank, department.abbr as department, position.abbr as position from members left join command on command.id = members.command left join rank on rank.id = members.rank left join department on department.id = members.department left join position on position.id = members.position where (members.phone_1 = '"+number+"' or members.phone_2 = '"+number+"')";
//            String sql = "select members.fname as fname, members.mname as mname, members.surname as surname, members.phone_1 as phone_1, members.phone_2 as phone_2, command.name as command, rank.abbr as rank, department.abbr as department, position.abbr as position from members left join command on command.id = members.command left join rank on rank.id = members.rank left join department on department.id = members.department left join position on position.id = members.position where members.phone_1 = '07034634717' or members.phone_2 = '0703000000'";
            Cursor res = db.rawQuery(sql, null);
            res.moveToFirst();
            if(res.isAfterLast() == false){
                inflateView();
                contacts x = new contacts();
                x.setFname(res.getString(res.getColumnIndex("fname")));
                x.setMname(res.getString(res.getColumnIndex("mname")));
                x.setSurname(res.getString(res.getColumnIndex("surname")));
                x.setPhone1(res.getString(res.getColumnIndex("phone_1")));
                x.setPhone2(res.getString(res.getColumnIndex("phone_2")));
                x.setRank(res.getString(res.getColumnIndex("rank")));
                x.setCommand(res.getString(res.getColumnIndex("command")));
                x.setPosition(res.getString(res.getColumnIndex("position")));
                x.setDepartment(res.getString(res.getColumnIndex("department")));
                contacts movie = x;
                TextView rank = (TextView) mFloatingView.findViewById(R.id.rank);
                TextView name = (TextView) mFloatingView.findViewById(R.id.name);
                TextView position = (TextView) mFloatingView.findViewById(R.id.position);
                TextView phone = (TextView) mFloatingView.findViewById(R.id.phone);
                rank.setText(movie.getRank());
                name.setText(movie.getName());
                position.setText(movie.getPosition()+ " ("+movie.getDepartment()+")");
                phone.setText(number);
                mydb.close();
            }else{
                mydb.close();
                stopSelf();
            }






    }

    public void inflateView(){
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);



        //Specify the view position
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;        //Initially view will be added to top-left corner
//        params.x = 20;
//        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

//Set the close button
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_button);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                stopSelf();
            }
        });

        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:


                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;


                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) if(mWindowManager != null) mWindowManager.removeView(mFloatingView);
        stopSelf();
    }
}