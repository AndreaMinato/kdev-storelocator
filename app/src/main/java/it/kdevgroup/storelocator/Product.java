package it.kdevgroup.storelocator;

/**
 * Created by andrea on 15/04/16.
 */
public class Product {

    private static final String TAG = "Store";

    public static final String KEY_ID = "id";
    public static final String KEY_ISAVAILABLE = "isAvailable";
    public static final String KEY_NAME = "name";
    public static final String KEY_PRICE = "price";

    private int id;
    private boolean isAvailable;
    private String name;
    private String price;

    public Product() {
        id = 0;
        isAvailable = true;
        name = "";
        price = "";
    }

    public Product(int id, boolean isAvailable, String name, String price) {
        this.id = id;
        this.isAvailable = isAvailable;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
