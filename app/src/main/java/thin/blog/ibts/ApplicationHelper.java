package thin.blog.ibts;

import android.app.Application;
import android.content.Context;

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

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

}
