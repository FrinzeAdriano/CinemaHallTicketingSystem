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
        System.out.println("\n===== INVENTORY MANAGEMENT =====");
        System.out.println("1. Add product");
        System.out.println("2. View all products");
        System.out.println("3. Edit product");
        System.out.println("4. Restock product");
        System.out.println("5. Deactivate product");
        System.out.println("6. View low stock");
        System.out.println("0. Back");
        return readInt("Choose an option: ", 0, 6);
    }

    // Product Input Methods

    public Product promptNewProduct(String productID) {
        System.out.println("Product category:");
        System.out.println("1. Food");
        System.out.println("2. Drink");
        int category = readInt("Choose a category: ", 1, 2);
 
        String name = readString("Product name: ");
        double price = readDouble("Price: ");
        int stock = readInt("Starting stock (0-100000): ", 0, 100000);
 
        Product product;
 
        if (category == 1) {
            System.out.println("Food type:");
            System.out.println("1. Snack");
            System.out.println("2. Meal");
            System.out.println("3. Dessert");
            int typeChoice = readInt("Choose a food type: ", 1, 3);
            String foodType = (typeChoice == 1) ? "Snack" : (typeChoice == 2) ? "Meal" : "Dessert";
 
            System.out.println("Serving size:");
            System.out.println("1. Small");
            System.out.println("2. Regular");
            System.out.println("3. Large");
            int sizeChoice = readInt("Choose a serving size: ", 1, 3);
            String servingSize = (sizeChoice == 1) ? "Small" : (sizeChoice == 2) ? "Regular" : "Large";
 
            Food food = new Food(productID);   //namumula kasi di pa updated constructor
            food.setFoodType(foodType);
            food.setServingSize(servingSize);
            product = food;
        } else {
            System.out.println("Drink size:");
            System.out.println("1. Small");
            System.out.println("2. Regular");
            System.out.println("3. Large");
            int sizeChoice = readInt("Choose a size: ", 1, 3);
            String size = (sizeChoice == 1) ? "Small" : (sizeChoice == 2) ? "Regular" : "Large";
            boolean isCold = confirm("Is it served cold?");
 
            Drink drink = new Drink(productID); //namumula kasi di pa updated constructor
            drink.setSize(size);
            drink.setCold(isCold);
            product = drink;
        }
 
        product.setName(name);
        product.setPrice(price);           // namumula kasi di pa updated yung sa setter methods ni product
        product.setStockQuantity(stock);   // also sets the status: AVAILABLE, or OUT_OF_STOCK at 0
        return product;}
 
    public String promptProductID() {
        return readString("Enter product ID: ");
    }
 
    public int promptQuantity() {
        return readInt("Quantity (1-100000): ", 1, 100000);
    }


    // Display Methods

    public void displayProducts(LinkedList list) {
        if (list == null || list.getHead() == null) {
            System.out.println("No products found.");
            return;
        }
        String row = "%-8s %-7s %-24s %10s %7s %-13s %-22s%n";
        System.out.println();
        System.out.printf(row, "ID", "Type", "Name", "Price", "Stock", "Status", "Details");
        System.out.println("--------------------------------------------------------------------------------------------------");
        Node n = list.getHead();
        while (n != null) {
            Product p = (Product) n.getData();
            String details = "";
            if (p instanceof Food) {
                Food food = (Food) p;
                details = food.getFoodType() + ", " + food.getServingSize();
            } else if (p instanceof Drink) {
                Drink drink = (Drink) p;
                details = drink.getSize() + ", " + (drink.isCold() ? "Cold" : "Hot");
            }
            System.out.printf(row, p.getProductID(), p.getCategory(), p.getName(),
                    String.format("%.2f", p.getPrice()), p.getStockQuantity(), p.getStatus(), details);
            n = n.getNext();
        }}
 
    public void displayLowStock(LinkedList list) {
        if (list == null || list.getHead() == null) {
            System.out.println("No products are low on stock.");
            return;
        }
        System.out.println("\n===== LOW STOCK ALERT =====");
        String row = "%-8s %-24s %7s%n";
        System.out.printf(row, "ID", "Name", "Stock");
        System.out.println("-----------------------------------------");
        Node n = list.getHead();
        while (n != null) {
            Product p = (Product) n.getData();
            System.out.printf(row, p.getProductID(), p.getName(), p.getStockQuantity());
            n = n.getNext();
        }
    }
}
