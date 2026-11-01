
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
    
       // Constructor
    public OrderProduct(Product product, int quantity, double price) {

        // Product validation
        if (product == null) {
            throw new IllegalArgumentException(
                "Invalid input. No product was provided."
            );
        }

        // Quantity validation
        if (!InputValidator.isPositiveInt(String.valueOf(quantity))) {
            throw new IllegalArgumentException(
                "Invalid input. Quantity must be greater than zero."
            );
        }

        // Product price validation
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException(
                "Invalid input. Product price must be a finite amount equal to or greater than zero."
            );
        }

        // Check whether the order price matches the actual product price
        if (Double.compare(price, product.getPrice()) != 0) {
            throw new IllegalArgumentException(
                "Invalid input. Order price must match the current product price."
            );
        }

        // Subtotal validation
        if (!Double.isFinite(price * quantity)) {
            throw new IllegalArgumentException(
                "Invalid input. Order subtotal exceeds the supported numeric range."
            );
        }

        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }


    // Calculates the subtotal
    public double getSubtotal() {

        double subtotal = price * quantity;

        return subtotal;
    }

    // Setter
    public void setQuantity(int quantity) {

        if (!InputValidator.isPositiveInt(String.valueOf(quantity))) {
            throw new IllegalArgumentException(
                "Invalid input. Quantity must be greater than zero."
            );
        }

        // Check subtotal after changing quantity
        if (!Double.isFinite(price * quantity)) {
            throw new IllegalArgumentException(
                "Invalid input. Order subtotal exceeds the supported numeric range."
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
