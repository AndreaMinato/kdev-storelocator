package it.kdevgroup.storelocator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;

import cz.msebera.android.httpclient.client.utils.URIBuilder;

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
        // TODO - prendere dal bundle i valori e metterli nelle textview
    }

    private void getMap(ImageView imgMap, String... latlong) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder("https://maps.googleapis.com/maps/api/staticmap");
        uriBuilder.addParameter("maptype", "satellite");
        uriBuilder.addParameter("center", String.format("%s,%s", latlong[0], latlong[1]));
        uriBuilder.addParameter("zoom", "12");
        uriBuilder.addParameter("size", String.format("%sx%s", imgMap.getWidth(), imgMap.getHeight()));
        uriBuilder.addParameter("key", getResources().getString(R.string.google_maps_key));
        String url = uriBuilder.build().toString(); // DEBUGGA PRIMA

        Picasso.with(getApplicationContext())
                .load(uriBuilder.build().toString())
                .fit()
                .into(imgMap);
    }
}
