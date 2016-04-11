package thin.blog.ibts;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import java.security.MessageDigest;

/**
 * This class extends Application
 * This class is contains methods used throughout the application
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
        return !password.contentEquals("");
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
        editor.putString(Constants.USER_DATA_OBJECT, value);
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

    /**
     * Returns a String Object which contains the SHA256 Hash value of the input
     *
     * @param input a String object for which SHA256 should be calculated
     * @return SHA256 of the input String
     */
    public static String getSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}
