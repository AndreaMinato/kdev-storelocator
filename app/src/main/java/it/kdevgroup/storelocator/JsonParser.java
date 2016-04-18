package it.kdevgroup.storelocator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrea on 15/04/16.
 */
public class JsonParser {

    private static JsonParser ourInstance = null;
    public static final String SUCCESS = "success";
    public static final String DATA = "data";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String ERROR_CODE = "errorCode";

    public static JsonParser getInstance() {
        if (ourInstance == null) {
            ourInstance = new JsonParser();
        }
        return ourInstance;
    }

    private JsonParser() {
    }

    /**
     * Effettua il parsing di un utente dal JSON di responso dal login
     *
     * @param jsonBody
     * @return User o null se qualcosa non funziona
     * @throws JSONException
     */
    public User parseUserAfterLogin(String jsonBody) throws JSONException {
        User user = null;
        JSONObject jsonObject = new JSONObject(jsonBody);
        if (jsonObject.getBoolean(SUCCESS)) {
            user = new User();
            JSONObject data = jsonObject.getJSONObject(DATA);
            user.setEmail(data.getString(User.EMAIL_KEY));
            user.setId(data.getString(User.ID_KEY));
            user.setSession(data.getString(User.SESSION_KEY));
            user.setSessionTtl(data.getInt(User.SESSION_TTL_KEY));
            user.setName(data.getString(User.NAME_KEY));
            user.setSurname(data.getString(User.SURNAME_KEY));
        }
        return user;
    }

    /**
     * Ritorna un array con [errorMessage, errorCode] se la chiamata non ha avuto successo
     *
     * @param jsonBody
     * @return
     * @throws JSONException
     */
    public String[] getErrorInfoFromResponse(String jsonBody) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonBody);
        String[] error = null;
        if (!jsonObject.getBoolean(SUCCESS)) {
            error = new String[]{jsonObject.getString(ERROR_MESSAGE), jsonObject.getString(ERROR_CODE)};
        }

        return error;
    }

    /**
     * Effettua il parsing degli store letti da un JSON
     *
     * @param jsonBody da dove parsare gli store
     * @return ArrayList<Store>
     * @throws Exception
     */
    public ArrayList<Store> parseStores(String jsonBody) throws JSONException {
        ArrayList<Store> negozi = new ArrayList<>();
        JSONObject jsnobject = new JSONObject(jsonBody);
        JSONArray jsonArray = jsnobject.getJSONArray(DATA);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            negozi.add(parseStore(obj));
        }
        return negozi;
    }

    /**
     * Effettua il parsing di un singolo store dalla lista
     *
     * @param obj JSONObject da parsare
     * @return Store
     * @throws Exception
     */
    public Store parseStore(JSONObject obj) throws JSONException {
        Store store = new Store();
        store.setGUID(obj.getString(Store.KEY_GUID));
        store.setName(obj.getString(Store.KEY_NAME));
        store.setAddress(obj.getString(Store.KEY_ADDRESS));
        store.setPhone(obj.getString(Store.KEY_PHONE));

        return store;
    }

    /**
     * Effettua il parsing dei dettagli dello store, bisogna differenziare le due chiamate perchè
     * questa ha parametri in più
     *
     * @param obj
     * @return
     * @throws JSONException
     */
    public Store parseStoreDetails(JSONObject obj) throws JSONException {
        Store store = new Store();
        store.setGUID(obj.getString(Store.KEY_GUID));
        store.setName(obj.getString(Store.KEY_NAME));
        store.setAddress(obj.getString(Store.KEY_ADDRESS));
        store.setPhone(obj.getString(Store.KEY_PHONE));
        store.setLatitude(obj.getString(Store.KEY_LATITUDE));
        store.setLongitude(Store.KEY_LONGITUDE);
        store.setImage(obj.getString(Store.KEY_IMAGE));
        store.setDescription(obj.getString(Store.KEY_DESCRIPTION));
        store.setThumbnail(Store.KEY_THUMBNAIL);

        JSONArray tagArray = obj.getJSONArray(Store.KEY_TAGS);
        List<String> tmp = new ArrayList<>();
        for (int i = 0; i < tagArray.length(); i++) {
            String tag = tagArray.getString(i);
            tmp.add(tag);
        }
        store.setTags(tmp);

        JSONObject person = new JSONObject(Store.KEY_PERSON);
        store.setEmail(person.getString(Store.KEY_EMAIL));
        store.setFirstName(person.getString(Store.KEY_FIRSTNAME));
        store.setLastName(person.getString(Store.KEY_LASTNAME));

        List<Product> products = new ArrayList<>();
        JSONArray productList = obj.getJSONArray(Store.KEY_PRODUCTS);
        for (int i = 0; i < productList.length(); i++) {
            JSONObject actualProduct = productList.getJSONObject(i);
            products.add(parseProduct(actualProduct));
        }
        store.setProducts(products);
        return store;
    }

    public Product parseProduct(JSONObject obj) throws JSONException {
        Product product = new Product();
        product.setId(obj.getInt(Product.KEY_ID));
        product.setIsAvailable(obj.getBoolean(Product.KEY_ISAVAILABLE));
        product.setName(obj.getString(Product.KEY_NAME));
        product.setPrice(obj.getString(Product.KEY_PRICE));
        return product;
    }

}
