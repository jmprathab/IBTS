package network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import thin.blog.ibts.ApplicationHelper;

/**
 * Singleton class which returns a RequestQueue instance of volley
 */
public class VolleySingleton {
    private static VolleySingleton sInstance = null;
    private final RequestQueue mRequestQueue;

    private VolleySingleton() {
        mRequestQueue = Volley.newRequestQueue(ApplicationHelper.getMyApplicationContext());

    }

    public static VolleySingleton getInstance() {
        if (sInstance == null) {
            sInstance = new VolleySingleton();
        }
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
