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
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.Map;

/**
 * Created by damiano on 15/04/16.
 */
public class CouchbaseDB {

    private static final String TAG = "CouchbaseDB";
    private static final String TYPE_KEY = "type";
    private static final String USER_TYPE_VALUE = User.class.getSimpleName();

    private static final String USER_VIEW = "viewUser";

    private static final String DB_NAME = "StoreLocatorDatabase";

    private Manager man;
    private Database db;
    private Context ctx;

    public CouchbaseDB() {

    }

    public CouchbaseDB(Context c) {
        ctx = c;
        createManager();
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

    private void createUserView() {
        View view = db.getView(USER_VIEW);
        view.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object obj = document.get(TYPE_KEY);

                if (obj != null && obj.equals(USER_TYPE_VALUE)) {
                    User user = new User(document);
                    emitter.emit(User.EMAIL_KEY, user.getEmail());
                    emitter.emit(User.EMAIL_KEY, user.getEmail());
                }
            }
        });
    }

    /**
     * Salva un utente nel database
     * @param user utente da salvare
     * @throws CouchbaseLiteException
     */
    public void saveUser(User user) throws CouchbaseLiteException {
        Document document = db.getExistingDocument(user.getEmail());
        if (document != null) {
            // TODO
        } else {
            document = db.getDocument(user.getEmail());
            Map<String, Object> map = user.toHashMap();
            map.put(TYPE_KEY, USER_TYPE_VALUE);
            document.putProperties(map);
        }

    }
}
