package it.kdevgroup.storelocator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import it.kdevgroup.storelocator.database.CouchbaseDB;

public class DetailUser extends AppCompatActivity implements LogoutAlertDialog.passDatabase {

    TextView title,id,nome,cognome,email,compagnia;

    Button logout;

    CouchbaseDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title=(TextView)findViewById(R.id.txtTitle);
        id=(TextView)findViewById(R.id.txtId);
        nome=(TextView)findViewById(R.id.txtNome);
        cognome=(TextView)findViewById(R.id.txtCognome);
        email=(TextView)findViewById(R.id.txtEmail);
        compagnia=(TextView)findViewById(R.id.txtCompany);

        title.setText(User.getInstance().getName()+" "+User.getInstance().getSurname());
        id.setText(User.getInstance().getId());
        nome.setText( User.getInstance().getName());
        cognome.setText(User.getInstance().getSurname());
        email.setText(User.getInstance().getEmail());
        compagnia.setText(User.getInstance().getCompany());

        database=new CouchbaseDB(getApplicationContext());

        logout=(Button)findViewById(R.id.btnLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutAlertDialog logoutAlertDialog=new LogoutAlertDialog();
                logoutAlertDialog.show(getFragmentManager(),"logout");
            }
        });

    }

    public CouchbaseDB couchbaseDB(){ return database;}

}



