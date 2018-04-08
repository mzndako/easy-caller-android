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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.app.Config;
import info.androidhive.materialdesign.app.MyApplication;
import info.androidhive.materialdesign.helper.MyDialog;
import info.androidhive.materialdesign.helper.MyFunctions;
import info.androidhive.materialdesign.helper.PrefManager;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = LoginActivity.class.getSimpleName();

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Button btnRequestNext, btnVerifyOtp;
    private EditText inputEmail, inputEmail2, inputMobile, inputOtp;
    private View progressBar;
    private PrefManager pref;
    private TextInputLayout inputLayoutEmail, inputLayoutEmail2;
    private ImageButton btnEditMobile;
    private TextView txtEditMobile;
    private LinearLayout layoutEditMobile;
    private TextView resendOtp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        viewPager = (ViewPager) findViewById(R.id.viewPagerVertical);
        inputEmail = (EditText) findViewById(R.id.login_email);
        inputEmail2 = (EditText) findViewById(R.id.login_email2);
        inputOtp = (EditText) findViewById(R.id.input_otp);

        btnRequestNext = (Button) findViewById(R.id.btn_request_next);

        progressBar = (View) findViewById(R.id.progress_overlay);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_login_email);
        inputLayoutEmail2 = (TextInputLayout) findViewById(R.id.input_layout_login_email2);

        resendOtp = (TextView) findViewById(R.id.resend_otp);

        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputEmail2.addTextChangedListener(new MyTextWatcher(inputEmail2));
        inputOtp.addTextChangedListener(new MyTextWatcher(inputOtp));

        resendOtp.setOnClickListener(this);
        btnRequestNext.setOnClickListener(this);

        // hiding the edit mobile number
//        layoutEditMobile.setVisibility(View.GONE);

        pref = new PrefManager(this);

        // Checking for user session
        // if user is already logged in, take him to main activity
        if (pref.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            finish();
        }


        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        /**
         * Checking if the device is waiting for sms
         * showing the user OTP screen
         */
        if (pref.isWaitingForEmail()) {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_request_next:
                validateEmailForm();
                break;

            case R.id.resend_otp:
                pref.setIsWaitingForEmail(false);
                viewPager.setCurrentItem(0);
                break;
        }
    }
    /**
     * Validating user details form
     */
    private void validateEmailForm() {
        if(!validateEmail(inputEmail,inputLayoutEmail, "Enter valid Email Address"))
            return;

        if(!validateEmail(inputEmail2,inputLayoutEmail2, "Enter valid Confirm Email Address"))
            return;


        // validating empty name and email
        if (!inputEmail.getText().toString().toLowerCase().trim().equalsIgnoreCase(inputEmail2.getText().toString().toLowerCase().trim())) {
            Toast.makeText(getApplicationContext(), "Email Address and Confirm Email Address most be the Same", Toast.LENGTH_LONG).show();
            return;
        }


        MyDialog dialog = new MyDialog(this){
            @Override
            protected void onButtonClick(View v) {
                if(v.getId() == MyDialog.YES_BUTTON)
                    generateOtp(inputEmail.getText().toString());
                super.onButtonClick(v);
            }
        };
        dialog.enableYesAndNo();

        dialog.showDialog("Please confirm your email address\n"+inputEmail.getText()+"\n\nContinue?");

    }

    private boolean validateMe(EditText input, TextInputLayout inputLayout, String msg, Boolean IsEmail) {

        if (input.getText().toString().trim().isEmpty() || (IsEmail && !isValidEmail(input.getText().toString().trim()))) {
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

    private boolean validateEmail(EditText input, TextInputLayout inputLayout, String msg) {
        return validateMe(input, inputLayout, msg, true);
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void generateToken(final String otp) {

        final Context ctx = this;

        progressBar.setVisibility(View.VISIBLE);
        progressBar.requestFocus();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.getLink("generate_token"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                progressBar.setVisibility(View.INVISIBLE);

                try {

                    JSONObject responseObj = new JSONObject(response);

                    final String success = MyFunctions.getJSONString(responseObj,"success");
                    final String error = MyFunctions.getJSONString(responseObj,"error");
                    String show = success.isEmpty()?error:success;

                    progressBar.setVisibility(View.INVISIBLE);

                            if(!success.isEmpty()){
                                pref.setToken(success);
                                pref.setIsWaitingForEmail(false);
                                Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.putExtra("me", "you");
                                startActivity(i);
                                finish();
                            }else {
                                new MyDialog((Activity) ctx) {
                                    @Override
                                    protected void onButtonClick(View v) {
                                        super.onButtonClick(v);

                                    }
                                }.showDialog(error);
                            }


                } catch (JSONException e) {
                    Toast.makeText(ctx,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                }



            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(ctx,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("otp", otp);
                params.put("email", pref.getEmail());

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }


    private void generateOtp(final String email) {

        final Context ctx = this;

        progressBar.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.getLink("generate_otp"), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());


                try {

                    JSONObject responseObj = new JSONObject(response);

                    final String success = MyFunctions.getJSONString(responseObj,"success");
                    final String error = MyFunctions.getJSONString(responseObj,"error");

                    if(!success.isEmpty()){
                        new MyDialog((Activity) ctx){
                            @Override
                            protected void onButtonClick(View v) {
                                pref.setEmail(email);
                                pref.setIsWaitingForEmail(true);
                                super.onButtonClick(v);
                                viewPager.setCurrentItem(1);
                            }
                        }.showDialog("OTP code successfully sent to your mail.\n\nClick OK to Continue");

                    }else {
                        new MyDialog((Activity) ctx) {
                                    @Override
                                    protected void onButtonClick(View v) {
                                        super.onButtonClick(v);

                                    }
                                }.showDialog(error);
                    }

                    // hiding the progress bar

                    progressBar.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ctx,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ctx,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    /**
     * sending the OTP to server and activating the user
     */
    private void verifyOtp() {
        String otp = inputOtp.getText().toString().trim();

        if (!otp.isEmpty()) {
//            Intent grapprIntent = new Intent(getApplicationContext(), HttpService.class);
//            grapprIntent.putExtra("otp", otp);
//            startService(grapprIntent);
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param mobile
     * @return
     */
    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
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
                case R.id.login_email:
                    validateEmail(inputEmail, inputLayoutEmail, getString(R.string.error_invalid_email));
                    break;
                case R.id.login_email2:
                    validateEmail(inputEmail2, inputLayoutEmail2, getString(R.string.error_invalid_email2));
                    break;
                case R.id.input_otp:
                    if(inputOtp.getText().length() >= 5){
                        generateToken(inputOtp.getText().toString());
                    }
                    break;
//                case R.id.input_password:
//                    validatePassword();
//                    break;
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