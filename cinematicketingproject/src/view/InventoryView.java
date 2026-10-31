package view;
import controllers.*;
import models.*;
import datastructures.*;
import repositories.*;
import utility.*;
import service.*;


public class InventoryView extends BaseView {


    // Menu Methods

    public int showInventoryMenu() {
         return 0;}


    // Product Input Methods

    public Product promptNewProduct(String productID) {
         return null;}

    public String promptProductID() {
         return " ";}

    public int promptQuantity() {
         return 0;}


    // Display Methods

    public void displayProducts(LinkedList list) {

    }

    public void displayLowStock(LinkedList list) {

    }

}