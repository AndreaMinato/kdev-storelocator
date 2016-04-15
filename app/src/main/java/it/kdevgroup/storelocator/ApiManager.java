package it.kdevgroup.storelocator;

/**
 * Created by damiano on 15/04/16.
 */
public class ApiManager {
    private static ApiManager ourInstance;

    public static ApiManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new ApiManager();
        }
        return ourInstance;
    }

    private ApiManager() {

    }

    public
}
