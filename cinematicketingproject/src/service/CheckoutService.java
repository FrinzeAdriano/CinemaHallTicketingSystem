
package service;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import models.*;

public class CheckoutService {

    private TransactionHistory history;
    private SalesRepository salesRepo;
    private InventoryRepository inventoryRepo;
    private VoucherRepository voucherRepo;
    private int receiptCounter;

public CheckoutService(TransactionHistory history, SalesRepository salesRepo, InventoryRepository inventoryRepo,
                       VoucherRepository voucherRepo, int receiptCounter) {
    this.history = history;
    this.salesRepo = salesRepo;
    this.inventoryRepo = inventoryRepo;
    this.voucherRepo = voucherRepo;
    this.receiptCounter = receiptCounter;
    }
    // Checkout Methods

    public Receipt checkout(Transaction transaction) {
         return null;}

    public boolean cancelTransaction(Transaction transaction) {
         return false;}

}
