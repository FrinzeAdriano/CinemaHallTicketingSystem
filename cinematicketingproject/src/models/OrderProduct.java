
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;

public class OrderProduct {
    
    private Product product;
    private int  quantity;
    private double  price;
    
     public OrderProduct(Product product, int quantity, double price) {

        if (!InputValidator.isPositiveInt(String.valueOf(quantity))) {
            throw new IllegalArgumentException(
                "Quantity must be greater than zero."
            );
        }

        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }


    // Calculates the subtotal
    public double getSubtotal() {
        return price * quantity;
    }


    // Setter
    public void setQuantity(int quantity) {

        if (!InputValidator.isPositiveInt(String.valueOf(quantity))) {
            throw new IllegalArgumentException(
                "Quantity must be greater than zero."
            );
        }

        this.quantity = quantity;
    }


    // Getters
    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

}
