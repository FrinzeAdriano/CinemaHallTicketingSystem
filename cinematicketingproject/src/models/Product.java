
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;
public abstract class Product {
    private final String productID;
    private String name;
    private double price;
    private int stockQuantity;
    private String status;
    
    public Product(String productID, String name, double price, int stockQuantity, String status) {
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
    }
    // Abstract Method

    public abstract String getCategory();
    
    // Stock Methods

    public boolean deductStock(int quantity) {
         return false;}

    public boolean addStock(int quantity) {
         return false;}
    // Setters

    public void setName(String name) {
    this.name = name;
    }

    public void setPrice(double price) {
    this.price = price;
    }

    public void setStatus(String status) {
    this.status = status;
    }
    // Getters

    public String getProductID() {
         return productID;}

    public String getName() {
         return name;}

    public double getPrice() {
         return price;}

    public int getStockQuantity() {
         return stockQuantity;}

    public String getStatus() {
         return status;}

}
