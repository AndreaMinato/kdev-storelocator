package it.kdevgroup.storelocator;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrea on 15/04/16.
 */
public class Store implements Parcelable, Comparable<Store> {

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
    private int lastKnownDistance;

    public Store() {
        tags = new ArrayList<>();
        products = new ArrayList<>();
        lastKnownDistance = 0;
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
        lastKnownDistance = 0;
    }

    public Store(Map<String, Object> map) {
        GUID = (String) map.get(KEY_GUID);
        name = ((String) map.get(KEY_NAME));
        address = ((String) map.get(KEY_ADDRESS));
        description = ((String) map.get(KEY_DESCRIPTION));
        phone = ((String) map.get(KEY_PHONE));
        thumbnail = ((String) map.get(KEY_THUMBNAIL));
        image = ((String) map.get(KEY_IMAGE));
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags = ((ArrayList<String>) map.get(KEY_TAGS));
        latitude = ((String) map.get(KEY_LATITUDE));
        longitude = ((String) map.get(KEY_LONGITUDE));
        email = ((String) map.get(KEY_EMAIL));
        firstName = ((String) map.get(KEY_FIRSTNAME));
        lastName = ((String) map.get(KEY_LASTNAME));
        products = new ArrayList<>();
        ArrayList<LinkedHashMap<String, String>> list = ((ArrayList) map.get(KEY_PRODUCTS));
        for (LinkedHashMap<String, String> pr : list ) {
            try {
                JSONObject jsonObject = new JSONObject(pr);
                Product product = new Product();
                product.setId(jsonObject.getInt("id"));
                product.setName(jsonObject.getString("name"));
                product.setPrice(jsonObject.getString("price"));
                product.setIsAvailable(jsonObject.getBoolean("available"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        lastKnownDistance = 0;
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getGUID() {
        return GUID;
    }

    public String getName() {
        //riformatta il nome dell'azienda(ZILIDIUM ==> Zilidium)
        name = name.toLowerCase();
        StringBuilder rackingSystemSb = new StringBuilder();
        rackingSystemSb.append(name);
        rackingSystemSb.setCharAt(0, Character.toUpperCase(rackingSystemSb.charAt(0)));
        name = rackingSystemSb.toString();
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

    public int getLastKnownDistance() {
        return lastKnownDistance;
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

    public void setLastKnownDistance(int lastKnownDistance) {
        this.lastKnownDistance = lastKnownDistance;
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_GUID, GUID);
        hashMap.put(KEY_NAME, name);
        hashMap.put(KEY_ADDRESS, address);
        hashMap.put(KEY_DESCRIPTION, description);
        hashMap.put(KEY_PHONE, phone);
        hashMap.put(KEY_THUMBNAIL, thumbnail);
        hashMap.put(KEY_IMAGE, image);

        hashMap.put(KEY_TAGS, tags);
        hashMap.put(KEY_LATITUDE, latitude);
        hashMap.put(KEY_LONGITUDE, longitude);
        hashMap.put(KEY_EMAIL, email);
        hashMap.put(KEY_FIRSTNAME, firstName);
        hashMap.put(KEY_LASTNAME, lastName);
        hashMap.put(KEY_PRODUCTS, products);

        return hashMap;
    }

    // Parte per la parcellizzazione

    @Override
    public int describeContents() {
        return getGUID().hashCode();
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
        dest.writeTypedList(products);
        dest.writeInt(lastKnownDistance);
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
        in.readTypedList(products, Product.CREATOR);
        lastKnownDistance = in.readInt();
    }

    @Override
    public int compareTo(@NonNull Store store) {
        if(this.lastKnownDistance < store.getLastKnownDistance())
            return -1;
        if(this.lastKnownDistance > store.getLastKnownDistance())
            return 1;
        return 0;
    }
}
