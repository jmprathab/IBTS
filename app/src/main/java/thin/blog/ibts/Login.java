package thin.blog.ibts;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.EditText;
import android.widget.TextView;

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

import static thin.blog.ibts.ApplicationHelper.isValidMobileNumber;
import static thin.blog.ibts.ApplicationHelper.isValidPassword;
import static thin.blog.ibts.ApplicationHelper.lockView;
import static thin.blog.ibts.ApplicationHelper.releaseView;
import static thin.blog.ibts.ApplicationHelper.writeToSharedPreferences;

public class Login extends AppCompatActivity {
    @Bind(R.id.mobile)
    EditText mobile;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.login)
    ActionProcessButton login;
    String serverMessage;
    int serverSuccess;
    User user = new User();
    CountDownTimer activityStarter;


    @OnClick(R.id.login)
    public void login() {
        lockView(login);
        String userInputMobile, userInputPassword;
        userInputMobile = mobile.getText().toString();
        userInputPassword = password.getText().toString();
        if (isValidMobileNumber(userInputMobile) && isValidPassword(userInputPassword)) {
            //if (true) {
            user.setMobile(userInputMobile);
            user.setPassword(userInputPassword);
            final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
            Map<String, String> formData = new HashMap<>();
            formData.put("mobile", userInputMobile);
            formData.put("password", userInputPassword);
            final CustomRequest request = new CustomRequest(Request.Method.POST, Constants.LOGIN, formData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //Toast.makeText(Login.this, response.toString(), Toast.LENGTH_LONG).show();
                    jsonParser(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(login, "Network Error", Snackbar.LENGTH_SHORT).show();
                    login.setProgress(-1);
                    new CountDownTimer(2000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            login.setProgress(0);
                            releaseView(login);
                        }
                    }.start();
                }
            });
            requestQueue.add(request);
            login.setProgress(1);
        } else {
            Snackbar.make(login, "Enter Valid Details", Snackbar.LENGTH_SHORT).show();
            releaseView(login);
        }
    }

    private void jsonParser(JSONObject response) {
        try {
            serverSuccess = response.getInt("status");
            if (serverSuccess == 1) {
                user.setUserId(Integer.parseInt(response.getString("user_id")));
            }
            serverMessage = response.getString("message");
            finalDecision();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void finalDecision() {
        if (serverSuccess == 1) {
            login.setProgress(100);
            writeToSharedPreferences(Constants.USER_DATA_OBJECT, User.getUserJson(user));
            writeToSharedPreferences(Constants.SUCCESSFUL_LOGIN_HISTORY, true);
            activityStarter = new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    startActivity(new Intent(Login.this, Home.class));
                    finish();
                }
            };
            activityStarter.start();

        } else {
            writeToSharedPreferences(Constants.USER_DATA_OBJECT, "");
            writeToSharedPreferences(Constants.SUCCESSFUL_LOGIN_HISTORY, false);
            login.setProgress(-1);
            new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    login.setProgress(0);
                    releaseView(login);
                }
            }.start();
            Snackbar.make(login, serverMessage, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (readFromSharedPreferences(Constants.SUCCESSFUL_LOGIN_HISTORY, false)) {
        if (false) {
            startActivity(new Intent(Login.this, Home.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mobile.setText("9025731119");
        password.setText("jaihanuman");
    }

    @OnClick(R.id.forgot_password)
    public void forgotPasssword() {
        SpannableString message = new SpannableString("To reset Password visit \nwww.ibts.com/forgotpassword.php");
        Linkify.addLinks(message, Linkify.ALL);
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this, R.style.AlertDialogLight);
        builder.setCancelable(false);
        builder.setTitle("Password Reset");
        builder.setMessage(message);
        builder.setPositiveButton("Okay", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onPause() {
        super.onPause();
        login.setProgress(0);
        if (activityStarter != null) {
            activityStarter.cancel();
        }
    }

    @OnClick(R.id.create_account)
    public void createAccount() {
        startActivity(new Intent(Login.this, SignUp.class));
    }
}
