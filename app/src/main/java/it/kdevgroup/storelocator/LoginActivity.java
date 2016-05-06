package it.kdevgroup.storelocator;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;

import cz.msebera.android.httpclient.Header;
import it.kdevgroup.storelocator.database.CouchbaseDB;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    MediaPlayer mp;
    private LinearLayout loginLinearLayout;

    private String email;
    private String password;
    private Button btnLogin;
    private EditText txtUsername;
    private EditText txtPassword;
    private ProgressBar progressBar;
    private CouchbaseDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mp = MediaPlayer.create(this, R.raw.error);
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

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        loginLinearLayout = (LinearLayout) findViewById(R.id.loginLinearLayout);

        database = new CouchbaseDB(getApplicationContext());

        try {
            long time = System.currentTimeMillis();
            User user = database.loadUser();
            if (user != null && !user.isSessionExpired()) {
                Log.d(TAG, "onCreate: impiegati " + (System.currentTimeMillis() - time) + "ms");
                launchHomeActivity(user);
            }
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
        if (isNetworkAvailable()) {
            if (email != null && password != null) {
                ApiManager.getInstance().login(
                        email,
                        password,
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                // called when response HTTP status is "200 OK"

                                User user = null;
                                String[] error = null;
                                String jsonBody = new String(responseBody);

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
                                        launchHomeActivity(user);
                                    }
                                } else {
                                    //Snackbar.make(loginLinearLayout, error[0] + " " + error[1], Snackbar.LENGTH_LONG).show();
                                    Snackbar.make(loginLinearLayout, "Non posso farti netrare con queste credenziali", Snackbar.LENGTH_LONG).show();
                                    mp.start();
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                progressBar.setVisibility(View.INVISIBLE);
                                //Snackbar.make(loginLinearLayout, getString(R.string.error_onFailure), Snackbar.LENGTH_LONG).show();
                                Snackbar.make(loginLinearLayout, "Accidenti non ci riesco", Snackbar.LENGTH_LONG).show();
                            }

                            @Override
                            public void onStart() {
                                super.onStart();
                                progressBar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onFinish() {
                                super.onFinish();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                );
            } else {
                Snackbar.make(loginLinearLayout, "Ho bisogno di avere tutti i tuoi dati per accedere", Snackbar.LENGTH_SHORT).show();
                mp.start();
            }
        } else {
            Snackbar.make(loginLinearLayout, "Sembra che no ci sia connessione, puoi verificare?", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void launchHomeActivity(User user) {
        User.getInstance().setInstance(user);
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Metodo che controlla la possibilità di accedere a internet
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
