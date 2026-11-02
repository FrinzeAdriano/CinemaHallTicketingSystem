package view;
import controllers.*;
import models.*;
import datastructures.*;
import java.util.ArrayList;
import repositories.*;
import utility.*;
import service.*;


public class InventoryView extends BaseView {

    // Inventory Menu

    public int showInventoryMenu() {
        System.out.println();
        System.out.println("===== INVENTORY MANAGEMENT =====");
        System.out.println("[1] Add product");
        System.out.println("[2] View all products");
        System.out.println("[3] Edit product");
        System.out.println("[4] Restock product");
        System.out.println("[5] Deactivate product");
        System.out.println("[6] Activate product");
        System.out.println("[7] View low stock");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readInt("Choose an option (0 to go back): ", 0, 7);
    }

    // Product Ordering Menu

    public int showProductOrderMenu() {
        System.out.println();
        System.out.println("===== PRODUCT ORDERING =====");
        System.out.println("[1] Add product");
        System.out.println("[2] Finish and continue");
        System.out.println("[3] Cancel booking");
        System.out.println("[0] Go back");
        System.out.println("----------------------------------------");
        return readInt("Choose an option (0 to go back): ", 0, 3);
    }

    public int showEditMenu(Product product) {
        System.out.println();
        System.out.println("===== EDIT PRODUCT =====");
        System.out.println("Product ID : " + product.getProductID());
        System.out.println("Name       : " + product.getName());
        System.out.println("Price      : " + String.format("%.2f", product.getPrice()));
        System.out.println("----------------------------------------");
        System.out.println("[1] Edit product name");
        System.out.println("[2] Edit product price");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readInt("Choose an option (0 to go back): ", 0, 2);
    }
    
    // Product Input Methods

    public Product promptNewProduct(String productID) {

        System.out.println();
        System.out.println("===== ADD PRODUCT =====");
        System.out.println("[1] Food");
        System.out.println("[2] Drink");
        System.out.println("[0] Cancel");
        System.out.println("----------------------------------------");

        int category = readInt("Choose an option (0 to cancel): ", 0, 2);

        if (category == 0) {
            return null;
        }

        String name = readString("Product name (0 to cancel): ");

        if (name.trim().equals("0")) {
            return null;
        }

        double price;

        while (true) {

            String input = readString("Price (0 to cancel): ");

            if (input.trim().equals("0")) {
                return null;
            }

            if (InputValidator.isPositiveDouble(input)) {
                price = Double.parseDouble(input.trim());
                break;
            }

            showError("Enter a valid price greater than zero.");
        }

        System.out.println();
        int stock = readInt("Starting stock, 1-100000 (0 to cancel): ", 0, 100000);

        if (stock == 0) {
            return null;
        }

        Product product;

        if (category == 1) {

            System.out.println();
            System.out.println("===== FOOD TYPE =====");
            System.out.println("[1] Pizza");
            System.out.println("[2] Burger");
            System.out.println("[3] Pasta");
            System.out.println("[4] Fries");
            System.out.println("[5] Sandwich");
            System.out.println("[6] Hotdog");
            System.out.println("[7] Popcorn");
            System.out.println("[8] Dessert");
            System.out.println("[0] Cancel");
            System.out.println("----------------------------------------");

            int typeChoice = readInt("Choose an option (0 to cancel): ", 0, 8);

            if (typeChoice == 0) {
                return null;
            }

            String foodType;

            switch (typeChoice) {
                case 1: foodType = "PIZZA"; break;
                case 2: foodType = "BURGER"; break;
                case 3: foodType = "PASTA"; break;
                case 4: foodType = "FRIES"; break;
                case 5: foodType = "SANDWICH"; break;
                case 6: foodType = "HOTDOG"; break;
                case 7: foodType = "POPCORN"; break;
                default: foodType = "DESSERT"; break;
            }

            System.out.println();
            System.out.println("===== SERVING SIZE =====");
            System.out.println("[1] Small");
            System.out.println("[2] Regular");
            System.out.println("[3] Large");
            System.out.println("[4] Family");
            System.out.println("[0] Cancel");
            System.out.println("----------------------------------------");

            int sizeChoice = readInt("Choose an option (0 to cancel): ", 0, 4);

            if (sizeChoice == 0) {
                return null;
            }

            String servingSize = sizeChoice == 1 ? "Small"
                : sizeChoice == 2 ? "Regular"
                : sizeChoice == 3 ? "Large" : "Family";

            Food food = new Food(productID);
            food.setFoodType(foodType);
            food.setServingSize(servingSize);

            product = food;

        } else {

            System.out.println();
            System.out.println("===== DRINK SIZE =====");
            System.out.println("[1] Small");
            System.out.println("[2] Regular");
            System.out.println("[3] Large");
            System.out.println("[0] Cancel");
            System.out.println("----------------------------------------");

            int sizeChoice = readInt("Choose an option (0 to cancel): ", 0, 3);

            if (sizeChoice == 0) {
                return null;
            }

            String size = sizeChoice == 1 ? "Small"
                : sizeChoice == 2 ? "Regular" : "Large";

            boolean isCold;

            while (true) {

                String input = readString(
                    "Is it served cold? (Y/N, 0 to cancel): "
                );

                if (input.trim().equals("0")) {
                    return null;
                }

                if (input.trim().equalsIgnoreCase("Y")) {
                    isCold = true;
                    break;
                }

                if (input.trim().equalsIgnoreCase("N")) {
                    isCold = false;
                    break;
                }

                showError("Enter Y or N.");
            }

            Drink drink = new Drink(productID);
            drink.setSize(size);
            drink.setCold(isCold);

            product = drink;
        }

        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stock);

        return product;
    }

    public String promptProductID() {
        System.out.println("----------------------------------------");
        return readString("Enter product ID (0 to go back): ");
    }

    public int promptQuantity() {
        System.out.println("----------------------------------------");
        return readInt("Quantity, 1-100000 (0 to go back): ", 0, 100000);
    }

    // Display Methods

    public void displayProducts(LinkedList<Product> products) {

        if (products == null || products.size() == 0) {
            showMessage("No products found.");
            return;
        }

        String[] headers = {
            "ID", "Type", "Name", "Price", "Stock", "Status", "Details"
        };

        ArrayList<String[]> rows = new ArrayList<String[]>();

        for (int i = 0; i < products.size(); i++) {

            Product product = products.get(i);

            String type = product.getCategory();
            String details = "";

            if (product instanceof Food) {

                Food food = (Food) product;

                details = food.getFoodType() + ", "
                    + food.getServingSize().toUpperCase();

            } else if (product instanceof Drink) {

                Drink drink = (Drink) product;

                details = drink.getSize().toUpperCase() + ", "
                    + (drink.isCold() ? "Cold" : "Hot");
            }

            rows.add(new String[] {
                product.getProductID(),
                type,
                product.getName(),
                String.format("%.2f", product.getPrice()),
                String.valueOf(product.getStockQuantity()),
                product.getStatus(),
                details
            });
        }

        BaseView.printTable(headers, rows);
    }

    public void displayLowStock(LinkedList list) {

        if (list == null || list.getHead() == null) {
            showMessage("No products are low on stock.");
            return;
        }

        System.out.println();
        System.out.println("===== LOW STOCK ALERT =====");

        String row = "%-8s %-24s %7s%n";
        System.out.printf(row, "ID", "Name", "Stock");
        System.out.println("-----------------------------------------");

        Node node = list.getHead();

        while (node != null) {

            Product p = (Product) node.getData();

            System.out.printf(row, p.getProductID(),
                p.getName(), p.getStockQuantity());

            node = node.getNext();
        }
    }
    
    public int promptLowStockThreshold() {
        System.out.println("----------------------------------------");
        return readInt("Low-stock limit, 1-100000 (0 to go back): ", 0, 100000);
    }

    // 0 = go back, 1 = yes, 2 = no
    public int confirmAction(String prompt) {

        while (true) {

            String input = readString(prompt + " (Y/N, 0 to go back): ").trim();

            if (input.equals("0")) {
                return 0;
            }

            if (input.equalsIgnoreCase("Y")) {
                return 1;
            }

            if (input.equalsIgnoreCase("N")) {
                return 2;
            }

            showError("Enter Y, N, or 0.");
        }
    }
}