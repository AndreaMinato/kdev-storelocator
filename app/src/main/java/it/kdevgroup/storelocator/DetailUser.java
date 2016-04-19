package it.kdevgroup.storelocator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class DetailUser extends AppCompatActivity {

   TextView title,id,nome,cognome,email,compagnia;

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
        id.setText("id: "+User.getInstance().getId());
        nome.setText("nome: " + User.getInstance().getName());
        cognome.setText("cognome: "+User.getInstance().getSurname());
        email.setText("email: "+User.getInstance().getEmail());
        compagnia.setText("azienda: "+User.getInstance().getCompany());




    }

}



