package it.kdevgroup.storelocator;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by damiano on 15/04/16.
 */
public class User implements Parcelable {

    private static User instance;

    public static final String ID_KEY = "_id";
    //    public static final String PASSWORD_KEY = "password";
    public static final String SESSION_KEY = "session";
    public static final String SESSION_TTL_KEY = "session_ttl";
    public static final String NAME_KEY = "name";
    public static final String SURNAME_KEY = "surname";
    public static final String EMAIL_KEY = "email";
    public static final String COMPANY_KEY = "company";
    public static final String FAVOURITE_COMPANY_KEY = "favorite_company";

    private String id;
    //    private String password;
    private String session;
    private Integer sessionTtl;
    private String name;
    private String surname;
    private String email;
    private String company;
    private String favouriteCompany;

    // PARTE SINGLETON

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    /**
     * Determina se l'istanza è nulla
     * @return
     */
    public static boolean isNull() {
        return instance == null;
    }

    /**
     * Determina se l'instanza del singleton è impostata in tutti i suoi campi
     *
     * @return
     */
    public boolean isSet() {
        return instance != null &&
                id != null &&
                session != null &&
                sessionTtl != null &&
                name != null &&
                surname != null &&
                email != null &&
                company != null &&
                favouriteCompany != null;
    }

    /**
     * Imposta l'instanza del singleton
     *
     * @param u
     */
    public void setInstance(User u) {
        instance = new User(u.getId(),
                u.getSession(),
                u.getSessionTtl(),
                u.getName(),
                u.getSurname(),
                u.getEmail(),
                u.getCompany(),
                u.getFavouriteCompany());
    }

    public User() {
    }

    public User(String id, String session, Integer sessionTtl, String name, String surname, String email,
                String company, String favouriteCompany) {
        this.id = id;
        this.session = session;
        this.sessionTtl = sessionTtl;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.company = company;
        this.favouriteCompany = favouriteCompany;
    }

    public User(Map<String, Object> map) {
        id = map.get("id").toString(); //non posso passargli ID_KEY
        session = map.get(SESSION_KEY).toString(); //qui per fortuna sì
        sessionTtl = (Integer) map.get("sessionTtl");
        name = map.get(NAME_KEY).toString();
        surname = map.get(SURNAME_KEY).toString();
        email = map.get(EMAIL_KEY).toString();
        company = map.get("company").toString();
        favouriteCompany = map.get("favouriteCompany").toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Integer getSessionTtl() {
        return sessionTtl;
    }

    public void setSessionTtl(Integer sessionTtl) {
        this.sessionTtl = sessionTtl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany(){
        return company;
    }

    public void setCompany(String company){
        this.company = company;
    }

    public String getFavouriteCompany() {
        return favouriteCompany;
    }

    public void setFavouriteCompany(String favouriteCompany) {
        this.favouriteCompany = favouriteCompany;
    }


    /**
     * Determina se la sessione dell'utente è scaduta
     *
     * @return <b>true</b> se è scaduta<br><b>false</b> se è ancora attiva
     */
    public boolean isSessionExpired() {
//        Log.d("isSessionExpired", new SimpleDateFormat("dd MM yyyy").format(new Date()));
//        Log.d("isSessionExpired", new SimpleDateFormat("dd MM yyyy").format(new Date((long) sessionTtl * 1000)));
        return new Date().after(new Date((long) sessionTtl * 1000));
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("user", this);

        return hashMap;
    }

    // parcellizzazione

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(session);
        dest.writeInt(sessionTtl);
        dest.writeString(name);
        dest.writeString(surname);
        dest.writeString(email);
        dest.writeString(company);
        dest.writeString(favouriteCompany);
    }

    public static final Parcelable.Creator<User> CREATOR = new ClassLoaderCreator<User>() {
        @Override
        public User createFromParcel(Parcel source, ClassLoader loader) {
            return new User(source);
        }

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        id = in.readString();
        session = in.readString();
        sessionTtl = in.readInt();
        name = in.readString();
        surname = in.readString();
        email = in.readString();
        company = in.readString();
        favouriteCompany = in.readString();
    }
}
