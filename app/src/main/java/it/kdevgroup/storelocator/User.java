package it.kdevgroup.storelocator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.client.UserTokenHandler;

/**
 * Created by damiano on 15/04/16.
 */
public class User {
    public static final String USERNAME_KEY = "username";
    //    public static final String PASSWORD_KEY = "password";
    public static final String SESSION_KEY = "session";
    public static final String SESSION_TTL_KEY = "session_ttl";
    public static final String NAME_KEY = "name";
    public static final String SURNAME_KEY = "surname";
    public static final String EMAIL_KEY = "email";

    private String username;
    //    private String password;
    private String session;
    private long sessionTtl;
    private String name;
    private String surname;
    private String email;

    public User() {
    }

    public User(String username, String session, long sessionTtl, String name, String surname, String email) {
        this.username = username;
        this.session = session;
        this.sessionTtl = sessionTtl;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public User(Map<String, Object> map) {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public long getSessionTtl() {
        return sessionTtl;
    }

    public void setSessionTtl(long sessionTtl) {
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

    /**
     * Determina se la sessione dell'utente è scaduta
     * @return <b>true</b> se è scaduta<br><b>false</b> se è ancora attiva
     */
    public boolean isSessionExpired() {
        return new Date(sessionTtl).after(new Date());
    }

    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(getEmail(), this);

        return hashMap;
    }
}
