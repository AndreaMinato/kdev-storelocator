package it.kdevgroup.storelocator;

/**
 * Created by andrea on 15/04/16.
 */
public class Product {
    private int id;
    private boolean isAvaible;
    private String name;
    private String price;

    public Product() {
        id = 0;
        isAvaible = true;
        name = "";
        price = "";
    }

    public Product(int id, boolean isAvaible, String name, String price) {
        this.id = id;
        this.isAvaible = isAvaible;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public boolean isAvaible() {
        return isAvaible;
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

    public void setIsAvaible(boolean isAvaible) {
        this.isAvaible = isAvaible;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
