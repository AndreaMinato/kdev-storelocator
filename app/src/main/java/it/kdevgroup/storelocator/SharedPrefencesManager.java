package it.kdevgroup.storelocator;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferencesManager per un eventuale sezione impostazioni.
 * Created by damiano on 15/04/16.
 */
public class SharedPrefencesManager {

    private static SharedPrefencesManager ourInstance;
    private static final String SP_NAME = "StoreLocatorSP";

    private SharedPreferences sharedPreferences;

    public static SharedPrefencesManager getInstance(Context ctx) {
        if (ourInstance == null) {
            ourInstance = new SharedPrefencesManager(ctx);
        }
        return ourInstance;
    }

    private SharedPrefencesManager(Context ctx) {
//        sharedPreferences = ctx.getSharedPreferences(SP_NAME, 0);
    }
}
