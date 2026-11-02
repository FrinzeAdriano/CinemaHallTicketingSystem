package controllers;

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
    }

    public void addProduct() {
    }

    public void editProduct() {
    }

    public void restockProduct() {
    }

    public void deactivateProduct() {
    }

    public void viewLowStock() {
    }
}
