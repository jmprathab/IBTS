package thin.blog.ibts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import adapters.ListAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import custom.SimpleDividerItemDecoration;
import network.CustomRequest;
import network.VolleySingleton;

public class Lister extends Fragment {
    private static final String LIST_NAME = "list_name";
    @Bind(R.id.main_list_recycler_view)
    RecyclerView mainList;
    @Bind(R.id.detail_list_recycler_view)
    RecyclerView detailsList;
    RequestQueue requestQueue;
    LinkedList<String> mainListData = new LinkedList<>();
    LinkedList<String> detailsListData = new LinkedList<>();
    @Bind(R.id.details_name)
    TextView displayDetailsName;
    @Bind(R.id.title_name)
    TextView displayTitleName;
    private String listName;
    private boolean isBusList = false;

    public Lister() {
    }

    public static Lister newInstance(String param1) {
        Lister fragment = new Lister();
        Bundle args = new Bundle();
        args.putString(LIST_NAME, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listName = getArguments().getString(LIST_NAME);
            if (listName.contentEquals("BUS")) {
                isBusList = true;
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        detailsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainList.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        detailsList.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        refreshData();
    }

    private void refreshData() {
        requestQueue = VolleySingleton.getInstance().getRequestQueue();
        String url;
        if (isBusList) {
            url = Constants.BUS_LIST;
        } else {
            url = Constants.STOP_LIST;
        }
        final CustomRequest request = new CustomRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonParser(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(mainList, "Network Error", Snackbar.LENGTH_SHORT).show();
            }
        });
        request.setTag(Constants.BUS_LIST);
        requestQueue.add(request);
    }

    private void jsonParser(JSONObject response) {
        try {
            int serverSuccess = response.getInt("status");
            if (serverSuccess == 1) {
                JSONArray busNameArray = response.getJSONArray("list");
                for (int i = 0; i < busNameArray.length(); i++) {
                    mainListData.add(busNameArray.getString(i));
                }
                ListAdapter mainListAdapter = new ListAdapter(mainListData, new ListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(String item) {
                        String url;
                        Map<String, String> formData = new HashMap<>();
                        if (isBusList) {
                            displayDetailsName.setText("Bus Route of " + item);
                            formData.put("busname", item);
                            url = Constants.BUS_DETAILS;
                        } else {
                            displayDetailsName.setText("Buses to " + item + " are");
                            formData.put("stopname", item);
                            url = Constants.STOP_DETAILS;
                        }
                        final CustomRequest request = new CustomRequest(Request.Method.POST, url, formData, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                getDetailsFromJson(response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Snackbar.make(mainList, "Error Fetching Data", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                        request.setTag(Constants.BUS_LIST);
                        requestQueue.add(request);
                    }
                });
                mainList.setAdapter(mainListAdapter);
            } else {
                Snackbar.make(mainList, "Network Error", Snackbar.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDetailsFromJson(JSONObject response) {
        detailsListData.clear();
        ListAdapter detailsListAdapter = new ListAdapter(detailsListData, new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
            }
        });
        detailsList.setAdapter(detailsListAdapter);
        try {
            if (response.getInt("status") == 1) {
                JSONArray busDetailsArray = response.getJSONArray("list");
                for (int i = 0; i < busDetailsArray.length(); i++) {
                    detailsListData.add(busDetailsArray.getString(i));
                }

            } else {
                Snackbar.make(mainList, "Error Fetching Details", Snackbar.LENGTH_SHORT).show();
                if (listName.contentEquals("BUS")) {
                    displayDetailsName.setText("Bus Details");
                } else {
                    displayDetailsName.setText("Stop Details");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        detailsListAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lister, container, false);
        ButterKnife.bind(this, view);
        if (isBusList) {
            displayDetailsName.setText("Bus Details");
            displayTitleName.setText("Bus List");
        } else {
            displayDetailsName.setText("Stop Details");
            displayTitleName.setText("Stop List");
        }
        return view;
    }

    public void shareDataAsText() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String busName = (String) displayDetailsName.getText();
        String message = "";
        if (isBusList) {
            message = "Click a Bus From the List to Share details about";
        } else {
            message = "Click a Stop From the List to Share details about";
        }

        if (busName.contentEquals("Bus Details") || busName.contentEquals("Stop Details")) {
            Snackbar.make(mainList, message, Snackbar.LENGTH_SHORT).show();
            return;
        }
        String sharingMessage = "IBTS\n" + busName + "\n\n";
        for (int i = 0; i < detailsListData.size(); i++) {
            sharingMessage += detailsListData.get(i) + "\n";
        }
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharingMessage);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
