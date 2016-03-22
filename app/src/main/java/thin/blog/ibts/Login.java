package thin.blog.ibts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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


public class Login extends AppCompatActivity {
    @Bind(R.id.mobile)
    EditText mobile;
    @Bind(R.id.password)
    EditText password;
    @Bind(R.id.login)
    ActionProcessButton login;
    String userInputMobile, userInputPassword;
    String serverMessage;
    int serverSuccess;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int userDataUserId;


    public static boolean isValidPassword(String password) {
        if (password.contentEquals("")) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.login)
    public void login() {
        startActivity(new Intent(Login.this, Home.class));
        finish();
        lockView(login);
        userInputMobile = mobile.getText().toString();
        userInputPassword = password.getText().toString();
        //if (isValidMobileNumber(userInputMobile) && isValidPassword(userInputPassword)) {
        if (true) {
            final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
            Map<String, String> formData = new HashMap<>();
            formData.put("mobile", userInputMobile);
            formData.put("password", userInputPassword);
            final CustomRequest request = new CustomRequest(Request.Method.POST, Constants.LOGIN, formData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
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
                userDataUserId = Integer.parseInt(response.getString("user_id"));
            }
            serverMessage = response.getString("message");
            finalDecision();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void finalDecision() {
        if (serverSuccess == 1) {
            login.setProgress(1);
            editor.putInt(Constants.USER_DATA_USER_ID, userDataUserId);
            editor.putString(Constants.USER_DATA_MOBILE, userInputMobile);
            editor.putString(Constants.USER_DATA_PASSWORD, userInputPassword);
            editor.putBoolean(Constants.SUCCESSFUL_LOGIN_HISTORY, true);
            editor.apply();
            new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    login.setProgress(100);
                    startActivity(new Intent(Login.this, Home.class));
                    //finish();
                }
            }.start();

        } else {
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
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS_USER_DATA, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean(Constants.SUCCESSFUL_LOGIN_HISTORY, false)) {
            //if (true) {
            //startActivity(new Intent(LoginActivity.this, Home.class));
            //finish();
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        mobile.setText("9025731119");
        password.setText("jaihanuman");
    }


    private void lockView(View v) {
        v.setClickable(false);
    }

    private void releaseView(View v) {
        v.setClickable(true);
    }


    @OnClick(R.id.forgot_password)
    public void forgotPasssword() {
        SpannableString message = new SpannableString("To reset Password visit \nwww.ibts.com/reset.php");
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

    @OnClick(R.id.create_account)
    public void createAccount() {
        startActivity(new Intent(Login.this, SignUp.class));
    }
}
