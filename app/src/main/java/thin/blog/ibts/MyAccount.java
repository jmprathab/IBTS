package thin.blog.ibts;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import network.CustomRequest;
import network.VolleySingleton;

import static thin.blog.ibts.ApplicationHelper.readFromSharedPreferences;
import static thin.blog.ibts.ApplicationHelper.writeToSharedPreferences;
import static thin.blog.ibts.User.getUserObject;

public class MyAccount extends Fragment {
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.mobile)
    TextView mobile;
    @Bind(R.id.email)
    TextView email;
    @Bind(R.id.address)
    TextView address;
    @Bind(R.id.balance)
    TextView balance;
    @Bind(R.id.qr_code)
    ImageView qrCode;
    int userId;
    String dataName, dataMobile, dataEmail, dataAddress, password;
    double dataBalance;
    String serverMessage;
    int serverSuccess;
    User user = new User();

    public MyAccount() {
        // Required empty public constructor
    }

    public static MyAccount newInstance() {
        MyAccount fragment = new MyAccount();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getUserObject(readFromSharedPreferences(Constants.USER_DATA_OBJECT, ""));
        userId = user.getUserId();
        password = user.getPassword();
        dataName = "Name : " + user.getName();
        dataMobile = "Mobile Number : " + user.getMobile();
        dataEmail = "Email : " + user.getEmail();
        dataAddress = "Address : " + user.getAddress();
        dataBalance = user.getBalance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        ButterKnife.bind(this, view);
        name.setText(dataName);
        mobile.setText(dataMobile);
        email.setText(dataEmail);
        address.setText(dataAddress);
        balance.setText("Balance : ₹ " + dataBalance);
        doProcess();
        return view;
    }

    private void doProcess() {
        final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        Map<String, String> formData = new HashMap<>();
        formData.put("userid", String.valueOf(userId));
        formData.put("password", password);
        final CustomRequest request = new CustomRequest(Request.Method.POST, Constants.FETCH_DETAILS, formData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonParser(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO:Update Pull to Refresh as not refreshing
            }
        });
        requestQueue.add(request);
    }

    private void jsonParser(JSONObject response) {
        try {
            serverSuccess = response.getInt("status");
            if (serverSuccess == 1) {
                String name, mobile, email, address;
                double balance = 0.00;
                name = response.getString("name");
                mobile = response.getString("mobile");
                email = response.getString("email");
                address = response.getString("address");
                balance = Double.valueOf(response.getString("balance"));
                user.setName(name);
                user.setMobile(mobile);
                user.setEmail(email);
                user.setAddress(address);
                user.setBalance(balance);
            }
            serverMessage = response.getString("message");
            finalDecision();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void finalDecision() {
        if (serverSuccess == 1) {
            name.setText("Name : " + user.getName());
            mobile.setText("Mobile Number : " + user.getMobile());
            email.setText("Email : " + user.getEmail());
            address.setText("Address : " + user.getAddress());
            balance.setText("Balance : ₹ " + user.getBalance());
            writeToSharedPreferences(Constants.USER_DATA_OBJECT, User.getUserJson(user));
        } else {
            Snackbar.make(name, serverMessage, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}