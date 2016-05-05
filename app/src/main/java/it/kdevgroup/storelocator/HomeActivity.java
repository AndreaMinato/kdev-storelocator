package it.kdevgroup.storelocator;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import it.kdevgroup.storelocator.database.CouchbaseDB;
import it.kdevgroup.storelocator.database.IAsyncMapQueryHandler;

interface StoresUpdater {
    void updateStores(ArrayList<Store> stores);
}

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        LogoutAlertDialog.passDatabase,
        LocationListener,
        PagerManager.StoresListFragment.IStoresListFragmentCallbacks {


    // TODO parallax detail, email to da detail, andare dal pin al detail
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Home Page", // TODO: Define a title for the content shown.
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
                "Home Page", // TODO: Define a title for the content shown.
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

    private static final int PERMISSION_REQUEST_LOCATION = 1;
    private static final String TAG = "HomeActivity";
    private static final String SAVE = "onsaved";
    public static final String STORES_KEY_FOR_BUNDLE = "StoresListKeyForBundle";
    public static final String DIALOG = "start dialog";

    private PagerManager.PagerAdapter pagerAdapter;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CouchbaseDB database;
    private boolean goSnack = true;
    private ArrayList<Store> stores;
    private FragmentManager fragManager;
    private PagerManager.StoresListFragment storesListFragment;
    private PersistentProgressDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;

    private LocationManager locationManager;
    private Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //requestLocationPermission();
        //setUserLocation();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            setUserLocation();
        }

        if (savedInstanceState != null) {
            goSnack = savedInstanceState.getBoolean(SAVE);
            stores = savedInstanceState.getParcelableArrayList(STORES_KEY_FOR_BUNDLE);
            notifyFragments();
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pagerAdapter = new PagerManager.PagerAdapter(getSupportFragmentManager(), this);

        // --- SET UP THE VIEW PAGER, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        viewPager = (ViewPager) findViewById(R.id.pager);
        assert viewPager != null;   //conferma che non è null
        viewPager.setAdapter(pagerAdapter);

        // --- INTENT PER AZIONE DI LOGOUT
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

        // --- ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        ((NavigationView) findViewById(R.id.nav_view)).setItemIconTintList(null);

        fragManager = getSupportFragmentManager();

        // --- SETUP TAB STILE WHATSAPP
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);

        storesListFragment = (PagerManager.StoresListFragment)
                fragManager.findFragmentByTag("android:switcher:" +
                        R.id.pager + ":" +
                        PagerManager.PagerAdapter.ID_STORE_LIST_FRAGMENT);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // --- OTTENGO GLI STORE DALLE API O DAL DATABASE
        database = new CouchbaseDB(getApplicationContext());
        if (stores == null) {
            progressDialog = new PersistentProgressDialog();
            progressDialog.show(getSupportFragmentManager(), "tag");
            stores = new ArrayList<>();
            try {
                database.getStoresAsync(new IAsyncMapQueryHandler() {
                    @Override
                    public void handle(Map<String, Object> value, Throwable error) {
                        if (value == null) {
                            Log.w(TAG, "handle: value is null", error);
                            if (isNetworkAvailable())
                                getStoresFromServer(false);
                            return;
                        }
                        stores.add(new Store(value));
                        if (error != null) {
                            error.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (stores != null) {
                            notifyFragments();  //dentro viene lanciato un NullPointerException se uno dei Fragment non è passato per l'onCreateView dove viene valorizzata la variabile homeActivity
                        }
                        progressDialog.dismiss();
                    }
                });

            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }

        if (stores == null) {
            stores = new ArrayList<>();
            //TODO dialog di errore
        }

        // snackbar di benvenuto, mostrata una volta sola
        if (goSnack) {
            Snackbar.make(viewPager, "Benvenuto " + User.getInstance().getName(), Snackbar.LENGTH_LONG).show();
            goSnack = false;
        }
    }

    public void requestLocationPermission() {


            // Should we show an explanation?
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//            new AlertDialog.Builder(this)
//                    .setMessage("Ci serve la tua posizione")
//                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            onDestroy();
//                        }
//                    })
//                    .create()
//                    .show();
//
//        } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

//        }
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

                    setUserLocation();
                    PagerManager.MapFragment mapFragment = (PagerManager.MapFragment) fragManager.findFragmentByTag("android:switcher:" + R.id.pager + ":" + 1);

                    try {
                        mapFragment.getGoogleMap().setMyLocationEnabled(true);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "goditi la tua app inutile", Toast.LENGTH_LONG).show();

                    //requestLocationPermission(); //non entra

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }

        }
    }

    public ArrayList<Store> getStores() {
        return stores;
    }

    public void getStoresFromServer() {
        getStoresFromServer(true);
    }

    /**
     * Ottiene gli store dal server
     *
     * @param async flag che specifica se eseguire il task asincronamente o no
     */
    public void getStoresFromServer(boolean async) {    //controlli già verificati prima della chiamata
        Log.i(TAG, "getStoresFromServer");
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String[] error = null;
                String jsonBody = new String(responseBody);
                Log.i("onSuccess response:", jsonBody);

                // ottengo dei possibili errori
                try {
                    error = JsonParser.getInstance().getErrorInfoFromResponse(jsonBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //se non ho trovato errori nella chiamata parso i negozi
                if (error == null) {
                    try {
                        stores = JsonParser.getInstance().parseStores(jsonBody);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (stores != null && stores.size() > 0) {
                        Collections.sort(stores);
                        //salvo store nel database
                        try {

                            database.saveStores(stores);
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }
                        //TODO la prima volta questa viene chiamata troppo presto e userLocation non è ancora stato valorizzato, da fixare
                        //setDistanceFromStores();

                        //notifyFragments();
                    }
                } else {
                    Snackbar.make(viewPager, error[0] + " " + error[1], Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) { //quando non c'è connessione non si connette al server e la risposta è null
                    String jsonBody = new String(responseBody);
                    Log.i("onFailure response:", jsonBody);
                }

                try {
                    String[] errString = JsonParser.getInstance().getErrorInfoFromResponse(new String(responseBody));
                    if(errString[0].equals("100"))
                        Snackbar.make(viewPager, "Accesso negato al server", Snackbar.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                setRefreshing(false);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                setRefreshing(false);
            }
        };

        if (async)
            ApiManager.getInstance().getAsyncStores(User.getInstance().getSession(), handler);
        else
            ApiManager.getInstance().getSyncStores(User.getInstance().getSession(), handler);
    }



    /**
     * Avvisa i fragment di aggiornarsi, se viene chiamata prima
     * della loro creazione il manager torna null ma viene gestito
     */
    public void notifyFragments() {

        Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {

                Log.i(TAG, "notifyFragments");
                StoresUpdater currentFragment;
                //prendo tutti i fragment castandoli come interfaccia
                //e gli dico di aggiornarsi la lista di negozi

                //Controllo se il fragment è già stato creato, se sì allora gli notifico l'aggiornamento dei negozi
                for (int i = 0; i < pagerAdapter.getCount(); ++i) {
                    if ((currentFragment = (StoresUpdater) fragManager.findFragmentByTag("android:switcher:" + R.id.pager + ":" + i)) != null) {
                        currentFragment.updateStores(stores);
                    }
                }

            }
        };

        mainHandler.post(myRunnable);

    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation() {
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        try {
            locationManager.requestLocationUpdates(locationProvider, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    public void setDistanceFromStores() {
        Log.i(TAG, "setDistanceFromStores");
        for (Store store : stores) {
            Location storeLocation = new Location("");
            storeLocation.setLatitude(Double.parseDouble(store.getLatitude()));
            storeLocation.setLongitude(Double.parseDouble(store.getLongitude()));
            store.setLastKnownDistance(Math.round(userLocation.distanceTo(storeLocation) / 1000));
        }
        Collections.sort(stores);
        notifyFragments();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.alphabetic_sort) {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    Collections.sort(stores, new Comparator<Store>() {
                        @Override
                        public int compare(Store lhs, Store rhs) {
                            return lhs.getName().compareTo(rhs.getName());
                        }
                    });
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    notifyFragments();
                }
            }.execute();

            return true;
        }

        if (id == R.id.distance_sort) {
            Collections.sort(stores);
            notifyFragments();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_user) {
            Intent vInt = new Intent(this, DetailUserActivity.class);
            startActivity(vInt);

            //} else if (id == R.id.nav_preferiti) {

        } else if (id == R.id.nav_impostazioni) {

        } else if (id == R.id.nav_logout) {
            LogoutAlertDialog logoutAlertDialog = new LogoutAlertDialog();
            logoutAlertDialog.show(getFragmentManager(), DIALOG);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public CouchbaseDB couchbaseDB() {
        return database;
    }

    @Override
    public void onStoreListFragmentCreated(PagerManager.StoresListFragment fragment) {
        this.storesListFragment = fragment;
    }

    @Override
    public void onStoreListRefresh() {
        if (isNetworkAvailable()) {
            getStoresFromServer();
        }
    }

    private void setRefreshing(boolean flag) {
        if (storesListFragment != null) {
            storesListFragment.setRefreshing(flag);
        }
    }

    // Metodo che controlla la possibilità di accedere a internet
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onLocationChanged(Location location) throws SecurityException {
        Log.i(TAG, "onLocationChanged: " + location.toString());

        //setto la posizione dell'utente ogni volta che apre il fragment per consentire alle card di scrivere la distanza
        userLocation = location;
        locationManager.removeUpdates(this);
        setDistanceFromStores();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STORES_KEY_FOR_BUNDLE, stores);
        outState.putBoolean(SAVE, goSnack);
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
