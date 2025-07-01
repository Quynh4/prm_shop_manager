package com.example.shop_sqlite_manage.Entity;
public class Product {
    public int id;
    public String name;
    public double price;
    public String description;
    public int quantity;
    public String image;

    public Product(int id, String name, double price, String description, int quantity, String image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.quantity=quantity;
    }


    public Product() {
    }

}
