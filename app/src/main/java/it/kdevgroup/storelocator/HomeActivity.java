package it.kdevgroup.storelocator;

import android.location.LocationListener;
import android.app.FragmentTransaction;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.couchbase.lite.CouchbaseLiteException;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONException;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;
import it.kdevgroup.storelocator.database.CouchbaseDB;

public class HomeActivity extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener,LogoutAlertDialog.passDatabase,LocationListener {


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

    public interface StoresUpdater {
        void updateStores();
    }

    private static final String TAG = "HomeActivity";
    private static final String SAVE = "onsaved";
    public static final String STORES_KEY_FOR_BUNDLE = "StoresListKeyForBundle";
    public static final String DIALOG="start dialog";

    private PagerManager.PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CouchbaseDB database;
    private boolean goSnack = true;
    private ArrayList<Store> stores;
    private FragmentManager fragManager;
    private LocationManager locationManager;
    private Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pagerAdapter = new PagerManager.PagerAdapter(getSupportFragmentManager(), this);
        database = new CouchbaseDB(getApplicationContext());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        userLocation = new Location("");
        setUserLocation();

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        viewPager = (ViewPager) findViewById(R.id.pager);
        assert viewPager != null;   //conferma che non è null
        viewPager.setAdapter(pagerAdapter);

        ((NavigationView) findViewById(R.id.nav_view)).setItemIconTintList(null);

        fragManager = getSupportFragmentManager();

        if (savedInstanceState != null) {
            goSnack = savedInstanceState.getBoolean(SAVE);
            stores = savedInstanceState.getParcelableArrayList(STORES_KEY_FOR_BUNDLE);
        }

        if (stores == null) {
            stores = new ArrayList<>();
            try {
                /*
                database.getStoresAsync(new IAsyncMapQueryHandler() {
                    @Override
                    public void handle(Map<String, Object> value, Throwable error) {
                        if (value == null) {
                            Log.w(TAG, "handle: value is null", error);
                            if (isNetworkAvailable())
                                getStoresFromServer();
                            return;

                        }
                        stores.add(new Store(value));
                        if (error != null) {
                            error.printStackTrace();
                        }
                    }
                });
                */
                stores = database.getStores();
                if (stores != null)
                    setDistanceFromStores();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
        }

        if (stores == null && isNetworkAvailable()) {
            getStoresFromServer();
        }

        if (stores == null) {
            stores = new ArrayList<>();
            //TODO dialog di errore
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        /**
         * da qua in poi setup di layout
         */
        //snackbar di benvenuto, mostrata una volta sola
        if (goSnack) {
            Snackbar.make(viewPager, "Benvenuto " + User.getInstance().getName(), Snackbar.LENGTH_LONG).show();
            goSnack = false;
        }

        //setup delle tab stile whatsapp
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);

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

        //notifico i frammenti di aggiornarsi per evitare problemi
        //in caso il fragment carichi prima del database (difficile)
        notifyFragments();
    }

    public ArrayList<Store> getStores() {
        return stores;
    }

    public void getStoresFromServer() {    //controlli già verificati prima della chiamata
        Log.i(TAG, "getStoresFromServer");
        ApiManager.getInstance().getStores(User.getInstance().getSession(), new AsyncHttpResponseHandler() {
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

                        //salvo store nel database
                        try {

                            database.saveStores(stores);
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }

                        //TODO la prima volta questa viene chiamata troppo presto e userLocation non è ancora stato valorizzato, da fixare
                        setDistanceFromStores();

                        notifyFragments();
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
            }
        });
    }

    /**
     * Avvisa i fragment di aggiornarsi, se viene chiamata prima
     * della loro creazione il manager torna null ma viene gestito
     */
    public void notifyFragments(){
        Log.i(TAG, "notifyFragments");
        StoresUpdater currentFragment;
        //prendo tutti i fragment castandoli come interfaccia
        //e gli dico di aggiornarsi la lista di negozi

        //Controllo se il fragment è già stato creato, se sì allora gli notifico l'aggiornamento dei negozi
        for(int i = 0; i < pagerAdapter.getCount(); ++i) {
            if( (currentFragment = (StoresUpdater) fragManager.findFragmentByTag("android:switcher:" + R.id.pager + ":" + i)) != null){
                currentFragment.updateStores();
            }
        }
    }

    public void setDistanceFromStores(){
        Log.i(TAG, "setDistanceFromStores");
        for (Store store : stores) {
            Location storeLocation = new Location("");
            storeLocation.setLatitude( Double.parseDouble(store.getLatitude()) );
            storeLocation.setLongitude( Double.parseDouble(store.getLongitude()) );
            store.setLastKnownDistance( Math.round(userLocation.distanceTo(storeLocation) / 1000) );
        }
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
        if (id == R.id.action_settings) {
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
            Intent vInt = new Intent(this, DetailUser.class);
            startActivity(vInt);

        //} else if (id == R.id.nav_preferiti) {

        } else if (id == R.id.nav_impostazioni) {

        } else if (id == R.id.nav_logout) {
            LogoutAlertDialog logoutAlertDialog=new LogoutAlertDialog();
            logoutAlertDialog.show(getFragmentManager(),DIALOG);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public CouchbaseDB couchbaseDB(){ return database;}

    // Metodo che controlla la possibilità di accedere a internet
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Location getUserLocation () {
        return userLocation;
    }

    public void setUserLocation () {
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        try {
            locationManager.requestLocationUpdates(locationProvider, 0, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) throws SecurityException{

        //setto la posizione dell'utente ogni volta che apre il fragment per consentire alle card di scrivere la distanza
        userLocation = location;
        locationManager.removeUpdates(this);
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
        outState.putParcelableArrayList(HomeActivity.STORES_KEY_FOR_BUNDLE, stores);
        outState.putBoolean(SAVE, goSnack);
    }
}
