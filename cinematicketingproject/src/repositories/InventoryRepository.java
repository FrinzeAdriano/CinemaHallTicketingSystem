package repositories;
import controllers.*;
import models.*;
import datastructures.*;
import view.*;
import utility.*;
import service.*;

public class InventoryRepository {

    private LinkedList inventory = new LinkedList();


    // Product Management Methods

    public boolean addProduct(Product product) {
         return false;}

    public LinkedList viewProducts() {
         return null;}

    public boolean removeProduct(String productID) {
         return false;}

    public boolean deactivateProduct(String productID) {
         return false;}

    public boolean restock(String id, int qty) {
         return false;}


    // Product Filtering Methods

    public LinkedList filterProducts(String category, String value) {
         return null;}

    public LinkedList filterByType(String type) {
         return null;}

    public LinkedList filterByName(String keyword) {
         return null;}

    public LinkedList filterByStatus(String status) {
         return null;}

    public LinkedList filterByPrice(double min, double max) {
         return null;}

    public LinkedList getLowStock(int threshold) {
         return null;}


    // ID Generation Method

    public String generateProductID() {
         return " ";}

}