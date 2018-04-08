package info.androidhive.materialdesign.helper;

/**
 * Created by HP ENVY on 5/15/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class PrefManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "easycaller";

    // All Shared Preferences Keys
    private static final String KEY_IS_WAITING_FOR_EMAIL = "IsWaitingForEmail";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMAIL = "email";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

        editor = pref.edit();
    }

    public void setIsWaitingForEmail(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_EMAIL, isWaiting);
        editor.commit();
    }

    public boolean isWaitingForEmail() {
        return pref.getBoolean(KEY_IS_WAITING_FOR_EMAIL, false);
    }

    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public void setToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }


    public boolean isLoggedIn() {
        return !getToken().isEmpty();
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

}
