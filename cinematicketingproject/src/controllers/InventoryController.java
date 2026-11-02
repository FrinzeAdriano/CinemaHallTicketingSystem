package controllers;

import datastructures.LinkedList;
import models.Product;
import repositories.InventoryRepository;
import view.InventoryView;

public class InventoryController {
    private InventoryRepository inventoryRepo;
    private InventoryView inventoryView;

    public InventoryController(InventoryRepository inventoryRepo, InventoryView inventoryView) {
        this.inventoryRepo = inventoryRepo;
        this.inventoryView = inventoryView;
    }

       public void manageInventory() {

        if (inventoryRepo == null || inventoryView == null) {
            return;
        }

        boolean running = true;

        while (running) {

            int choice = inventoryView.showInventoryMenu();

            switch (choice) {

                case 1:
                    addProduct();
                    break;

                case 2:
                    inventoryView.displayProducts(inventoryRepo.viewProducts());
                    break;

                case 3:
                    editProduct();
                    break;

                case 4:
                    restockProduct();
                    break;

                case 5:
                    deactivateProduct();
                    break;

                case 6:
                    viewLowStock();
                    break;

                case 0:
                    running = false;
                    break;

                default:
                    inventoryView.showError("Invalid option.");
                    break;
            }
        }
    }


      public void addProduct() {

        try {

            String productID = inventoryRepo.generateProductID();

            Product product = inventoryView.promptNewProduct(productID);

            if (product == null) {
                inventoryView.showMessage("Product creation cancelled.");
                return;
            }

            if (inventoryRepo.addProduct(product)) {

                inventoryView.showMessage(
                    "Product successfully added. Product ID: " + productID
                );

            } else {

                inventoryView.showError("Unable to add product.");
            }

        } catch (IllegalArgumentException | IllegalStateException e) {

            inventoryView.showError(e.getMessage());
        }
    }

     public void editProduct() {

        Product product = selectProduct();

        if (product == null) {
            return;
        }

        boolean editing = true;

        while (editing) {

            inventoryView.showMessage("\n===== EDIT PRODUCT =====");
            inventoryView.showMessage("Product ID: " + product.getProductID());
            inventoryView.showMessage("Product Name: " + product.getName());
            inventoryView.showMessage("Price: " + product.getPrice());
            inventoryView.showMessage("----------------------------------------");
            inventoryView.showMessage("1. Edit product name");
            inventoryView.showMessage("2. Edit product price");
            inventoryView.showMessage("0. Back");

            int choice = inventoryView.readInt("Choice: ", 0, 2);

            try {

                switch (choice) {

                    case 1:

                        String name = inventoryView.readString(
                            "New product name (0 to go back): "
                        );

                        if (name.equals("0")) {
                            break;
                        }

                        product.setName(name);

                        inventoryView.showMessage("Product name successfully updated.");
                        break;

                    case 2:

                        String input = inventoryView.readString(
                            "New product price (0 to go back): "
                        );

                        if (input.equals("0")) {
                            break;
                        }

                        double price = Double.parseDouble(input.trim());

                        product.setPrice(price);

                        inventoryView.showMessage("Product price successfully updated.");
                        break;

                    case 0:
                        editing = false;
                        break;
                }

            } catch (IllegalArgumentException | IllegalStateException e) {

                inventoryView.showError(e.getMessage());
            }
        }
    }

     public void restockProduct() {

        Product product = selectProduct();

        if (product == null) {
            return;
        }

        if (product.getStatus().equalsIgnoreCase("INACTIVE")) {
            inventoryView.showError("Inactive products cannot be restocked.");
            return;
        }

        int quantity = inventoryView.promptQuantity();

        if (quantity == 0) {
            inventoryView.showMessage("Restocking cancelled.");
            return;
        }

        if (inventoryRepo.restock(product.getProductID(), quantity)) {

            inventoryView.showMessage("Product successfully restocked.");
            inventoryView.showMessage(
                "Current stock: " + product.getStockQuantity()
            );

        } else {

            inventoryView.showError("Unable to restock product.");
        }
    }

       public void deactivateProduct() {

        Product product = selectProduct();

        if (product == null) {
            return;
        }

        if (product.getStatus().equalsIgnoreCase("INACTIVE")) {
            inventoryView.showMessage("This product is already inactive.");
            return;
        }

        int confirmation = inventoryView.confirmAction(
            "Are you sure you want to deactivate " + product.getName() + "?"
        );

        if (confirmation == 0) {
            inventoryView.showMessage("Deactivation cancelled.");
            return;
        }

        if (confirmation == 2) {
            inventoryView.showMessage("Product was not deactivated.");
            return;
        }

        if (inventoryRepo.deactivateProduct(product.getProductID())) {

            inventoryView.showMessage("Product successfully deactivated.");

        } else {

            inventoryView.showError("Unable to deactivate product.");
        }
    }


   public void viewLowStock() {

        int threshold = inventoryView.promptLowStockThreshold();

        if (threshold == 0) {
            return;
        }

        LinkedList<Product> lowStockProducts =
            inventoryRepo.getLowStock(threshold);

        inventoryView.displayLowStock(lowStockProducts);
    }

    // Selects a product using its ID. Enter 0 to go back.
    private Product selectProduct() {

        LinkedList<Product> products = inventoryRepo.viewProducts();

        if (products == null || products.size() == 0) {
            inventoryView.showMessage("No products available.");
            return null;
        }

        inventoryView.displayProducts(products);

        while (true) {

            String productID = inventoryView.promptProductID();

            if (productID.trim().equals("0")) {
                return null;
            }

            for (int i = 0; i < products.size(); i++) {

                Product product = products.get(i);

                if (product.getProductID().equalsIgnoreCase(productID.trim())) {
                    return product;
                }
            }

            inventoryView.showError("Product ID not found. Please try again.");
        }
    }
}