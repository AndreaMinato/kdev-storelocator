package it.kdevgroup.storelocator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.couchbase.lite.CouchbaseLiteException;

import it.kdevgroup.storelocator.database.CouchbaseDB;

/**
 * Created by Michele on 21/04/2016.
 */
public class LogoutAlertDialog extends DialogFragment {

    public static final String ACTION_LOGOUT = "it.kdevgroup.storelocator.CUSTOM_ACTION_LOGOUT";
    private static final String TAG = "LogoutAlertDialog";

    public interface passDatabase {
        CouchbaseDB couchbaseDB();
    }

    public LogoutAlertDialog() {
        //default constructor
    }

    passDatabase database;
    Context ctx;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof passDatabase) {
            database = (passDatabase) activity;
            ctx = activity.getBaseContext();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        database = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Vuoi uscire da Jesse Store Locator?")
                .setPositiveButton("Esci", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent loginActivity = new Intent(ctx, LoginActivity.class);

//                        logout.addFlags(Intent.FLAG_cle);
                        try {
                            database.couchbaseDB().deleteUser(User.getInstance());
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }
                        startActivity(loginActivity);

                        // manda un broadcast a tutte le activity per dire di terminarsi
                        // causa logout effettuato
                        Intent broadcastLogout = new Intent(ACTION_LOGOUT);
                        getActivity().sendBroadcast(broadcastLogout);
/*
                        if (getActivity() != null) {
                            Log.d(TAG, "onClick: activity not null");
                            if (getActivity().getParent() != null) {
                                Log.d(TAG, "onClick: parent not null");
                                getActivity().getParent().finish();
                            }
                            getActivity().finish();
                        }
*/
                    }
                })
                .setNegativeButton("Indietro", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        onDestroy();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
