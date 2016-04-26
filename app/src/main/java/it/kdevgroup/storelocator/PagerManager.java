package it.kdevgroup.storelocator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class PagerManager {

    public static class PagerAdapter extends FragmentPagerAdapter {

//        private StoresListFragment storesListFragment;
//        private MapFragment mapFragment;

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
        //private ArrayList<Store> stores;
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

            homeActivity = (HomeActivity) context;
            cardsAdapter = new EventsCardsAdapter(homeActivity.getStores(), context, homeActivity.getUserLocation());
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
                //stores = savedInstanceState.getParcelableArrayList(HomeActivity.STORES_KEY_FOR_BUNDLE);
                if (User.isNull())
                    User.getInstance().setInstance((User) savedInstanceState.getParcelable(USER_KEY_FOR_BUNDLE));
                Log.d(TAG, "trovati utente e stores nel bundle");
            }

            recyclerView.setAdapter(cardsAdapter);

            //if (stores == null)
            //    stores = homeActivity.getAsyncStores();

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
        public void onSaveInstanceState(Bundle outState) {
            //outState.putParcelableArrayList(HomeActivity.STORES_KEY_FOR_BUNDLE, stores);
            outState.putParcelable(USER_KEY_FOR_BUNDLE, User.getInstance());
            super.onSaveInstanceState(outState);
            Log.d(TAG, "onSaveInstanceState: ");
        }

        public void updateAdapter() {
            cardsAdapter.swapStores(homeActivity.getStores());
        }

        @Override
        public void updateStores() {
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

            homeActivity = (HomeActivity) getActivity();

            if (stores == null)
                stores = homeActivity.getStores();

            //TODO cacare la mappa per visualizzarla anche senza dati se si può
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
                setMarkers();
            }

        }

        @Override
        public void updateStores() {
            try {
                stores = homeActivity.getStores();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        public void setMarkers() {

            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater(null).inflate(R.layout.window_adapter, null);

                    TextView title = (TextView)v.findViewById(R.id.txtTitle);
                    title.setText(marker.getTitle());

                    TextView info = (TextView)v.findViewById(R.id.txtInfo);
                    info.setText(marker.getSnippet());

                    int k = Integer.parseInt(marker.getId().substring(1));

                    TextView phone = (TextView)v.findViewById(R.id.txtPhone);
                    phone.setText( stores.get(k).getPhone());

                    TextView mail = (TextView)v.findViewById(R.id.txtMail);
                    mail.setText( stores.get(k).getEmail());

                    return v;
                }
            });

            //Il marker viene dato con il colore di default rosso, per modificare il suo colore
            //si gioca con l'hue del colore sarurandolo per ottenere quello che si preferisce (37-45) sono tutte tonalità simili all'oro ma questa mi piace
            float hue = 39;
            if (googleMap != null) {
                for (int i = 0; i < stores.size(); i++) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(stores.get(i).getLatitude()), Double.parseDouble(stores.get(i).getLongitude())))
                            .icon(BitmapDescriptorFactory.defaultMarker(hue))
                            .alpha(0.7f)
                            .rotation(15)
                            .snippet(stores.get(i).getAddress())
                            .title(stores.get(i).getName()));
                    //final String thisGUID=stores.get(i).getGUID();
                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            Log.i("click", "cliccato");
                            Intent vIntent = new Intent(getActivity(), DetailStoreActivity.class);
                            Bundle vBundle = new Bundle();
                            int k = Integer.parseInt(marker.getId().substring(1));
                            vBundle.putString(DetailStoreActivity.KEY_STORE, stores.get(k).getGUID());
                            vIntent.putExtras(vBundle);
                            getActivity().startActivity(vIntent);
                        }
                    });

                }
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelableArrayList(HomeActivity.STORES_KEY_FOR_BUNDLE, stores);
        }
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