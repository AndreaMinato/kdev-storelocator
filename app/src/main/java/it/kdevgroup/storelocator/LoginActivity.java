package it.kdevgroup.storelocator;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
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
    private static final int PERMISSION_REQUEST_LOCATION = 1;

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

        requestLocationPermission();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Grazie :)", Toast.LENGTH_LONG).show();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    requestLocationPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void requestLocationPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setMessage("Ci serve la tua posizione")
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onDestroy();
                            }
                        })
                        .create()
                        .show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
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
                                    Snackbar.make(loginLinearLayout, error[0] + " " + error[1], Snackbar.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                                progressBar.setVisibility(View.INVISIBLE);
                                Snackbar.make(loginLinearLayout, getString(R.string.error_onFailure), Snackbar.LENGTH_LONG).show();
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
                Snackbar.make(loginLinearLayout, "Inserire tutti i dati", Snackbar.LENGTH_SHORT).show();

            }
        } else {
            Snackbar.make(loginLinearLayout, "Connessione assente", Snackbar.LENGTH_SHORT).show();
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
