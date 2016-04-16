package it.kdevgroup.storelocator;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by damiano on 15/04/16.
 */
public class ApiManager {

    private static final String LINK_LOGIN = "http://its-bitrace.herokuapp.com/api/public/v2/login";

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
     * @param password non hashata, me ne occupo io
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
        // TODO
        return password;
    }
}
