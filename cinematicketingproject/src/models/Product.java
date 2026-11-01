
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import java.util.Locale;
import utility.*;
import service.*;
public abstract class Product {
    private final String productID;
    private String name;
    private double price;
    private int stockQuantity;
    private String status;
    
    public Product(String productID) {
    if (!InputValidator.isNotBlank(productID)) {
        throw new IllegalArgumentException(
            "Invalid input. Product ID cannot be blank."
        );
    }

    String formattedID = productID.trim().toUpperCase();

    if (!formattedID.matches("PRD[0-9]{3}") || formattedID.equals("PRD000")) {
        throw new IllegalArgumentException(
            "Invalid input. Product ID must follow the format PRD001 to PRD999."
        );
    }

    this.productID = formattedID;
 
}
    // Abstract Method

    public abstract String getCategory();
    
    // Stock Methods

    // Deduct stock
    public boolean deductStock(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Invalid input. Quantity must be greater than zero."
            );
        }

        if ("INACTIVE".equals(status)) {
            throw new IllegalStateException(
                "Operation cannot be completed. Inactive products cannot be sold."
            );
        }

        if (quantity > stockQuantity) {
            return false;
        }

        setStockQuantity(stockQuantity - quantity);
        return true;
    }


    // Add stock
    public boolean addStock(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                "Invalid input. Quantity must be greater than zero."
            );
        }

        if (stockQuantity > Integer.MAX_VALUE - quantity) {
            return false;
        }

        setStockQuantity(stockQuantity + quantity);
        return true;
    }

   
    public void setName(String name) {

        if (!InputValidator.isNotBlank(name)) {
            throw new IllegalArgumentException(
                "Invalid input. Product name cannot be blank."
            );
        }

        this.name = name.trim();
    }

    public void setPrice(double price) {

        if (!Double.isFinite(price) || price <= 0) {
            throw new IllegalArgumentException(
                "Invalid input. Product price must be greater than zero."
            );
        }

        this.price = price;
    }
    public void setStockQuantity(int stockQuantity) {

        if (stockQuantity < 0) {
            throw new IllegalArgumentException(
                "Invalid input. Stock quantity cannot be negative."
            );
        }

        this.stockQuantity = stockQuantity;

        if (!"INACTIVE".equals(status)) {
            if (stockQuantity == 0) {
                status = "OUT_OF_STOCK";
            } else {
                status = "AVAILABLE";
            }
        }
    }
    public void setStatus(String status) {

        if (!InputValidator.isNotBlank(status)) {
            throw new IllegalArgumentException(
                "Invalid input. Product status cannot be blank."
            );
        }

        String formattedStatus = status.trim().toUpperCase(Locale.ROOT);

        if (!formattedStatus.equals("AVAILABLE")
                && !formattedStatus.equals("LOW_STOCK")
                && !formattedStatus.equals("OUT_OF_STOCK")
                && !formattedStatus.equals("INACTIVE")) {
            throw new IllegalArgumentException(
                "Invalid input. Product status must be AVAILABLE, LOW_STOCK, OUT_OF_STOCK, or INACTIVE."
            );
        }

        if (formattedStatus.equals("OUT_OF_STOCK") && stockQuantity > 0) {
            throw new IllegalArgumentException(
                "Invalid input. Product still has available stock."
            );
        }

        if ((formattedStatus.equals("AVAILABLE")
                || formattedStatus.equals("LOW_STOCK"))
                && stockQuantity == 0) {
            throw new IllegalArgumentException(
                "Invalid input. Product has no available stock."
            );
        }

        this.status = formattedStatus;
    }

    // Getters

    public String getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public String getStatus() {
        return status;
    }

}
