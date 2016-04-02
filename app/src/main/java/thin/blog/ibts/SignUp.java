package thin.blog.ibts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dd.processbutton.ProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import network.CustomRequest;
import network.VolleySingleton;

import static thin.blog.ibts.ApplicationHelper.isValidMobileNumber;
import static thin.blog.ibts.ApplicationHelper.isValidName;
import static thin.blog.ibts.ApplicationHelper.isValidPassword;
import static thin.blog.ibts.ApplicationHelper.lockView;
import static thin.blog.ibts.ApplicationHelper.releaseView;
import static thin.blog.ibts.ApplicationHelper.writeToSharedPreferences;

/*
* Sign Up Activity which enables Users to create a new Account by providing valid details
* After account has been created user will be taken to LoginActivity in which Users can Login using the credentials which were used to create account
* */

public class SignUp extends AppCompatActivity {
    @Bind(R.id.app_bar)
    Toolbar toolbar;
    @Bind(R.id.name)
    EditText name;
    @Bind(R.id.mobile)
    EditText mobile;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.create_account)
    ProcessButton signUp;
    int serverSuccess;
    String serverMessage;
    String userInputName, userInputMobile, userInputPassword;
    User user = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Account");
        name.setText("Prathab");
        mobile.setText("9025731119");
        password.setText("jaihanuman");
    }

    @OnClick(R.id.create_account)
    public void createAccount() {
        lockView(signUp);
        userInputName = name.getText().toString();
        userInputMobile = mobile.getText().toString();
        userInputPassword = password.getText().toString();
        serverSuccess = 0;
        serverMessage = "Cannot contact server\nCheck your Internet Connection and Try again";
        if (isValidName(userInputName) && isValidMobileNumber(userInputMobile) && isValidPassword(userInputPassword)) {
            signUp.setProgress(1);
            RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
            Map<String, String> formData = new HashMap<>();
            formData.put("name", userInputName);
            formData.put("mobile", userInputMobile);
            formData.put("password", userInputPassword);

            final CustomRequest request = new CustomRequest(Request.Method.POST, Constants.SIGNUP, formData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    jsonParser(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(signUp, "Network Error", Snackbar.LENGTH_SHORT).show();
                    signUp.setProgress(-1);
                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            signUp.setProgress(0);
                            releaseView(signUp);
                        }
                    }.start();
                }
            });
            request.setTag(Constants.SIGNUP);
            requestQueue.add(request);

        } else {
            Snackbar.make(signUp, "Enter Valid Details", Snackbar.LENGTH_SHORT).show();
            signUp.setProgress(-1);
            new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    signUp.setProgress(0);
                    releaseView(signUp);
                }
            }.start();
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this, R.style.AlertDialogDark);
        builder.setCancelable(false);
        if (serverSuccess == 1) {
            user.setMobile(userInputMobile);
            user.setPassword(userInputPassword);
            user.setName(userInputName);
            writeToSharedPreferences(Constants.USER_DATA_OBJECT, User.getUserJson(user));
            writeToSharedPreferences(Constants.SUCCESSFUL_REGISTRATION_HISTORY, true);
            signUp.setProgress(100);
            builder.setTitle("Successfully Registered");
            builder.setMessage(serverMessage);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });
        } else {
            signUp.setProgress(-1);
            builder.setTitle("Cannot Register");
            builder.setMessage(serverMessage);
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    signUp.setProgress(0);
                    releaseView(signUp);
                    dialog.cancel();
                }
            });

        }
        AlertDialog alertDialog;
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }
}
