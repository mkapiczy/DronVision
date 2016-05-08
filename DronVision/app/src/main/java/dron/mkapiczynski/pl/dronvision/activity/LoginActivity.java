package dron.mkapiczynski.pl.dronvision.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import dron.mkapiczynski.pl.dronvision.R;
import dron.mkapiczynski.pl.dronvision.domain.DBDrone;
import dron.mkapiczynski.pl.dronvision.domain.Parameters;
import dron.mkapiczynski.pl.dronvision.utils.SessionManager;
import dron.mkapiczynski.pl.dronvision.message.GetPreferencesMessage;
import dron.mkapiczynski.pl.dronvision.utils.MessageDecoder;
import dron.mkapiczynski.pl.dronvision.utils.InitializationUtils;

/**
 * Widok logowania
 */
public class LoginActivity extends AppCompatActivity {


    /**
     * Asynchroniczne zadanie wysyłające request logowania do serwera
     */
    private UserLoginTask mAuthTask = null;

    /**
     * Elementy UI
     */
    private AutoCompleteTextView mloginTextView;
    private EditText mPasswordTextView;
    private View mProgressView;
    private View mLoginFormView;
    private Button registerButton;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mloginTextView = (AutoCompleteTextView) findViewById(R.id.login);


        mPasswordTextView = (EditText) findViewById(R.id.password);
        mPasswordTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        final Button msignInButton = (Button) findViewById(R.id.email_sign_in_button);
        msignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                    attemptLogin();
                } else {
                    mloginTextView.setError(getString(R.string.error_no_network_connection));
                    mloginTextView.requestFocus();
                }
            }
        });

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        sessionManager = new SessionManager(this);

        String ngrokPort = InitializationUtils.getInitializationNgrokPort();
        sessionManager.setServerHost(ngrokPort);
        //sessionManager.setServerHost("11728");
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mloginTextView.setError(null);
        mPasswordTextView.setError(null);

        // Store values at the time of the login attempt.
        String login = mloginTextView.getText().toString();
        String password = mPasswordTextView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordTextView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordTextView;
            cancel = true;
        }

        // Check for a valid login address.
        if (TextUtils.isEmpty(login)) {
            mloginTextView.setError(getString(R.string.error_field_required));
            focusView = mloginTextView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(login, password);
            mAuthTask.execute((Void) null);
        }
    }


    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    /**
     * Pokazuje widok progressu i ukrywa widok logowania
     * @param show
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Sprawdza, czy aplikacja ma połączenie z internetem
     * @return
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Asynchroniczny task do logowania użytkownika
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String login;
        private final String password;
        private List<DBDrone> assignedDrones;
        private List<DBDrone> trackedDrones;
        private List<DBDrone> visualizedDrones;

        UserLoginTask(String login, String password) {
            this.login = login;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String urlParameters = "login="+login+"&password="+password;
            byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
            int postDataLength = postData.length;

            String requestUrl = Parameters.getLoginRequestUrl(sessionManager);

            URL url = null;
            try {
                url = new URL(requestUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "UTF-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn.setConnectTimeout(2500);
                conn.setReadTimeout(2500);
                conn.setUseCaches(false);


                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);

                int responseCode = conn.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    GetPreferencesMessage getPreferencesMessage = MessageDecoder.decodePreferencesMessage(sb.toString());
                    if(getPreferencesMessage !=null) {
                        assignedDrones = getPreferencesMessage.getAssignedDrones();
                        trackedDrones = getPreferencesMessage.getTrackedDrones();
                        visualizedDrones = getPreferencesMessage.getVisualizedDrones();
                    }
                    return true;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                sessionManager.setLoggedUserLogin(login);
                sessionManager.setAssignedDrones(assignedDrones);
                sessionManager.setTrackedDrones(trackedDrones);
                sessionManager.setVisuazliedDrones(visualizedDrones);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                mPasswordTextView.setError(getString(R.string.error_incorrect_password));
                mPasswordTextView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

