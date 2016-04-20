package it.kdevgroup.storelocator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailStore extends AppCompatActivity {

    public static final String KEY_STORE = "storePresoDalBundle";

    private ImageView imgMap;//dettaglio longitudine e latitudine
    private TextView txtStoreName, txtStoreAddress, txtStorePhone, txtSalesPerson, txtStoreDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_store);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle;
        if (getIntent() != null) {
            bundle = getIntent().getExtras();
        }

        imgMap = (ImageView) findViewById(R.id.imgMap);
        txtStoreName = (TextView) findViewById(R.id.txtStoreName);
        txtStoreAddress = (TextView) findViewById(R.id.txtStoreAddress);
        txtStorePhone = (TextView) findViewById(R.id.txtStorePhone);
        txtSalesPerson = (TextView) findViewById(R.id.txtSalesPerson);
        txtStoreDescription = (TextView) findViewById(R.id.txtStoreDescriptions);


    }

    private void updateFields(Bundle bundle) {

        Store store= bundle.getParcelable(DetailStore.KEY_STORE);
        txtStoreName.setText("name" + store.getName());
        txtStoreAddress.setText("address" + store.getAddress());
        txtStorePhone.setText("phone" + store.getPhone() );
        txtSalesPerson.setText("firstName/lastName/email" + store.getFirstName() + store.getLastName() + '\n' + store.getEmail());
        txtStoreDescription.setText("description" + store.getDescription());
        // TODO - prendere dal bundle i valori e metterli nelle textview
    }
}
