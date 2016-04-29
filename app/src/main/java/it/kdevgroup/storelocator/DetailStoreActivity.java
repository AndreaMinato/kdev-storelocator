package it.kdevgroup.storelocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import it.kdevgroup.storelocator.database.CouchbaseDB;

public class DetailStoreActivity extends AppCompatActivity
        implements ObservableScrollViewCallbacks {

    private static final String TAG = "DetailStoreActivity";
    public static final String KEY_STORE = "storePresoDalBundle";

    private ImageView imgMap;//dettaglio longitudine e latitudine
    private TextView txtStoreName, txtStoreAddress, txtStorePhone, txtSalesPerson, txtStoreDescription;
    private BroadcastReceiver broadcastReceiver;
    private CouchbaseDB database;
    private ObservableScrollView scrollView;
    private int flexibleSpaceImageHeight;
    private int actionBarSize;
    private Toolbar toolbar;
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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        scrollView = (ObservableScrollView)findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_map_height);
        Log.i("height", "image: " + flexibleSpaceImageHeight);

        database = new CouchbaseDB(getApplication());

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


        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgMap = (ImageView) findViewById(R.id.imgMap);
        txtStoreName = (TextView) findViewById(R.id.txtStoreName);
        txtStoreAddress = (TextView) findViewById(R.id.txtStoreAddress);

        txtStorePhone = (TextView) findViewById(R.id.txtStorePhone);
        txtStorePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phone = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + store.getPhone()));
                startActivity(phone);
            }
        });

        txtSalesPerson = (TextView) findViewById(R.id.txtSalesPerson);
        txtSalesPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent mail = new Intent(Intent.ACTION_SENDTO);
                mail.setType("text/plain");
                mail.putExtra(Intent.EXTRA_SUBJECT, "");
                mail.putExtra(Intent.EXTRA_TEXT, "");
                mail.setData(Uri.parse("mailto:"+store.getEmail()));
                mail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(mail);
            }
        });

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
            boolean storeAlreadyStored = false;
            try {
                storeAlreadyStored = database.isThisStoreStoredWithDetails(guid);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
                storeAlreadyStored = false;
            }

            if (!storeAlreadyStored) {
                Log.d(TAG, "onCreate: evento non presente nel db");
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
                                    try {
                                        database.saveStore(store);
                                    } catch (CouchbaseLiteException e) {
                                        e.printStackTrace();
                                    }
                                    updateFieldsAndMap(store);
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
            } else {
                try {
                    store = database.getStore(guid);
                    updateFieldsAndMap(store);
                    Log.d(TAG, "onCreate: caricato evento dal db");
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                    store = null;
                }
            }

        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        /*
        ScrollUtils.addOnGlobalLayoutListener(scrollView, new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, flexibleSpaceImageHeight - actionBarSize);

                // If you'd like to start from scrollY == 0, don't write like this:
                //mScrollView.scrollTo(0, 0);
                // The initial scrollY is 0, so it won't invoke onScrollChanged().
                // To do this, use the following:
                //onScrollChanged(0, false, false);

                // You can also achieve it with the following codes.
                // This causes scroll change from 1 to 0.
                //mScrollView.scrollTo(0, 1);
                //mScrollView.scrollTo(0, 0);
            }
        });
        */
    }

    private void updateFieldsAndMap(final Store store) {

        getSupportActionBar().setTitle(store.getName());
        txtStoreAddress.setText(store.getAddress());
        txtStorePhone.setText(store.getPhone());
        txtSalesPerson.setText(store.getFirstName() +" "+store.getLastName() + '\n' + store.getEmail());
        txtStoreDescription.setText(store.getDescription());



        imgMap.post(new Runnable() {
            @Override
            public void run() {
                try {
                    getMap(store.getLatitude(), store.getLongitude());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Imposta la mappa statica che si andrà a chiamare con picasso
     *
     * @param latlong
     * @throws URISyntaxException
     */
    private void getMap(String... latlong) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder("https://maps.googleapis.com/maps/api/staticmap");
        uriBuilder.addParameter("maptype", "roadmap");
        uriBuilder.addParameter("center", String.format("%s,%s", latlong[0], latlong[1]));
        uriBuilder.addParameter("zoom", "12");
        uriBuilder.addParameter("markers", String.format("%s,%s", latlong[0], latlong[1]));
        uriBuilder.addParameter("size", String.format("%sx%s", imgMap.getWidth(), imgMap.getHeight()));
        uriBuilder.addParameter("scale", "2");
        uriBuilder.addParameter("key", getResources().getString(R.string.google_maps_key));
//        String url = uriBuilder.build().toString(); // DEBUGGA PRIMA

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
                "DetailStoreActivity Page", // TODO: Define a title for the content shown.
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

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll,
                                boolean dragging) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / flexibleSpaceImageHeight);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(imgMap, scrollY / 2);
}

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(scrollView.getCurrentScrollY(), false, false);
    }
}
