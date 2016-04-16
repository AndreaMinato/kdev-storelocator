package it.kdevgroup.storelocator;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.couchbase.lite.CouchbaseLiteException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONException;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private LinearLayout loginLinearLayout;

    private String email;
    private String password;
    private Button btnLogin;
    private EditText txtUsername;
    private EditText txtPassword;
    private CouchbaseDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.login);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //i dati dagli edittext vanno presi al momento del click, altrimenti sono stringhe vuote
                    if (txtUsername != null) {
                        email = txtUsername.getText().toString();
                    }
                    if (txtPassword != null) {
                        password = txtPassword.getText().toString();
                    }
                    doLogin(email, password);
                }
            });
        }

        txtUsername = (EditText) findViewById(R.id.email);

        txtPassword = (EditText) findViewById(R.id.password);
        if (txtPassword != null) {
            txtPassword.setTypeface(Typeface.DEFAULT);
        }

        loginLinearLayout = (LinearLayout) findViewById(R.id.loginLinearLayout);

        database = new CouchbaseDB(getApplicationContext());

        try {
            User user = database.loadUser();
            if (user != null && !user.isSessionExpired())
                launchHomeActivity();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Effettua un login passando email e password
     *
     * @param email
     * @param password
     */
    private void doLogin(String email, String password) {

        if (email != null && password != null) {
            ApiManager.getInstance().login(
                    email,
                    password,
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            User user = null;
                            String jsonBody = new String(responseBody);
                            String[] error = null;

                            if (statusCode == 200) {
                                // ottengo dei possibili errori
                                try {
                                    error = JsonParser.getInstance().getErrorInfoFromResponse(jsonBody);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // se non ho errori procedo
                                if (error == null) {
                                    try {
                                        user = JsonParser.getInstance().parseUserAfterLogin(jsonBody);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    // se ho letto l'utente, lo salvo nel DB
                                    if (user != null) {
                                        try {
                                            database.saveUser(user);
                                            Log.d(TAG, "salvato utente");

                                        } catch (CouchbaseLiteException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                } else {
                                    Snackbar.make(loginLinearLayout, error[0] + " " + error[1], Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Snackbar.make(loginLinearLayout, getString(R.string.error_onFailure), Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onStart() {
                            super.onStart();
                        }
                    }
            );
        }
    }


    private void launchHomeActivity() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}
