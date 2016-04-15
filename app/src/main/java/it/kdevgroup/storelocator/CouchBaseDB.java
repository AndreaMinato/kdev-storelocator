package it.kdevgroup.storelocator;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

/**
 * Created by damiano on 15/04/16.
 */
public class CouchBaseDB {

    private static final String TAG = "CouchBaseDB";
    private static final String KEY_TYPE = "type";
    private static final String VALUE_TYPE = User.class.getSimpleName();

    private static final String DB_NAME = "StoreLocatorDatabase";

    private Manager man;
    private Database db;
    private Context ctx;

    public CouchBaseDB(Context c) {
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
}
