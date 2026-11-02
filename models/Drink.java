
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import java.util.Locale;
import utility.*;
import service.*;
public class Drink extends Product {
    private String size;
    private boolean isCold;
    
    public Drink(String productID) {
        super(productID);
        
    }
    
    // Override Method

    @Override
    public String getCategory() {
         return "Drink";}
    
    // Setters

     public void setSize(String size) {

        if (!InputValidator.isNotBlank(size)) {
            throw new IllegalArgumentException(
                "Invalid input. Drink size cannot be blank."
            );
        }

        String formattedSize = size.trim().toUpperCase(Locale.ROOT);

        if (!formattedSize.equals("SMALL")
                && !formattedSize.equals("REGULAR")
                && !formattedSize.equals("LARGE")) {
            throw new IllegalArgumentException(
                "Invalid input. Drink size must be SMALL, REGULAR, or LARGE."
            );
        }

        this.size = formattedSize;
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