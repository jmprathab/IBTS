package thin.blog.ibts;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    String userInputName, userInputMobile, userInputPassword;
    int serverSuccess;
    String serverMessage;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static boolean isValidPassword(String password) {
        if (password.contentEquals("")) {
            return false;
        }
        return true;
    }

    public static boolean isValidName(String name) {
        return !name.contentEquals("");
    }

    public static boolean isValidMobile(String mobile) {
        return !mobile.contentEquals("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS_USER_DATA, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //for testing
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
        if (isValidName(userInputName) && isValidMobile(userInputMobile) && isValidPassword(userInputPassword)) {
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
            editor.putString(Constants.USER_DATA_MOBILE, userInputMobile);
            editor.putString(Constants.USER_DATA_PASSWORD, userInputPassword);
            editor.putString(Constants.USER_DATA_NAME, userInputName);
            editor.putBoolean(Constants.SUCCESSFUL_REGISTRATION_HISTORY, true);
            editor.apply();
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

    private void lockView(View v) {
        v.setClickable(false);
    }

    private void releaseView(View v) {
        v.setClickable(true);
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
