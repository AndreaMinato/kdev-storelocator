package it.kdevgroup.storelocator;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by andrea on 15/04/16.
 */
public class JsonParser {

    private static JsonParser ourInstance = null;
    public static final String DATA = "data";

    public static JsonParser getInstance() {
        if (ourInstance == null) {
            ourInstance = new JsonParser();
        }
        return ourInstance;
    }

    private JsonParser(){
        Log.d("JSONParser: ","costruito");
    }

    public ArrayList<Store> parseJsonResponse(String jsonBody) throws Exception {
        ArrayList<Store> negozi = new ArrayList<>();
        JSONObject jsnobject = new JSONObject(jsonBody);
        JSONArray jsonArray = jsnobject.getJSONArray(DATA);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            negozi.add(parseJsonObject(obj));
        }
        return negozi;
    }

    public Store parseJsonObject(JSONObject obj) throws Exception {
        Store store = new Store();
        store.setGUID(obj.getString(Store.KEY_GUID));
        store.setName(obj.getString(Store.KEY_NAME));
        store.setAddress(obj.getString(Store.KEY_ADDRESS));
        store.setDescription(obj.getString(Store.KEY_DESCRIPTION));

        return store;
    }


}
