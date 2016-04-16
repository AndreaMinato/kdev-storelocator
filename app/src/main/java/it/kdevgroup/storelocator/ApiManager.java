package it.kdevgroup.storelocator;

import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by damiano on 15/04/16.
 */
public class ApiManager {

    private static final String LINK_LOGIN = "http://its-bitrace.herokuapp.com/api/public/v2/login";
    private static final String TAG="tag";

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
     * @param username
     * @param password me ne occupo io
     * @param handler  hangler
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
        Log.d(TAG,""+password);
        return password;
        // TODO rivedere il codice perché ritorna un valore diverso da quello di approsto.com :(
        //password:tsac
        // output del codice:z4PhNX7vuL3xVChQ1m2AB9Yg5AULVxXcg/SpIdNs6c5H0NE8XYXysP+DGNKHfuwvY7kxvUdBeoGlODJ6+SfaPg==
        //output di approsto:AkL6KhBcibHLVGZbs/JyBJqMCGB6nDLK/0ovxGZHojt6EepTxpdfygqKsIWz3Q4FS4wyHY4cIrP1W8nHAd8F4A==
    }
}
