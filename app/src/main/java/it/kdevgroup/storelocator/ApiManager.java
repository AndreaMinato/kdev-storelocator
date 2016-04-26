package it.kdevgroup.storelocator;

import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by damiano on 15/04/16.
 */
public class ApiManager {

    private static final String LINK_LOGIN = "http://its-bitrace.herokuapp.com/api/public/v2/login";
    private static final String LINK_GET_STORES = "http://its-bitrace.herokuapp.com/api/v2/stores";
    private static final String TAG = "tag";

    private static ApiManager ourInstance;

    public static ApiManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new ApiManager();
        }
        return ourInstance;
    }

    private ApiManager() {

    }

    /**
     * Effettua il btnLogin sul server
     *
     * @param username email
     * @param password me ne occupo io
     * @param handler  handler
     */
    public void login(String username, String password, AsyncHttpResponseHandler handler) {
        final String USERNAME = "email";
        final String PASSWORD = "password";

        RequestParams params = new RequestParams();
        params.add(USERNAME, username);
        params.add(PASSWORD, toBase64Sha512(password));

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.post(LINK_LOGIN, params, handler);
    }

    /**
     * Richiede la lista di negozi con la session passata
     *
     * @param session sessione dell'utente
     */
    public void getAsyncStores(String session, AsyncHttpResponseHandler handler) {
        final String session_key = "x-bitrace-session";

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.addHeader(session_key, session);
        httpClient.get(LINK_GET_STORES, handler);
    }

    /**
     * Richiede la lista di negozi con la session passata
     *
     * @param session sessione dell'utente
     */
    public void getSyncStores(String session, AsyncHttpResponseHandler handler) {
        final String session_key = "x-bitrace-session";

        SyncHttpClient httpClient = new SyncHttpClient();
        httpClient.addHeader(session_key, session);
        httpClient.get(LINK_GET_STORES, handler);
    }

    /**
     * Ottiene il dettaglio del negozio in base all'ID passato come parametro
     *
     * @param storeID id dello store da recuperare
     * @param session sessione dell'utente
     * @param handler handler per gestire la risposta
     */
    public void getStoreDetail(String storeID, String session, AsyncHttpResponseHandler handler) {
        final String session_key = "x-bitrace-session";

        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.addHeader(session_key, session);
        httpClient.get(String.format("%s/%s", LINK_GET_STORES, storeID), handler);
    }

    private String toBase64Sha512(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (md != null) {
            md.update(password.getBytes());
            byte byteData[] = md.digest();
            password = Base64.encodeToString(byteData, Base64.NO_WRAP);
        }
        Log.d(TAG, "" + password);
        return password;
    }


}
