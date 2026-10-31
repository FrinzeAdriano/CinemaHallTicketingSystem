
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
    this.product = product;
    this.quantity = quantity;
    this.price = price;
}
    public double getSubtotal() {
    return 0;}
    
    // Setter
    public void setQuantity(int quantity) {
    this.quantity = quantity;}
    
    // Getters
    public Product getProduct() {
    return product;}

    public int getQuantity() {
    return quantity;}

    public double getPrice() {
    return price;}






}
