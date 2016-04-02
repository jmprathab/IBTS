package thin.blog.ibts;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

/**
 * ApplicationHelper class which extends Application
 * This class is used by Volley Library
 */
public class ApplicationHelper extends Application {
    private static ApplicationHelper sInstance;

    public static ApplicationHelper getMyApplicationInstance() {
        return sInstance;
    }

    public static Context getMyApplicationContext() {
        return sInstance.getApplicationContext();
    }

    public static boolean isValidPassword(String password) {
        if (password.contentEquals("")) {
            return false;
        }
        return true;
    }

    public static boolean isValidName(String name) {
        return !name.contentEquals("");
    }


    public static boolean isValidMobileNumber(String mobile) {
        return android.util.Patterns.PHONE.matcher(mobile).matches();
    }

    public static void writeToSharedPreferences(String name, String value) {
        SharedPreferences sharedPreferences = getMyApplicationContext().getSharedPreferences(Constants.SHARED_PREFS_USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public static void writeToSharedPreferences(String name, boolean value) {
        SharedPreferences sharedPreferences = getMyApplicationContext().getSharedPreferences(Constants.SHARED_PREFS_USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    public static boolean readFromSharedPreferences(String name, boolean defaultValue) {
        SharedPreferences sharedPreferences = getMyApplicationContext().getSharedPreferences(Constants.SHARED_PREFS_USER_DATA, MODE_PRIVATE);
        return sharedPreferences.getBoolean(name, defaultValue);
    }

    public static String readFromSharedPreferences(String name, String defaultValue) {
        SharedPreferences sharedPreferences = getMyApplicationContext().getSharedPreferences(Constants.SHARED_PREFS_USER_DATA, MODE_PRIVATE);
        return sharedPreferences.getString(name, defaultValue);
    }

    public static void lockView(View v) {
        v.setClickable(false);
    }

    public static void releaseView(View v) {
        v.setClickable(true);
    }

    public static void L(String message) {
        Log.d("prathab", message);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
