package it.kdevgroup.storelocator;

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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by damiano on 15/04/16.
 */
public class CouchbaseDB {

    private static final String TAG = "CouchbaseDB";
    private static final String TYPE_KEY = "type";
    private static final String USER_TYPE_VALUE = User.class.getSimpleName();

    private static final String USER_VIEW = "viewUser";

    private static final String DB_NAME = "storelocatordb";

    private Manager man;
    private Database db;
    private Context ctx;

    public CouchbaseDB(Context c) {
        ctx = c;
        createManager();
        createUserView();
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
                DatabaseOptions options = new DatabaseOptions();
                options.setCreate(true);
                db = man.getDatabase(DB_NAME);
                //db = man.openDatabase(DB_NAME, options);
                Log.d("DB costr", "Database creato\n");


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
        Document document = db.getExistingDocument(user.getEmail());
        Map<String, Object> properties = new HashMap<>();

        // se non ho gia il documento, lo creo e inserisco il type per identificarlo
        if (document == null) {
            document = db.getDocument(user.getEmail());
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
     * Carica l'utente dal database
     * @return
     * @throws CouchbaseLiteException
     */
    public User loadUser() throws CouchbaseLiteException {
        Query query = db.getView(USER_VIEW).createQuery();
        query.setMapOnly(true);
        QueryEnumerator queryRows = query.run();

        User user = null;
        for (QueryRow row : queryRows) {
            Object obj = row.getValue();
            user = new User((Map<String, Object>) row.getValue());
        }

        return user;
    }
}
