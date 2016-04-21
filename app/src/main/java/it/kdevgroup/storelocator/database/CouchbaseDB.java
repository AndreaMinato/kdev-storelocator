package it.kdevgroup.storelocator.database;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.kdevgroup.storelocator.Store;
import it.kdevgroup.storelocator.User;

/**
 * Created by damiano on 15/04/16.
 */
public class CouchbaseDB {

    private static final String TAG = "CouchbaseDB";
    private static final String TYPE_KEY = "type";
    private static final String USER_TYPE_VALUE = User.class.getSimpleName();
    private static final String STORE_TYPE_VALUE = Store.class.getSimpleName();

    private static final String USER_VIEW = "viewUser";
    private static final String STORES_VIEW = "viewStores";

    private static final String DB_NAME = "storelocatordb";

    private Manager man;
    private Database db;
    private Context ctx;

    public CouchbaseDB(Context c) {
        ctx = c;
        createManager();
        createUserView();
        createStoresView();
    }

    /**
     * Crea il database manager
     */
    private void createManager() {
        try {
            man = new Manager(new AndroidContext(ctx), Manager.DEFAULT_OPTIONS);
            Log.d("MAN Costruttore", "Manager Creato\n");
        } catch (IOException e) {
            Log.d("Eccezione DB", "Impossibile creare l'oggetto Manager");
            e.printStackTrace();
        }
        if (!Manager.isValidDatabaseName(DB_NAME)) {
            Log.d(" controllo nome db ", "Nome del Database errato");

        } else {
            try {
                db = man.getExistingDatabase(DB_NAME);
                if (db == null) {
                    DatabaseOptions options = new DatabaseOptions();
                    options.setCreate(true);
                    db = man.getDatabase(DB_NAME);
                    Log.d("DB costr", "Database creato\n");
                }

            } catch (CouchbaseLiteException e) {
                Log.d("ECCEZIONE", "Impossibile accedere al database\n");
                e.printStackTrace();
            }
        }
    }

    /**
     * Crea la view che da in output una mappa con<br>
     * key      -> email utente (univoca) <br>
     * value    -> oggetto Utente
     */
    private void createUserView() {
        View view = db.getView(USER_VIEW);
        view.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object obj = document.get(TYPE_KEY);

                if (obj != null && obj.equals(USER_TYPE_VALUE)) {
                    User user = new User(document);
                    emitter.emit(User.EMAIL_KEY, user);
                }
            }
        }, "1");
    }

    /**
     * Salva l'utente nel database
     *
     * @param user utente da salvare
     * @throws CouchbaseLiteException
     */
    public void saveUser(User user) throws CouchbaseLiteException {
        Document document = db.getExistingDocument("user");
        Map<String, Object> properties = new HashMap<>();

        // se non ho gia il documento, lo creo e inserisco il type per identificarlo
        if (document == null) {
            document = db.getDocument("user");
            properties.put(TYPE_KEY, USER_TYPE_VALUE);
        }
        // se ho gia il documento, prendo tutte le proprietà
        else {
            properties.putAll(document.getProperties());
        }
        properties.putAll(user.toHashMap());
        document.putProperties(properties);
    }


    /**
     *
     * cancella l'utente dal database
     *
     * @param user utente da cancellare
     * @throws CouchbaseLiteException
     */


    public void deleteUser(User user) throws CouchbaseLiteException {
        Document document = db.getDocument("user");
        document.delete();
    }

    /**
     * Carica l'utente dal database
     *
     * @return
     * @throws CouchbaseLiteException
     */
    public User loadUser() throws CouchbaseLiteException {
        long time = System.currentTimeMillis();

        Document document = db.getExistingDocument("user");
        User user = null;
        if (document != null) {

            Map<String, Object> properties = document.getProperties();
            Map<String, Object> userValues = (Map<String, Object>) properties.get("user");
            user = new User(userValues);
        /*
        Query query = db.getView(USER_VIEW).createQuery();
        query.setMapOnly(true);
        QueryEnumerator queryRows = query.run();

        User user = null;
        for (QueryRow row : queryRows) {
            Object obj = row.getValue();
            user = new User((Map<String, Object>) row.getValue());
        }
        */
        }

        Log.d(TAG, "loadUser: impegati " + (System.currentTimeMillis() - time) + "ms");
        return user;
    }

    /**
     * Crea la view che da in output una mappa con
     * key:     guid
     * value:   attributi dello store (anche il guid e type:Store)
     */
    private void createStoresView() {
        View view = db.getView(STORES_VIEW);
        view.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                for (String key : document.keySet()) {
                    if (key.equals(TYPE_KEY)
                            && document.get(TYPE_KEY).equals(STORE_TYPE_VALUE)) {
                        emitter.emit(document.get(Store.KEY_GUID), document);
                    }
                }
            }
        }, "1");
    }

    /**
     * Salva l'array di store nel DB
     *
     * @param stores
     * @throws CouchbaseLiteException
     */
    public void saveStores(ArrayList<Store> stores) throws CouchbaseLiteException {

        /*
        ID documento    ->  GUID store
        value documento ->  store.toHashMap()
         */
        for (Store store : stores) {
            Document document = db.getExistingDocument(store.getGUID());

            // se non ho gia il documento, lo creo e inserisco il type per identificarlo
            Map<String, Object> properties = new HashMap<>();
            if (document == null) {
                document = db.getDocument(store.getGUID());
                properties.put(TYPE_KEY, STORE_TYPE_VALUE);     // "type": "Store"
                document.putProperties(properties);
            }
            // ottengo le proprietà per la modifica

            properties.putAll(document.getProperties());
            properties.putAll(store.toHashMap());       // aggiungo lo store alle proprietà (se gia presenti, sovrascrivo)
            document.putProperties(properties);         // salvo nel documento
        }
    }

    /**
     * Ottiene un array degli store memorizzati nel database
     *
     * @return
     * @throws CouchbaseLiteException
     */
    @Deprecated
    public ArrayList<Store> getStores() throws CouchbaseLiteException {

        View view = db.getView(STORES_VIEW);
        Query query = view.createQuery();
        query.setMapOnly(true);
        QueryEnumerator rows = query.run();

        if (rows.getCount() == 0)
            return null;

        ArrayList<Store> stores = new ArrayList<>();
        for (QueryRow row : rows) {
            stores.add(new Store(((Map<String, Object>) row.getValue())));
        }

        return stores;
    }

    /**
     * Esegue asincronamente la query per ottenere gli stores
     * @param handler
     * @throws CouchbaseLiteException
     */
    public void getStoresAsync(final IAsyncMapQueryHandler handler) throws CouchbaseLiteException {
        View view = db.getView(STORES_VIEW);
        Query query = view.createQuery();
        query.setMapOnly(true);
        query.runAsync(new Query.QueryCompleteListener() {
            @Override
            public void completed(QueryEnumerator rows, Throwable error) {
                if (rows.getCount() == 0) {
                    handler.handle(null, error);
                    return;
                }

                for (QueryRow row : rows) {
                    handler.handle((Map<String, Object>) row.getValue(), error);
                }
            }
        });
    }
}