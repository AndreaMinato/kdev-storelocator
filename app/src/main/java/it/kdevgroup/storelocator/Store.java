package it.kdevgroup.storelocator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrea on 15/04/16.
 */
public class Store {
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

    public Store(String GUID,
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

    public Store() {
        tags = new ArrayList<>();
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
}
