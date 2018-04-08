package info.androidhive.materialdesign.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
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

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.app.Config;
import info.androidhive.materialdesign.app.MyApplication;
import info.androidhive.materialdesign.helper.MyDialog;
import info.androidhive.materialdesign.helper.MyFunctions;
import info.androidhive.materialdesign.helper.PrefManager;


public class ProfileActivity extends AppCompatActivity {

    private static String TAG = ProfileActivity.class.getSimpleName();

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Button btnRequestNext, btnVerifyOtp;
    private EditText fname, mname, surname, phone1, phone2;
    private View progressBar;
    private PrefManager pref;
    private ImageButton btnEditMobile;
    private TextView txtEditMobile;
    private LinearLayout layoutEditMobile;
    private TextView resendOtp;
    private Spinner rank, command, position;
    private JSONObject rk, cd, pn;
    private boolean openMainActivity = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        fname = (EditText) findViewById(R.id.fname);
        mname = (EditText) findViewById(R.id.mname);
        surname = (EditText) findViewById(R.id.surname);
        phone1 = (EditText) findViewById(R.id.phone1);
        phone2 = (EditText) findViewById(R.id.phone2);
        fname.setEnabled(false);
        mname.setEnabled(false);
        surname.setEnabled(false);

        btnRequestNext = (Button) findViewById(R.id.update);

        progressBar = (View) findViewById(R.id.progress_overlay);


        phone1.addTextChangedListener(new MyTextWatcher(phone1));
        phone2.addTextChangedListener(new MyTextWatcher(phone2));

        rank  = (Spinner) findViewById(R.id.rank);
        position  = (Spinner) findViewById(R.id.position);
        command  = (Spinner) findViewById(R.id.command);


        Bundle extras = getIntent().getExtras();
        String userName;

        if (extras != null) {
            openMainActivity = true;
            // and get whatever type user account id is
        }

        btnRequestNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveForm();
            }
        });

        pref = new PrefManager(this);

        loadForm();
//progressBar.setVisibility(View.VISIBLE);
    }



    private boolean validateMe(EditText input, TextInputLayout inputLayout, String msg, Boolean isPhone) {

        if (input.getText().toString().trim().isEmpty() || (isPhone && !isValidPhoneNumber(input.getText().toString().trim()))) {
            inputLayout.setError(msg);
            requestFocus(input);
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateField(EditText input, TextInputLayout inputLayout, String msg) {
        return validateMe(input, inputLayout, msg, false);
    }

    private boolean validatePhone(EditText input, TextInputLayout inputLayout, String msg) {
        return validateMe(input, inputLayout, msg, true);
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Method initiates the SMS request on the server
     *
     */
    private void loadForm() {

        final String token = pref.getToken();
        final Context ctx = this;


        progressBar.setVisibility(View.VISIBLE);
        progressBar.requestFocus();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.getLink("get_user"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());


                try {

                    JSONObject responseObj = new JSONObject(response);

                    String error = MyFunctions.getJSONString(responseObj, "error");

                    if(!error.isEmpty()){
                        if(error.equalsIgnoreCase("invalid login")){
                            new MyDialog((Activity) ctx){
                                @Override
                                protected void onButtonClick(View v) {
                                    super.dialog.dismiss();
                                    pref.setToken("");
                                    pref.setEmail("");

                                }
                            }.showDialog("Already logout.");
                            return;
                        }else{
                            new MyDialog((Activity) ctx){
                                @Override
                                protected void onButtonClick(View v) {
                                    super.dialog.dismiss();
                                    ((Activity) ctx).finish();
                                }
                            }.showDialog("Cant load profile. Please try again");
                        }
                    }

                    JSONObject members = responseObj.getJSONObject("member");
                    rk = responseObj.getJSONObject("rank");
                    cd = responseObj.getJSONObject("command");
                    pn = responseObj.getJSONObject("position");

                    fname.setText(members.getString("fname"));
                    mname.setText(members.getString("mname"));
                    surname.setText(members.getString("surname"));
                    phone1.setText(members.getString("phone_1"));
                    phone2.setText(members.getString("phone_2"));
                    int mrk = members.getInt("rank");
                    int mcd = members.getInt("command");
                    int mpn = members.getInt("position");


                    //RANK
                    List<String> l = new ArrayList<String>();
                    Iterator<String> x = rk.keys();
                    int pos = 0;
                    int count = 0;
                    while(x.hasNext()){
                        String name = x.next();
                        if(name.equalsIgnoreCase(""+mrk)){
                            pos = count;
                        }
                        String value = rk.getString(name);
                        l.add(value);
                        count++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
                            android.R.layout.simple_spinner_item, l);;
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    rank.setAdapter(adapter);
                    rank.setSelection(pos);

                    //COMMAND
                    l = new ArrayList<String>();
                    x = cd.keys();
                    pos = 0;
                    count = 0;
                    while(x.hasNext()){
                        String name = x.next();
                        if(name.equalsIgnoreCase(""+mcd)){
                            pos = count;
                        }
                        String value = cd.getString(name);
                        l.add(value);
                        count++;
                    }
                    ArrayAdapter mda = new ArrayAdapter<String>(ctx,
                            android.R.layout.simple_spinner_item, l);;
//                    SpinnerAdapter snprAdapter = new SpinnerAdapter(ProfileActivity.this, R.layout.support_simple_spinner_dropdown_item , l);
//
                    mda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    command.setAdapter(mda);
                    mda.notifyDataSetChanged();
                    command.setSelection(pos);

                    //POSITION
                    l = new ArrayList<String>();
                    x = pn.keys();
                    pos = 0;
                    count = 0;
                    while(x.hasNext()){
                        String name = x.next();
                        if(name.equalsIgnoreCase(""+mpn)){
                            pos = count;
                        }
                        String value = pn.getString(name);
                        l.add(value);
                        count++;
                    }
                    adapter = new ArrayAdapter<String>(ProfileActivity.this,
                            android.R.layout.simple_spinner_item, l);;
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    position.setAdapter(adapter);
                    position.setSelection(pos);
                    adapter.notifyDataSetChanged();


                    // hiding the progress bar

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                }
                progressBar.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);

                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }


    private void saveForm() {

        final String token = pref.getToken();
        final Context ctx = this;
        final Activity activity = this;

        if(!isValidPhoneNumber(phone1.getText().toString())){
            new MyDialog(this){
                @Override
                protected void onButtonClick(View v) {
                    requestFocus(phone1);
                    super.onButtonClick(v);
                }
            }.showDialog("Invalid Mobile Number 1");
            return;
        }

        if(!phone2.getText().toString().isEmpty() && !isValidPhoneNumber(phone2.getText().toString())){
            new MyDialog(this){
                @Override
                protected void onButtonClick(View v) {
                    requestFocus(phone2);
                    super.onButtonClick(v);
                }
            }.showDialog("Invalid Mobile Number 2");
            return;
        }


        final Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);
        params.put("phone_1", phone1.getText().toString());
        params.put("phone_2", phone2.getText().toString());
        params.put("rank", getSpinnerPosition(rank,rk) + "");
        params.put("position", getSpinnerPosition(position,pn) + "");
        params.put("command", getSpinnerPosition(command,cd) + "");


        progressBar.setVisibility(View.VISIBLE);


        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.getLink("save_user"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());


                try {

                    JSONObject responseObj = new JSONObject(response);

                    final String success = MyFunctions.getJSONString(responseObj,"success");
                    final String error = MyFunctions.getJSONString(responseObj,"error");
                    String show = success.isEmpty()?error:success;

                    progressBar.setVisibility(View.GONE);
                    new MyDialog(activity){
                        @Override
                        protected void onButtonClick(View v) {
                            if(error.isEmpty()) {
                                if(openMainActivity){
                                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                                activity.finish();
                            }
                            super.onButtonClick(v);
                        }
                    }.showDialog(show);
                } catch (JSONException e) {
                    String result = e.getMessage();
                    progressBar.setVisibility(View.GONE);
                    new MyDialog(activity){
                        @Override
                        protected void onButtonClick(View v) {
                            super.onButtonClick(v);
                        }
                    }.showDialog(result);

                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String result = error.getMessage();
                progressBar.setVisibility(View.GONE);
                new MyDialog(activity){
                    @Override
                    protected void onButtonClick(View v) {
                        super.onButtonClick(v);
                    }
                }.showDialog(result);            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {

                Log.e(TAG, "Posting params: " + params.toString());

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }


    private int getSpinnerPosition(Spinner spin, JSONObject json){
        Iterator<String> x = json.keys();
        int pos = 0;
        int currentPos = spin.getSelectedItemPosition();
        int count = 0;
        while(x.hasNext()){
            String id = x.next();
            if(count == currentPos){
                pos = Integer.parseInt(id);
            }
            count++;
        }
        return pos;
    }

    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param mobile
     * @return
     */
    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{11}$";
        String regEx2 = "^[0-9]{13}$";
        if(mobile.length() == 11 || mobile.length() == 13)
            return true;
        return false;
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.phone1:
//                    validatePhone(phone1, phone1T , "Invalid Phone Number");
                    break;
            }
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.layout_sms;
                    break;
                case 1:
                    resId = R.id.layout_otp;
                    break;
            }
            return findViewById(resId);
        }
    }

}