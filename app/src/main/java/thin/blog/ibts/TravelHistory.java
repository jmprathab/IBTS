package thin.blog.ibts;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment which displays travel made by the User
 */
public class TravelHistory extends Fragment {
    public TravelHistory() {
    }

    public static TravelHistory newInstance() {
        return new TravelHistory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_travel_history, container, false);
    }

}
