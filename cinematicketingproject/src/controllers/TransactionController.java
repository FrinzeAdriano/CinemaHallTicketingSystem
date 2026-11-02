package controllers;

import models.Cashier;
import models.Customer;
import repositories.SalesRepository;
import repositories.TransactionHistory;
import service.CheckoutService;
import view.ReceiptView;
import view.TransactionHistoryView;

public class TransactionController {
    private TransactionHistory history;
    private SalesRepository salesRepo;
    private CheckoutService checkoutService;
    private TransactionHistoryView historyView;
    private ReceiptView receiptView;

    public TransactionController(TransactionHistory history, SalesRepository salesRepo,
            CheckoutService checkoutService, TransactionHistoryView historyView, ReceiptView receiptView) {
        this.history = history;
        this.salesRepo = salesRepo;
        this.checkoutService = checkoutService;
        this.historyView = historyView;
        this.receiptView = receiptView;
    }

    // Admin
    public void manageTransactionHistory() {
    }

    public void reprintReceipt() {
    }

    public void voidTransaction() {
    }

    // Cashier
    public void viewMyTransactions(Cashier cashier) {
    }

    // Customer
    public void viewMyHistory(Customer customer) {
    }
}
