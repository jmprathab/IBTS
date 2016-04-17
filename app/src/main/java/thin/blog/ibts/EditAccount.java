package thin.blog.ibts;

import android.content.DialogInterface;
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
import datasets.User;
import network.CustomRequest;
import network.VolleySingleton;

import static thin.blog.ibts.ApplicationHelper.isValidPassword;
import static thin.blog.ibts.ApplicationHelper.lockView;
import static thin.blog.ibts.ApplicationHelper.readFromSharedPreferences;
import static thin.blog.ibts.ApplicationHelper.releaseView;
import static thin.blog.ibts.ApplicationHelper.writeToSharedPreferences;

/**
 * Fragment which is used to edit Account details of the user
 */
public class EditAccount extends Fragment {
    private final User userFromInputData = new User();
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
    ActionProcessButton applyChanges;
    private int serverSuccess;
    private String serverMessage;
    private CountDownTimer countDownTimerFailed;
    private User userFromSharedPreferences = new User();

    public EditAccount() {
    }

    public static EditAccount newInstance() {
        return new EditAccount();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userFromSharedPreferences = User.getUserObject(readFromSharedPreferences(Constants.USER_DATA_OBJECT, ""));
    }

    private void resetUserDataInViews() {
        name.setText(userFromSharedPreferences.getName());
        mobile.setText(userFromSharedPreferences.getMobile());
        email.setText(userFromSharedPreferences.getEmail());
        address.setText(userFromSharedPreferences.getAddress());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_account, container, false);
        ButterKnife.bind(this, view);
        resetUserDataInViews();
        return view;
    }

    @OnClick(R.id.edit_details)
    public void applyDetails() {
        lockView(applyChanges);
        String inputEmail = email.getText().toString();
        String inputAddress = address.getText().toString();
        String inputOldPassword = oldPassword.getText().toString();
        String inputNewPassword = newPassword.getText().toString();
        String inputConfirmNewPassword = confirmNewPassword.getText().toString();
        userFromInputData.setUserId(userFromSharedPreferences.getUserId());
        userFromInputData.setName(userFromSharedPreferences.getName());
        userFromInputData.setBalance(userFromSharedPreferences.getBalance());
        userFromInputData.setEmail(inputEmail);
        userFromInputData.setAddress(inputAddress);
        if (isValidPassword(inputNewPassword)) {
            userFromInputData.setPassword(inputNewPassword);
        } else {
            userFromInputData.setPassword(inputOldPassword);
        }
        if (inputOldPassword.contentEquals("")) {
            Snackbar.make(applyChanges, "Enter Your Password", Snackbar.LENGTH_LONG).show();
            newPassword.setText("");
            confirmNewPassword.setText("");
            releaseView(applyChanges);
            return;
        }
        if (!inputNewPassword.contentEquals("")) {
            if (!inputNewPassword.contentEquals(inputConfirmNewPassword)) {
                newPassword.setText("");
                confirmNewPassword.setText("");
                Snackbar.make(applyChanges, "Passwords should match", Snackbar.LENGTH_LONG).show();
                releaseView(applyChanges);
                return;
            }
        } else {
            inputNewPassword = inputOldPassword;
            userFromInputData.setPassword(inputNewPassword);
        }
        final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        Map<String, String> formData = new HashMap<>();
        formData.put("userid", String.valueOf(userFromSharedPreferences.getUserId()));
        formData.put("email", inputEmail);
        formData.put("address", inputAddress);
        formData.put("oldpassword", inputOldPassword);
        formData.put("newpassword", inputNewPassword);
        final CustomRequest request = new CustomRequest(Request.Method.POST, Constants.EDIT_DETAILS, formData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                jsonParser(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(applyChanges, "Network Error", Snackbar.LENGTH_SHORT).show();
                applyChanges.setProgress(-1);
                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        if (applyChanges != null) {
                            applyChanges.setProgress(0);
                            releaseView(applyChanges);
                        }
                    }
                }.start();
            }
        });
        requestQueue.add(request);
        applyChanges.setProgress(1);
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
        if (serverSuccess == 1 || serverSuccess == 20) {
            writeToSharedPreferences(Constants.USER_DATA_OBJECT, User.getUserJson(userFromInputData));
            applyChanges.setProgress(100);
            lockView(applyChanges);
            if (serverSuccess == 20) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.AlertDialogDark);
                builder.setCancelable(false);
                builder.setTitle("Password Changed");
                builder.setMessage("Password has been changed\nPlease Login again into the application");
                builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        OnFragmentInteractionListener listener = (OnFragmentInteractionListener) getActivity();
                        listener.onFragmentInteraction();
                    }
                });
                builder.create().show();
            }
        } else {
            applyChanges.setProgress(-1);
            countDownTimerFailed = new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    applyChanges.setProgress(0);
                    releaseView(applyChanges);
                }
            };
            countDownTimerFailed.start();

            Snackbar.make(applyChanges, serverMessage, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (countDownTimerFailed != null) {
            countDownTimerFailed.cancel();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
