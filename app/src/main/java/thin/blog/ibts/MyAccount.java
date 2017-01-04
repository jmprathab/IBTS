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

import net.glxn.qrgen.android.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import datasets.User;
import network.CustomRequest;
import network.VolleySingleton;

import static datasets.User.getUserObject;
import static thin.blog.ibts.ApplicationHelper.getSHA256;
import static thin.blog.ibts.ApplicationHelper.readFromSharedPreferences;
import static thin.blog.ibts.ApplicationHelper.writeToSharedPreferences;


/**
 * Fragment which displays details about the User
 */
public class MyAccount extends Fragment {
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.mobile)
    TextView mobile;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.balance)
    TextView balance;
    @BindView(R.id.qr_code)
    ImageView imageQRCode;
    private int userId;
    private String dataName;
    private String dataMobile;
    private String dataEmail;
    private String dataAddress;
    private String password;
    private String qrCodeData;
    private double dataBalance;
    private String serverMessage;
    private int serverSuccess;
    private User user = new User();
    private Unbinder unbinder;

    public MyAccount() {
    }

    public static MyAccount newInstance() {
        return new MyAccount();
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
        qrCodeData = userId + "/" + getSHA256(password + dataMobile + userId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        unbinder = ButterKnife.bind(this, view);
        name.setText(dataName);
        mobile.setText(dataMobile);
        email.setText(dataEmail);
        address.setText(dataAddress);
        balance.setText("Balance : ₹ " + dataBalance);
        imageQRCode.setImageBitmap(QRCode.from(qrCodeData).bitmap());
        fetchDetails();
        return view;
    }

    private void fetchDetails() {
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
                Snackbar.make(name, "Network Error", Snackbar.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);
    }

    private void jsonParser(JSONObject response) {
        try {
            serverSuccess = response.getInt("status");
            if (serverSuccess == 1) {
                String name, mobile, email, address;
                double balance;
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
            qrCodeData = userId + "/" + getSHA256(password + dataMobile + userId);
            imageQRCode.setImageBitmap(QRCode.from(qrCodeData).bitmap());
            writeToSharedPreferences(Constants.USER_DATA_OBJECT, User.getUserJson(user));
        } else {
            Snackbar.make(name, serverMessage, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
