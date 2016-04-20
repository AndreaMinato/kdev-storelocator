package it.kdevgroup.storelocator;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.utils.URIBuilder;

public class DetailStoreActivity extends AppCompatActivity {

    public static final String KEY_STORE = "storePresoDalBundle";

    private ImageView imgMap;//dettaglio longitudine e latitudine
    private TextView txtStoreName, txtStoreAddress, txtStorePhone, txtSalesPerson, txtStoreDescription;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private Store store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_store);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgMap = (ImageView) findViewById(R.id.imgMap);
        txtStoreName = (TextView) findViewById(R.id.txtStoreName);
        txtStoreAddress = (TextView) findViewById(R.id.txtStoreAddress);
        txtStorePhone = (TextView) findViewById(R.id.txtStorePhone);
        txtSalesPerson = (TextView) findViewById(R.id.txtSalesPerson);
        txtStoreDescription = (TextView) findViewById(R.id.txtStoreDescriptions);

        Bundle bundle = null;
        if (getIntent() != null) {
            bundle = getIntent().getExtras();
        }
        String guid = null;
        if (bundle != null) {
            guid = bundle.getString(DetailStoreActivity.KEY_STORE);
        }
        if (guid != null) {

            final Bundle finalBundle = bundle;
            ApiManager.getInstance().getStoreDetail(guid, User.getInstance().getSession(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String response = new String(responseBody);
                        JSONObject jsonResponse = new JSONObject(response);
                        String[] error = JsonParser.getInstance().getErrorInfoFromResponse(response);
                        if (error == null) {
                            if (jsonResponse != null) {
                                store = JsonParser.getInstance().parseStoreDetails(jsonResponse.getJSONObject("data"));
                                updateFields(store);
                                imgMap.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            getMap(imgMap, store.getLatitude(), store.getLongitude());
                                        } catch (URISyntaxException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    error.printStackTrace();
                }
            });

        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void updateFields(Store store) {

        txtStoreName.setText(store.getName());
        txtStoreAddress.setText(store.getAddress());
        txtStorePhone.setText(store.getPhone());
        txtSalesPerson.setText(store.getFirstName() + store.getLastName() + '\n' + store.getEmail());
        txtStoreDescription.setText(store.getDescription());
    }

    private void getMap(ImageView imgMap, String... latlong) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder("https://maps.googleapis.com/maps/api/staticmap");
        uriBuilder.addParameter("maptype", "roadmap");
        uriBuilder.addParameter("center", String.format("%s,%s", latlong[0], latlong[1]));
        uriBuilder.addParameter("zoom", "12");
        uriBuilder.addParameter("markers", String.format("%s,%s", latlong[0], latlong[1]));
        uriBuilder.addParameter("size", String.format("%sx%s", imgMap.getWidth(), imgMap.getHeight()));
        uriBuilder.addParameter("scale", "2");
        uriBuilder.addParameter("key", getResources().getString(R.string.google_maps_key));
        String url = uriBuilder.build().toString(); // DEBUGGA PRIMA

        Picasso.with(getApplicationContext())
                .load(uriBuilder.build().toString())
                .resize(imgMap.getWidth(), imgMap.getHeight())
                .centerCrop()
                .into(imgMap);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DetailStore Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://it.kdevgroup.storelocator/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DetailStore Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://it.kdevgroup.storelocator/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
