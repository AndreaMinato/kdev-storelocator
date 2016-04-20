package it.kdevgroup.storelocator;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class PagerManager {

    public static class PagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = new String[]{"Negozi", "Mappa"};
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
                    //TODO lista prodotti ?
                    return PlaceholderFragment.newInstance(i + 1);
                default:
                    return PlaceholderFragment.newInstance(i + 1);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }


    }

    /**
     * Fragment che conterrà la lista
     */
    public static class StoresListFragment extends Fragment implements HomeActivity.StoresUpdater {

        private static final String TAG = "StoresListFragment";
        private static final String USER_KEY_FOR_BUNDLE = "UserKeyForBundle";

        private static Context context;
        private ArrayList<Store> stores;
        private EventsCardsAdapter cardsAdapter;
        private RecyclerView recyclerView;
        private LinearLayoutManager layoutManager;
        private HomeActivity homeActivity;

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

            recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

            if (savedInstanceState != null) {
                stores = savedInstanceState.getParcelableArrayList(HomeActivity.STORES_KEY_FOR_BUNDLE);
                if (User.isNull())
                    User.getInstance().setInstance((User) savedInstanceState.getParcelable(USER_KEY_FOR_BUNDLE));
                Log.d(TAG, "trovati utente e stores nel bundle");
            }

            //castare il context non sembra dare problemi
            homeActivity = (HomeActivity) context; //devo chiamare l'activity perchè il metodo utilizza un metodo di sistema

            if (stores == null)
                stores = homeActivity.getStores();

            if (stores != null && stores.size() > 0) {
                updateAdapter();
            }

            cardsAdapter = new EventsCardsAdapter(stores, context);
            recyclerView.setAdapter(cardsAdapter);

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
            outState.putParcelableArrayList(HomeActivity.STORES_KEY_FOR_BUNDLE, stores);
            outState.putParcelable(USER_KEY_FOR_BUNDLE, User.getInstance());
            super.onSaveInstanceState(outState);
            Log.d(TAG, "onSaveInstanceState: ");
        }

        @Override
        public void onDetach() {
            super.onDetach();
            Log.d(TAG, "onDetach: ");
        }

        public void updateAdapter() {
            cardsAdapter = new EventsCardsAdapter(stores, context);
            recyclerView.swapAdapter(cardsAdapter, true);
        }

        @Override
        public void updateStores(ArrayList<Store> newStores) {
            stores = newStores;
            updateAdapter();
        }
    }

    /**
     * Fragment per la mappa
     */
    public static class MapFragment extends Fragment implements OnMapReadyCallback, HomeActivity.StoresUpdater {

        private GoogleMap googleMap;
        private HomeActivity homeActivity;
        private ArrayList<Store> stores;

        public static MapFragment newInstance() {
            return new MapFragment();
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
                    R.layout.fragment_map, container, false);

            if (savedInstanceState != null) {
                stores = savedInstanceState.getParcelableArrayList(HomeActivity.STORES_KEY_FOR_BUNDLE);
            }

            //castare il context non sembra dia problemi
            homeActivity = (HomeActivity) getActivity(); //devo chiamare l'activity perchè il metodo utilizza un metodo di sistema

            if (stores == null)
                stores = homeActivity.getStores();

            if (stores != null && stores.size() > 0) {
                //TODO chiamare setMarkers();

            }

            //TODO cachare la mappa per visualizzarla anche senza dati se si può
            homeActivity = (HomeActivity) getActivity();

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
            mapFragment.getMapAsync(this);
            return rootView;
        }

        @Override
        public void onMapReady(GoogleMap gm) {
            googleMap = gm;


            try {
                googleMap.setMyLocationEnabled(true); //benedetta sia questa riga, anche se poteva saltare fuori prima (setta il punto blu)
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            if (stores == null)
                stores = homeActivity.getStores();

            if (stores != null && stores.size() > 0) {
                updateStores(stores);

            }

            /*
            homeActivity.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (homeActivity.isNetworkAvailable()) {
                        setLocationRequest(LocationManager.NETWORK_PROVIDER);
                    } else
                        setLocationRequest(LocationManager.GPS_PROVIDER);
                }
            }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            */
        }

        @Override
        public void updateStores(ArrayList<Store> newStores) {
            stores = newStores;
            if (googleMap != null) {
                for (int i = 0; i < stores.size(); i++) {
                    Log.i("onMapReady: ", "Ciclo Markers");
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(stores.get(i).getLatitude()), Double.parseDouble(stores.get(i).getLongitude())))
                            .title(stores.get(i).getName()));
                }
            }
           
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelableArrayList(HomeActivity.STORES_KEY_FOR_BUNDLE, stores);
        }

        /*
        public void setLocationRequest(final String locationProvider){
            final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            try {
                locationManager.requestLocationUpdates(
                        locationProvider,
                        FIVE_SECS,
                        0.5f,       //mezzo metro dalla vecchia location
                        new LocationListener() {

                            @Override
                            public void onLocationChanged(Location location) {
                                Log.i(TAG, "onLocationChanged: lat: " + location.getLatitude());
                                Log.i(TAG, "onLocationChanged: long: " + location.getLongitude());

                                try {
                                    if ( isBetterLocation(location, bestLocation) ) {
                                        bestLocation = location;

                                        //posizione telecamera
                                        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(
                                                new LatLng(
                                                        location.getLatitude(),
                                                        location.getLongitude()),
                                                googleMap.getCameraPosition().zoom);    //prendo lo zoom già presente per non rompere le palle ogni volta, si può zoomare solo la prima volta in caso
                                        googleMap.animateCamera(center);

                                        //tolgo il marker se era già presente
                                        if(userMarker != null)
                                            userMarker.remove();

                                        //setta marker
                                        MarkerOptions userMarkerOptions = new MarkerOptions();
                                        userMarkerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()));
                                        userMarkerOptions.title(User.getInstance().getName());
                                        userMarker = googleMap.addMarker(userMarkerOptions);

                                        Log.i(TAG, "onLocationChanged: animata camera");
                                    }
                                } catch (SecurityException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {
                                //qua si scambiano i provider
                                if (provider.equals(LocationManager.GPS_PROVIDER)) {
                                    Log.i("Provider cambiato: ", "network");
                                    setLocationRequest(LocationManager.NETWORK_PROVIDER);
                                } else {
                                    Log.i("Provider cambiato: ", "gps");
                                    setLocationRequest(LocationManager.GPS_PROVIDER);
                                }
                            }
                        });
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        protected boolean isBetterLocation(Location location, Location currentBestLocation) {
            if (currentBestLocation == null) {
                // A new location is always better than no location
                return true;
            }

            // Check whether the new location fix is more or less accurate
            int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
            boolean isLessAccurate = accuracyDelta > 0;
            boolean isMoreAccurate = accuracyDelta < 0;
            boolean isSignificantlyLessAccurate = accuracyDelta > 200;

            // Check if the old and new location are from the same provider
            boolean isFromSameProvider = isSameProvider(location.getProvider(),
                    currentBestLocation.getProvider());

            // Determine location quality using a combination of timeliness and accuracy
            if (isMoreAccurate) {
                return true;
            } else if (!isLessAccurate) {
                return true;
            } else if (!isSignificantlyLessAccurate && isFromSameProvider) {
                return true;
            }
            return false;
        }

        // Checks whether two providers are the same
        private boolean isSameProvider(String provider1, String provider2) {
            if (provider1 == null) {
                return provider2 == null;
            }
            return provider1.equals(provider2);
        }
        */
    }


    /**
     * Fragment placeholder che verrà sostituito dalla lista di prodotti più avanti
     */
    public static class PlaceholderFragment extends Fragment {
        public static final String ARG_OBJECT = "object";

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