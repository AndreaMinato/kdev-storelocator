package it.kdevgroup.storelocator;

import android.content.Context;
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

import com.couchbase.lite.CouchbaseLiteException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PagerManager {

    public static class PagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = new String[] { "Negozi", "Mappa", "Prodotti" };
        private Context context;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    return StoresListFragment.newInstance(context);
                case 1:
                    //TODO mappa
                    //return MapFragment.newInstance();
                    break;
                case 2:
                    //TODO lista prodotti
                    return PlaceholderFragment.newInstance(i + 1);
                default:
                    break;
            }
            return PlaceholderFragment.newInstance(i + 1);
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
     * TODO implementare ciclo di vita per non effettuare la chiamata ad ogni slide
     */
    public static class StoresListFragment extends Fragment {

        private static Context context;
        private ArrayList<Store> stores;
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

            stores = new ArrayList<>();
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

            CouchbaseDB database = new CouchbaseDB(context);
            User user = null;
            String session = "b";
            try {
                user = database.loadUser();
                session = user.getSession();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }

            //TODO FIXARE SALVATAGGIO DEI DATI NEL DATABASE, PER ORA BISOGNA FORZARE LA SESSIONE
            session = "febc3b43-a320-49ef-8d35-c7117fae837f";

            ApiManager.getInstance().getStores(session, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
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
                    String jsonBody = new String(responseBody);
                    Log.i("onFailure response:", jsonBody);
                }
            });

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
    }

    /**
     * Fragment per la mappa
     */
    public static class MapFragment extends Fragment {
        public static final String ARG_OBJECT = "object";
        private int section;

        public static MapFragment newInstance(int page) {
            Bundle args = new Bundle();
            args.putInt(ARG_OBJECT, page);
            MapFragment fragment = new MapFragment();
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
                    R.layout.fragment_placeholder, container, false);
            TextView text = (TextView)rootView.findViewById(R.id.sectionText);
            text.setText("Section " + section);
            return rootView;
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
                    R.layout.fragment_placeholder, container, false);
            TextView text = (TextView)rootView.findViewById(R.id.sectionText);
            text.setText("Section " + section);
            return rootView;
        }
    }

}