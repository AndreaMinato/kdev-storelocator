package it.kdevgroup.storelocator;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrea on 15/04/16.
 */
public class Store implements Parcelable {

    private static final String TAG = "Store";

    public static final String KEY_GUID = "guid";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_THUMBNAIL = "thumbnail";
    public static final String KEY_IMAGE = "featured_image";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FIRSTNAME = "first";
    public static final String KEY_LASTNAME = "last";
    public static final String KEY_PERSON = "sales_person";
    public static final String KEY_PRODUCTS = "products";

    private List<Product> products;
    private String GUID;
    private String name;
    private String latitude;
    private String longitude;
    private String address;
    private String description;
    private String phone;
    private String thumbnail;
    private String image;
    private List<String> tags;
    private String email;
    private String firstName;
    private String lastName;

    public Store() {
        tags = new ArrayList<>();
        products = new ArrayList<>();
    }

    public Store(List<Product> products,
                 String GUID,
                 String name,
                 String latitude,
                 String longitude,
                 String address,
                 String description,
                 String phone,
                 String thumbnail,
                 String image,
                 List<String> tags,
                 String email,
                 String firstName,
                 String lastName) {
        this.products = products;
        this.GUID = GUID;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.description = description;
        this.phone = phone;
        this.thumbnail = thumbnail;
        this.image = image;
        this.tags = tags;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Store (Map<String, Object> map) {
        phone = map.get(KEY_PHONE).toString();
        GUID = map.get(KEY_GUID).toString();
        name = map.get(KEY_NAME).toString();
        address = map.get(KEY_ADDRESS).toString();
        //latitude = map.get(KEY_LATITUDE).toString();
        //longitude = map.get(KEY_LONGITUDE).toString();
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getGUID() {
        return GUID;
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getPhone() {
        return phone;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getImage() {
        return image;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_GUID, getGUID());
        hashMap.put(KEY_NAME, getName());
        hashMap.put(KEY_ADDRESS, getAddress());
        hashMap.put(KEY_PHONE, getPhone());

        return hashMap;
    }

    // Parte per la parcellizzazione

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(GUID);
        dest.writeString(name);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(address);
        dest.writeString(description);
        dest.writeString(phone);
        dest.writeString(thumbnail);
        dest.writeString(image);
        dest.writeStringList(tags);
        dest.writeString(email);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeList(products);
    }

    public final static Parcelable.Creator<Store> CREATOR = new ClassLoaderCreator<Store>() {
        @Override
        public Store createFromParcel(Parcel source, ClassLoader loader) {
            return new Store(source);
        }

        @Override
        public Store createFromParcel(Parcel source) {
            return new Store(source);
        }

        @Override
        public Store[] newArray(int size) {
            return new Store[size];
        }
    };

    private Store(Parcel in) {
        GUID = in.readString();
        name = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        address = in.readString();
        description = in.readString();
        phone = in.readString();
        thumbnail = in.readString();
        image = in.readString();
        tags = new ArrayList<>();
        in.readStringList(tags);
        email = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        products = new ArrayList<>();
        in.readList(products, Product.class.getClassLoader());
    }
}
