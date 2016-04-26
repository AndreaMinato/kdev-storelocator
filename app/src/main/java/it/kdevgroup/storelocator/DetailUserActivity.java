package it.kdevgroup.storelocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import it.kdevgroup.storelocator.database.CouchbaseDB;

public class DetailUserActivity extends AppCompatActivity implements LogoutAlertDialog.passDatabase {

    private static final String TAG = "DetailUserActivity";

    private TextView title, id, nome, cognome, email, compagnia;
    private Button logout;
    private CouchbaseDB database;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // broadcast receiver per terminare l'activity in caso di logout
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LogoutAlertDialog.ACTION_LOGOUT);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "onReceive: richiesto logout, termino activity");
                finish();
            }
        };
        this.registerReceiver(broadcastReceiver, intentFilter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (TextView) findViewById(R.id.txtTitle);
        id = (TextView) findViewById(R.id.txtId);
        nome = (TextView) findViewById(R.id.txtNome);
        cognome = (TextView) findViewById(R.id.txtCognome);
        email = (TextView) findViewById(R.id.txtEmail);
        compagnia = (TextView) findViewById(R.id.txtCompany);

        title.setText(User.getInstance().getName() + " " + User.getInstance().getSurname());
        id.setText(User.getInstance().getId());
        nome.setText(User.getInstance().getName());
        cognome.setText(User.getInstance().getSurname());
        email.setText(User.getInstance().getEmail());
        compagnia.setText(User.getInstance().getCompany());

        database = new CouchbaseDB(getApplicationContext());

        logout = (Button) findViewById(R.id.btnLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutAlertDialog logoutAlertDialog = new LogoutAlertDialog();
                logoutAlertDialog.show(getFragmentManager(), "logout");
            }
        });

    }

    public CouchbaseDB couchbaseDB() {
        return database;
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}



