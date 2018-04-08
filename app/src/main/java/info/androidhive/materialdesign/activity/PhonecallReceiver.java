package info.androidhive.materialdesign.activity;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Service;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.adapter.ContactAdapter;
import info.androidhive.materialdesign.adapter.contacts;
import info.androidhive.materialdesign.db.DBHelper;
import info.androidhive.materialdesign.db.database;
import info.androidhive.materialdesign.helper.PrefManager;
import info.androidhive.materialdesign.service.FloatingViewService;

public class PhonecallReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private PrefManager pref;
    private static String savedNumber;  //because the passed incoming is only valid in ringing

    Intent service = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        pref = new PrefManager(context);
        if(!pref.isLoggedIn())
            return;


        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {

            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

//            Toast.makeText(context,"outgoing " + savedNumber,Toast.LENGTH_SHORT).show();



        }
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }


            onCallStateChanged(context, state, number);
        }
    }

    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, Date start){
        if(service == null) {
            service = new Intent(ctx, FloatingViewService.class);
            service.putExtra("phone_number",number);
            ctx.startService(service);
        }
    }
    protected void onOutgoingCallStarted(Context ctx, String number, Date start){
        if(service == null) {
            service = new Intent(ctx, FloatingViewService.class);
            service.putExtra("phone_number",number);
            ctx.startService(service);
        }
    }
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end){
        if(service == null)
            service = new Intent(ctx, FloatingViewService.class);
        ctx.stopService(service);
        log_me(ctx,number,start,end,1);
    }
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end){
        if(service == null)
            service = new Intent(ctx, FloatingViewService.class);
        ctx.stopService(service);
        log_me(ctx,number,start,end,3);
    }
    protected void onMissedCall(Context ctx, String number, Date start){
        log_me(ctx,number,start,new Date(),2);
    }
    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up

    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallStarted(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime);
                }
                else if(isIncoming){
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                else{
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }

    //type 2 = missed calls, 3 outgoing, 1 incoming
    public void log_me(Context ctx, String number, Date start, Date end, int type){
        number = number.replace(" ", "").replace("+","");
        if(number.indexOf("0") == 0){
            number = "234"+number.substring(1);
        }
        DBHelper mydb = new DBHelper(ctx);
        SQLiteDatabase db = mydb.getReadableDatabase();
        String sql = "select members.id,members.command as cid, members.rank as rid, members.position as pid, members.department as did, members.fname as fname, members.mname as mname, members.surname as surname, members.phone_1 as phone_1, members.phone_2 as phone_2, command.name as command, rank.abbr as rank, department.abbr as department, position.abbr as position from members left join command on command.id = members.command left join rank on rank.id = members.rank left join department on department.id = members.department left join position on position.id = members.position where members.phone_1 = '"+number+"' or members.phone_2 = '"+number+"'";
        Cursor res = db.rawQuery(sql, null);
        res.moveToFirst();
        if(res.isAfterLast() == false){
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
            mydb.close();

            long s = (long) start.getTime();
            long e = (long) end.getTime();

            database d = new database(ctx);
            d.put("member",res.getString(res.getColumnIndex("id")));
            d.put("command",res.getString(res.getColumnIndex("cid")));
            d.put("department",res.getString(res.getColumnIndex("did")));
            d.put("rank",res.getString(res.getColumnIndex("rid")));
            d.put("position",res.getString(res.getColumnIndex("pid")));
            d.put("start",s+"");
            d.put("end",e+"");
            d.put("type",type + "");
            d.put("phone",number);
            d.insert("recent");
            d.close();
        }else{
            mydb.close();
        }
    }
}