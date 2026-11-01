package repositories;
import controllers.*;
import models.*;
import datastructures.*;
import view.*;
import utility.*;
import service.*;

public class InventoryRepository {
 
    private LinkedList<Product> inventory = new LinkedList<Product>();
 
 
    // Product Management Methods
 
    // Fails on null or if the product ID already exists.
    public boolean addProduct(Product product) {
        if (product == null) {
            return false;
        }
        Node<Product> n = inventory.getHead();
        while (n != null) {
            if (n.getData().getProductID().equalsIgnoreCase(product.getProductID())) {
                return false;
            }
            n = n.getNext();
        }
        inventory.insertAtEnd(product);
        return true;}
 
    public LinkedList viewProducts() {
        return inventory;}
 
    public boolean removeProduct(String productID) {
        if (productID == null) {
            return false;
        }
        Node<Product> n = inventory.getHead();
        while (n != null) {
            if (n.getData().getProductID().equalsIgnoreCase(productID.trim())) {
                return inventory.remove(n.getData());
            }
            n = n.getNext();
        }
        return false;}
 
    // Sets the status to INACTIVE. Returns true only if the product exists and was not already INACTIVE.
    public boolean deactivateProduct(String productID) {
        if (productID == null) {
            return false;
        }
        Node<Product> n = inventory.getHead();
        while (n != null) {
            Product p = n.getData();
            if (p.getProductID().equalsIgnoreCase(productID.trim())) {
                if ("INACTIVE".equals(p.getStatus())) {
                    return false;
                }
                p.setStatus("INACTIVE");
                return true;
            }
            n = n.getNext();
        }
        return false;}
 
    // Adds qty to the product's stock (qty must be above 0). Product.addStock updates the status.
    public boolean restock(String id, int qty) {
        if (id == null || qty <= 0) {
            return false;
        }
        Node<Product> n = inventory.getHead();
        while (n != null) {
            if (n.getData().getProductID().equalsIgnoreCase(id.trim())) {
                return n.getData().addStock(qty);
            }
            n = n.getNext();
        }
        return false;}
 
 
    // Product Filtering Methods
 
    // category: "type", "name", "status" or "price" (value written as min-max, e.g. 50-150)
    public LinkedList filterProducts(String category, String value) {
        LinkedList<Product> none = new LinkedList<Product>();
        if (category == null || value == null) {
            return none;
        }
        if (category.equalsIgnoreCase("type") || category.equalsIgnoreCase("category")) {
            return filterByType(value);
        }
        if (category.equalsIgnoreCase("name")) {
            return filterByName(value);
        }
        if (category.equalsIgnoreCase("status")) {
            return filterByStatus(value);
        }
        if (category.equalsIgnoreCase("price")) {
            String[] parts = value.split("-");
            if (parts.length == 2) {
                try {
                    return filterByPrice(Double.parseDouble(parts[0].trim()),
                                         Double.parseDouble(parts[1].trim()));
                } catch (NumberFormatException e) {
                    return none;
                }
            }
        }
        return none;}
 
    // type is the product category: Food or Drink
    public LinkedList filterByType(String type) {
        LinkedList<Product> result = new LinkedList<Product>();
        if (type == null) {
            return result;
        }
        Node<Product> n = inventory.getHead();
        while (n != null) {
            if (n.getData().getCategory().equalsIgnoreCase(type.trim())) {
                result.insertAtEnd(n.getData());
            }
            n = n.getNext();
        }
        return result;}
 
    public LinkedList filterByName(String keyword) {
        LinkedList<Product> result = new LinkedList<Product>();
        if (keyword == null) {
            return result;
        }
        Node<Product> n = inventory.getHead();
        while (n != null) {
            String name = n.getData().getName();
            if (name != null && name.toLowerCase().contains(keyword.trim().toLowerCase())) {
                result.insertAtEnd(n.getData());
            }
            n = n.getNext();
        }
        return result;}
 
    public LinkedList filterByStatus(String status) {
        LinkedList<Product> result = new LinkedList<Product>();
        if (status == null) {
            return result;
        }
        Node<Product> n = inventory.getHead();
        while (n != null) {
            if (status.trim().equalsIgnoreCase(n.getData().getStatus())) {
                result.insertAtEnd(n.getData());
            }
            n = n.getNext();
        }
        return result;}
 
    // Price between min and max, both included.
    public LinkedList filterByPrice(double min, double max) {
        LinkedList<Product> result = new LinkedList<Product>();
        Node<Product> n = inventory.getHead();
        while (n != null) {
            double price = n.getData().getPrice();
            if (price >= min && price <= max) {
                result.insertAtEnd(n.getData());
            }
            n = n.getNext();
        }
        return result;}
 
    // Products (not INACTIVE) with stock at or below the threshold, including OUT_OF_STOCK ones.
    public LinkedList getLowStock(int threshold) {
        LinkedList<Product> result = new LinkedList<Product>();
        Node<Product> n = inventory.getHead();
        while (n != null) {
            Product p = n.getData();
            if (p.getStockQuantity() <= threshold && !"INACTIVE".equals(p.getStatus())) {
                result.insertAtEnd(p);
            }
            n = n.getNext();
        }
        return result;}
 
 
    // ID Generation Method
 
    // PRD001, PRD002, ... (next free number)
    public String generateProductID() {
        int max = 0;
        Node<Product> n = inventory.getHead();
        while (n != null) {
            String id = n.getData().getProductID();
            if (id.startsWith("PRD")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException e) {
                    // not one of our generated IDs, ignore it
                }
            }
            n = n.getNext();
        }
        return "PRD" + String.format("%03d", max + 1);}
 
}
