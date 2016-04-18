package it.kdevgroup.storelocator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PagerManager {

    public static class PagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = new String[]{"Negozi", "Mappa", "Prodotti"};
        private Context context;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return StoresListFragment.newInstance(context);
                case 1:
                    return MapFragment.newInstance();
                case 2:
                    //TODO lista prodotti
                    return PlaceholderFragment.newInstance(i + 1);
                default:
                    return PlaceholderFragment.newInstance(i + 1);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }


    /**
     * Fragment che conterrà la lista
     */
    public static class StoresListFragment extends Fragment {

        private static final String TAG = "StoresListFragment";
        private static final String STORES_KEY_FOR_BUNDLE = "StoresListKeyForBundle";
        private static final String USER_KEY_FOR_BUNDLE = "UserKeyForBundle";

        private static Context context;
        private ArrayList<Store> stores;
        private User user;
        private EventsCardsAdapter cardsAdapter;
        private RecyclerView recyclerView;
        private LinearLayoutManager layoutManager;

        public static StoresListFragment newInstance(Context ctx) {
            context = ctx;
            Bundle args = new Bundle();
            StoresListFragment fragment = new StoresListFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_stores_list, container, false);


            if (savedInstanceState != null) {
                stores = savedInstanceState.getParcelableArrayList(STORES_KEY_FOR_BUNDLE);
                user = savedInstanceState.getParcelable(USER_KEY_FOR_BUNDLE);
                Log.d(TAG, "trovati utente e stores nel bundle");
            }

            if (stores == null) {
                stores = new ArrayList<>();
            }

            if (user == null) {
                CouchbaseDB database = new CouchbaseDB(context);
                try {
                    user = database.loadUser();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

            cardsAdapter = new EventsCardsAdapter(stores, context);
            recyclerView.setAdapter(cardsAdapter);

            if (stores.size() == 0) {
                //TODO riciclare codice per controllare la connessione
                ApiManager.getInstance().getStores(user.getSession(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        //TODO controllare casi di errore
                        String jsonBody = new String(responseBody);
                        Log.i("onSuccess response:", jsonBody);
                        try {
                            stores = JsonParser.getInstance().parseStores(jsonBody);
                            cardsAdapter = new EventsCardsAdapter(stores, context);
                            recyclerView.swapAdapter(cardsAdapter, true);
                        } catch (JSONException e) {
                            e.printStackTrace();
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

            // --- LAYOUT MANAGER
            /**
             * @author damiano
             * Qui gioco di cast. GridLayoutManager eredita da LinearLayoutManager, quindi lo dichiaro
             * come Linear ma lo istanzio come Grid, per poter avere disponibili i metodi del Linear, tra
             * i quali quello che mi consente di stabilire qual'è l'ultimo elemento della lista completamente
             * visibile. FIGATTAAA
             */
            int colonne = 1;
            // se lo schermo è orizzontale, allora le colonne da utilizzare sono due
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                colonne = 2;
            }
            layoutManager = new GridLayoutManager(context, colonne, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);


            return rootView;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            Log.d(TAG, "onAttach: ");
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putParcelableArrayList(STORES_KEY_FOR_BUNDLE, stores);
            outState.putParcelable(USER_KEY_FOR_BUNDLE, user);
            super.onSaveInstanceState(outState);
            Log.d(TAG, "onSaveInstanceState: ");
        }

        @Override
        public void onDetach() {
            super.onDetach();
            Log.d(TAG, "onDetach: ");
        }
    }

    /**
     * Fragment per la mappa
     */
    public static class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

        private static final String TAG = "MapFragment";
        public static final String ARG_OBJECT = "object";
        private int section;

        private GoogleMap googleMap;

        public static MapFragment newInstance() {
//            Bundle args = new Bundle();
//            args.putInt(ARG_OBJECT, page);
            MapFragment fragment = new MapFragment();
//            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            section = getArguments().getInt(ARG_OBJECT);

        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_map, container);

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

//            TextView text = (TextView) rootView.findViewById(R.id.sectionText);
//            text.setText("Section " + section);
            return rootView;
        }

        @Override
        public void onMapReady(GoogleMap gm) {
            this.googleMap = gm;

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0,
                        0,
                        new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                                googleMap.moveCamera(center);
                                Log.d(TAG, "onLocationChanged: animata camera");
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
                        });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fragment placeholder che verrà sostituito dalla lista di prodotti più avanti
     */
    public static class PlaceholderFragment extends Fragment {
        public static final String ARG_OBJECT = "object";
        private int section;

        public static PlaceholderFragment newInstance(int page) {
            Bundle args = new Bundle();
            args.putInt(ARG_OBJECT, page);
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            section = getArguments().getInt(ARG_OBJECT);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_stores_list, container, false);
//            TextView text = (TextView) rootView.findViewById(R.id.sectionText);
//            text.setText("Section " + section);
            return rootView;
        }
    }

}