package it.kdevgroup.storelocator;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andrea on 15/04/16.
 */
public class Product implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(isAvailable ? 1 : 0);
        dest.writeString(name);
        dest.writeString(price);
    }

    Parcelable.Creator<Product> CREATOR = new ClassLoaderCreator<Product>() {
        @Override
        public Product createFromParcel(Parcel source, ClassLoader loader) {
            return new Product(source);
        }

        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    private Product(Parcel parcel) {
        id = parcel.readInt();
        isAvailable = (parcel.readInt() == 1);
        name = parcel.readString();
        price = parcel.readString();
    }
}
