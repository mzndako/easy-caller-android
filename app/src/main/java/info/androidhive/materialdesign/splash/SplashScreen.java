package info.androidhive.materialdesign.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import info.androidhive.materialdesign.activity.MainActivity;
import info.androidhive.materialdesign.activity.LoginActivity;

import info.androidhive.materialdesign.R;
import info.androidhive.materialdesign.helper.PrefManager;

public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pref = new PrefManager(this);

        if(pref.isLoggedIn()){
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                if(!pref.isLoggedIn()) {
                    Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

                finish();
                // close this activity
            }
        }, SPLASH_TIME_OUT);
    }

}