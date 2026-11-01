
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import java.util.Locale;
import utility.*;
import service.*;
public class Food extends Product {
   
    private String foodType;
    private String servingSize;
    
   public Food(String productID) {
    super(productID);
    }
   // Override Method

    
    @Override
    public String getCategory() {
        return "Food";
    }

    // Setters

      public void setFoodType(String foodType) {
            //pinalimit ko muna possible lagay
        if (!InputValidator.isNotBlank(foodType)) {
            throw new IllegalArgumentException(
                "Invalid input. Food type cannot be blank."
            );
        }

        String formattedType = foodType.trim().toUpperCase(Locale.ROOT);

        if (!formattedType.equals("PIZZA")
                && !formattedType.equals("BURGER")
                && !formattedType.equals("PASTA")
                && !formattedType.equals("FRIES")
                && !formattedType.equals("SANDWICH")
                && !formattedType.equals("HOTDOG")
                && !formattedType.equals("POPCORN")
                && !formattedType.equals("DESSERT")) {
            throw new IllegalArgumentException(
                "Invalid input. Food type must be PIZZA, BURGER, PASTA, FRIES, SANDWICH, HOTDOG, POPCORN, or DESSERT."
            );
        }

        this.foodType = formattedType;
    }


    public void setServingSize(String servingSize) {

        if (!InputValidator.isNotBlank(servingSize)) {
            throw new IllegalArgumentException(
                "Invalid input. Serving size cannot be blank."
            );
        }

        String formattedSize = servingSize.trim().toUpperCase(Locale.ROOT);

        if (!formattedSize.equals("SMALL")
                && !formattedSize.equals("REGULAR")
                && !formattedSize.equals("LARGE")
                && !formattedSize.equals("FAMILY")) {
            throw new IllegalArgumentException(
                "Invalid input. Serving size must be SMALL, REGULAR, LARGE, or FAMILY."
            );
        }

        this.servingSize = formattedSize;
    }
    
   // Getters

      public String getFoodType() {
        return foodType;
    }

    public String getServingSize() {
        return servingSize;
    }




}
