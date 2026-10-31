
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;
public class Drink extends Product {
    private String size;
    private boolean isCold;
    
    public Drink(String productID, String name, double price, int stockQuantity, String status, String size, boolean isCold) {
        super(productID, name, price, stockQuantity, status);
        this.size = size;
        this.isCold = isCold;
    }
    
    // Override Method

    @Override
    public String getCategory() {
         return "Drink";}
    
    // Setters

    public void setSize(String size) {
    this.size = size;
    }

    public void setCold(boolean isCold) {
    this.isCold = isCold;
    }
    // Getters

    public String getSize() {
         return size;}

    public boolean isCold() {
         return isCold;}






}
