package thin.blog.ibts;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import network.CustomRequest;
import network.VolleySingleton;

import static thin.blog.ibts.ApplicationHelper.isValidPassword;
import static thin.blog.ibts.ApplicationHelper.readFromSharedPreferences;
import static thin.blog.ibts.ApplicationHelper.writeToSharedPreferences;

public class EditAccount extends Fragment {
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.mobile)
    EditText mobile;
    @Bind(R.id.email)
    EditText email;
    @Bind(R.id.address)
    EditText address;
    @Bind(R.id.old_password)
    EditText oldPassword;
    @Bind(R.id.new_password)
    EditText newPassword;
    @Bind(R.id.confirm_new_password)
    EditText confirmNewPassword;
    @Bind(R.id.edit_details)
    ActionProcessButton applyDetails;
    int serverSuccess;
    String serverMessage;
    CountDownTimer failed;
    User user = new User();
    User userInputData = new User();
    private Context activityContext;

    public EditAccount() {
        // Required empty public constructor
    }

    public static EditAccount newInstance() {
        EditAccount fragment = new EditAccount();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.getUserObject(readFromSharedPreferences(Constants.USER_DATA_OBJECT, ""));

    }

    private void resetViews() {
        name.setText(user.getName());
        mobile.setText(user.getMobile());
        email.setText(user.getEmail());
        address.setText(user.getAddress());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_account, container, false);
        ButterKnife.bind(this, view);
        resetViews();
        return view;
    }

    @OnClick(R.id.edit_details)
    public void applyDetails() {
        lockView(applyDetails);
        String inputEmail = email.getText().toString();
        String inputAddress = address.getText().toString();
        String inputOldPassword = oldPassword.getText().toString();
        String inputNewPassword = newPassword.getText().toString();
        String inputConfirmNewPassword = confirmNewPassword.getText().toString();
        userInputData.setUserId(user.getUserId());
        userInputData.setName(user.getName());
        userInputData.setBalance(user.getBalance());
        userInputData.setEmail(inputEmail);
        userInputData.setAddress(inputAddress);
        if (isValidPassword(inputNewPassword)) {
            userInputData.setPassword(inputNewPassword);
        } else {
            userInputData.setPassword(inputOldPassword);
        }
        if (!isValidPassword(inputOldPassword)) {
            Snackbar.make(applyDetails, "Enter Your Password", Snackbar.LENGTH_LONG).show();
            newPassword.setText("");
            confirmNewPassword.setText("");
            releaseView(applyDetails);
            return;
        }
        if (isValidPassword(inputNewPassword)) {
            if (!inputNewPassword.contentEquals(inputConfirmNewPassword)) {
                oldPassword.setText("");
                newPassword.setText("");
                confirmNewPassword.setText("");
                Snackbar.make(applyDetails, "Passwords should match", Snackbar.LENGTH_LONG).show();
                releaseView(applyDetails);
                return;
            }
        } else {
            inputNewPassword = inputConfirmNewPassword = "";
        }
        final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        Map<String, String> formData = new HashMap<>();
        formData.put("userid", String.valueOf(user.getUserId()));
        formData.put("email", inputEmail);
        formData.put("address", inputAddress);
        formData.put("oldpassword", inputOldPassword);
        //formData.put("newpassword", inputNewPassword);
        final CustomRequest request = new CustomRequest(Request.Method.POST, Constants.EDIT_DETAILS, formData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonParser(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(applyDetails, "Network Error", Snackbar.LENGTH_SHORT).show();
                applyDetails.setProgress(-1);
                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        if (applyDetails != null) {
                            applyDetails.setProgress(0);
                            releaseView(applyDetails);
                        }
                    }
                }.start();
            }
        });
        requestQueue.add(request);
        applyDetails.setProgress(1);
    }


    private void jsonParser(JSONObject response) {
        try {
            serverSuccess = response.getInt("status");
            serverMessage = response.getString("message");
            finalDecision();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void finalDecision() {
        if (serverSuccess == 1) {
            writeToSharedPreferences(Constants.USER_DATA_OBJECT, User.getUserJson(userInputData));
            applyDetails.setProgress(1);
            //Add details to Shared Preferences
            new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    applyDetails.setProgress(100);
                    lockView(applyDetails);
                }
            }.start();

        } else {
            applyDetails.setProgress(-1);
            failed = new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    applyDetails.setProgress(0);
                    releaseView(applyDetails);
                }
            };
            failed.start();

            Snackbar.make(applyDetails, serverMessage, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (failed != null) {
            failed.cancel();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activityContext = null;
    }

    private void lockView(View v) {
        v.setClickable(false);
    }

    private void releaseView(View v) {
        v.setClickable(true);
    }
}
