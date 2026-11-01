
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;
public class Food extends Product {
   
    private String foodType;
    private String servingSize;
    
   public Food(String productID, String name, double price, int stockQuantity, String status, String foodType, String servingSize) {
    super(productID, name, price, stockQuantity, status);
    this.foodType = foodType;
    this.servingSize = servingSize;
    }
   // Override Method

    @Override
    public String getCategory() {
         return "Food";}

    // Setters

    public void setFoodType(String foodType) {
    this.foodType = foodType;
    }

    public void setServingSize(String servingSize) {
    this.servingSize = servingSize;
    }
    
   // Getters

    public String getFoodType() {
         return foodType;}

    public String getServingSize() {
         return servingSize;}




}