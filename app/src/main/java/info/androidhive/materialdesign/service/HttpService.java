package info.androidhive.materialdesign.service;

/**
 * Created by HP ENVY on 5/20/2017.
 */

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import info.androidhive.materialdesign.activity.MainActivity;
import info.androidhive.materialdesign.app.Config;
import info.androidhive.materialdesign.app.MyApplication;
import info.androidhive.materialdesign.db.database;
import info.androidhive.materialdesign.helper.MyDialog;
import info.androidhive.materialdesign.helper.MyFunctions;
import info.androidhive.materialdesign.helper.PrefManager;
import info.androidhive.materialdesign.R;


/**
 * Created by Ravi on 04/04/15.
 */
public class HttpService extends IntentService {

    PrefManager pref;
    private static String TAG = HttpService.class.getSimpleName();

    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        pref = new PrefManager(this);

        synchronisation s = new synchronisation();
        s.start();

        return super.onStartCommand(intent, flags, startId);
    }

    class synchronisation extends Thread implements Runnable{


        @Override
        public void run() {
            doWork();
        }
    }

    private void doWork(){

        while(true) {
            MyCountDownLatch doneSignal = new MyCountDownLatch(6);
            String[] tables = {"notification", "rank", "command", "position", "department", "members"};
            for (int i = 0; i < tables.length; i++) {
                database db = new database(this);
                String id = db.getLastRowVersion(tables[i]);
                sync_tables(tables[i], id, doneSignal);
            }

            try {
                doneSignal.await();
                if(!doneSignal.isMore())
                    Thread.currentThread().sleep(Config.MAXIMUM_SLEEP);
//                    Thread.currentThread().sleep(5000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class MyCountDownLatch extends CountDownLatch{
        private boolean more = false;
        public MyCountDownLatch(int count) {
            super(count);
        }

        public boolean isMore(){
            return more;
        }

        public void setMoreTrue(){
            more = true;
        }
    }

    private void sync_tables(final String table, final String row_version, final MyCountDownLatch doneSignal) {


        if(!pref.isLoggedIn()){
            return;
        }

        final String token = pref.getToken();



        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL+"/get_table", new Response.Listener<String>()
        {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());


                try {
                    JSONObject resObj = new JSONObject(response);
                    String error = MyFunctions.getJSONString(resObj, "error");

                    if(!error.isEmpty()){
                        if(error.equalsIgnoreCase("invalid login")){
                            pref.setToken("");
                            pref.setEmail("");
                            return;
                        }
                        return;
                    }
                } catch (JSONException e) {

                }


                try {

                    JSONArray responseObj = new JSONArray(response);


                    database db = new database(getApplicationContext());

                    for(int i=0; i < responseObj.length(); i++){
                        try {
                            JSONObject result = responseObj.getJSONObject(i);

                            String id = result.getString("id");

                            Iterator<String> x = result.keys();
                            while(x.hasNext()){
                                String name = x.next();
                                db.put(name, result.getString(name));
                            }

                            if(db.idExit(id,table)){
                                db.update(table,"id = ?", new String[]{id});
                            }else{
                                ContentValues cv = db.getValues();

                                db.insert(table, false);

                                if(table.equalsIgnoreCase("notification")){
                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    int time = 2000;
                                    int status = cv.getAsInteger("urgent");
                                    if(status == 1) {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
                                        mp.start();
                                        sendNotification(cv.getAsString("content"));
                                        time = 5000;
//                                        Intent intent = new Intent(HttpService.this, MainActivity.class);
//                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        startActivity(intent);
                                    }
                                    v.vibrate(time);

                                }
                                db.clear();
                            }



                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                        }
                    }

                    if(responseObj.length() == Config.FETCH_LIMIT){
                        doneSignal.setMoreTrue();
                    }

                    db.close();
                    db = null;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                doneSignal.countDown();
            }
        },
                new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                doneSignal.countDown();
//                Log.e(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("table", table);
                params.put("token", token);
                params.put("limit", Config.FETCH_LIMIT+"");
                params.put("last_row_version", row_version);

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    public void sendNotification(String text) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);

//Create the intent that’ll fire when the user taps the notification//

//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.androidauthority.com/"));
        Intent intent = new Intent(HttpService.this, MainActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);

        mBuilder.setSmallIcon(R.drawable.notification);
        mBuilder.setContentTitle(getString(R.string.app_name));
        mBuilder.setContentText(text);

        NotificationManager mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001, mBuilder.build());
    }

}
