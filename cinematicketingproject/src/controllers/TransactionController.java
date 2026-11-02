package controllers;

import datastructures.LinkedList;
import java.time.LocalDate;
import models.*;
import repositories.SalesRepository;
import repositories.TransactionHistory;
import service.CheckoutService;
import utility.InputValidator;
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

        if (history == null || historyView == null) {
            return;
        }

        while (true) {

            int choice = historyView.showHistoryMenu();

            switch (choice) {

                case 0:
                    return;

                // View all transactions
                case 1:

                    historyView.displayTransactions(
                        history.viewTransactionHistory()
                    );

                    break;

                // Filter transactions
                case 2: {

                    int filterChoice;

                    // Repeats the filter option until valid
                    while (true) {

                        filterChoice = historyView.promptFilterOption();

                        if (filterChoice == 0) {
                            break;
                        }

                        if (filterChoice >= 1 && filterChoice <= 5) {
                            break;
                        }

                        historyView.showError(
                            "Invalid filter option. Please try again."
                        );
                    }

                    if (filterChoice == 0) {
                        break;
                    }

                    // Filter by date range
                    if (filterChoice == 5) {

                        LocalDate startDate;

                        // Repeats the start date field if invalid
                        while (true) {

                            startDate = historyView.readDate(
                                "Enter start date (yyyy-MM-dd): "
                            );

                            if (startDate != null) {
                                break;
                            }

                            historyView.showError(
                                "Invalid start date. Please try again."
                            );
                        }

                        LocalDate endDate;

                        // Repeats only the end date field if invalid
                        while (true) {

                            endDate = historyView.readDate(
                                "Enter end date (yyyy-MM-dd): "
                            );

                            if (endDate == null) {

                                historyView.showError(
                                    "Invalid end date. Please try again."
                                );

                                continue;
                            }

                            if (endDate.isBefore(startDate)) {

                                historyView.showError(
                                    "End date cannot be earlier than start date."
                                );

                                continue;
                            }

                            break;
                        }

                        historyView.displayTransactions(
                            history.filterByDateRange(startDate, endDate)
                        );

                    } else {

                        String field = "";

                        if (filterChoice == 1) {
                            field = "status";
                        } else if (filterChoice == 2) {
                            field = "bookingType";
                        } else if (filterChoice == 3) {
                            field = "customerID";
                        } else if (filterChoice == 4) {
                            field = "cashierID";
                        }

                        // Repeats the filter value field if blank or invalid
                        while (true) {

                            String value = historyView.readString(
                                "Enter filter value: "
                            );

                            if (!InputValidator.isNotBlank(value)) {

                                historyView.showError(
                                    "Filter value cannot be blank. Please try again."
                                );

                                continue;
                            }

                            try {

                                historyView.displayTransactions(
                                    history.filterTransactionHistory(
                                        field, value.trim()
                                    )
                                );

                                break;

                            } catch (IllegalArgumentException e) {

                                historyView.showError(e.getMessage());
                            }
                        }
                    }

                    break;
                }

                // Search transaction
                case 3: {

                    while (true) {

                        String transactionID = historyView.readString(
                            "Enter transaction ID: "
                        );

                        if (!InputValidator.isNotBlank(transactionID)) {

                            historyView.showError(
                                "Transaction ID cannot be blank. Please try again."
                            );

                            continue;
                        }

                        Transaction transaction = history.findByID(
                            transactionID.trim()
                        );

                        if (transaction == null) {

                            historyView.showError(
                                "Transaction not found. Please try again."
                            );

                            continue;
                        }

                        historyView.displayTransactionDetails(transaction);
                        break;
                    }

                    break;
                }

                default:

                    historyView.showError(
                        "Invalid menu option. Please try again."
                    );
            }
        }
    }


     public void reprintReceipt() {

        if (history == null || receiptView == null) {
            return;
        }

        while (true) {

            String transactionID = receiptView.readString(
                "Enter transaction ID: "
            );

            if (!InputValidator.isNotBlank(transactionID)) {

                receiptView.showError(
                    "Transaction ID cannot be blank. Please try again."
                );

                continue;
            }

            Transaction transaction = history.findByID(
                transactionID.trim()
            );

            if (transaction == null) {

                receiptView.showError(
                    "Transaction not found. Please try again."
                );

                continue;
            }

            if (!InputValidator.isNotBlank(transaction.getReceiptNo())) {

                receiptView.showError(
                    "No receipt is available for this transaction. Please try another transaction."
                );

                continue;
            }

            try {

                Receipt receipt = new Receipt(
                    transaction.getReceiptNo(),
                    transaction.getDateTime(),
                    transaction
                );

                receiptView.displayReprint(receipt);
                return;

            } catch (IllegalArgumentException e) {

                receiptView.showError(e.getMessage());
                return;
            }
        }
    }
     public void voidTransaction() {

        if (history == null || historyView == null
                || checkoutService == null) {

            return;
        }

        // Displays transactions before requesting an ID
        historyView.displayTransactions(
            history.viewTransactionHistory()
        );

        Transaction transaction;

        // Repeats the transaction ID field until a valid transaction is found
        while (true) {

            String transactionID = historyView.readString(
                "Enter transaction ID to void: "
            );

            if (!InputValidator.isNotBlank(transactionID)) {

                historyView.showError(
                    "Transaction ID cannot be blank. Please try again."
                );

                continue;
            }

            transaction = history.findByID(transactionID.trim());

            if (transaction == null) {

                historyView.showError(
                    "Transaction not found. Please try again."
                );

                continue;
            }

            if ("CANCELLED".equalsIgnoreCase(transaction.getStatus())) {

                historyView.showError(
                    "This transaction has already been voided. Please try another transaction."
                );

                continue;
            }

            break;
        }

        // Requests confirmation
        if (!historyView.confirm(
                "Are you sure you want to void this transaction?")) {

            return;
        }

        // Cancels the transaction through the service
        try {

            boolean cancelled =
                checkoutService.cancelTransaction(transaction);

            if (cancelled) {

                historyView.showMessage(
                    "Transaction successfully voided."
                );

            } else {

                historyView.showError(
                    "Unable to void transaction."
                );
            }

        } catch (IllegalArgumentException | IllegalStateException e) {

            historyView.showError(e.getMessage());
        }
    }

    // Cashier
    public void viewMyTransactions(Cashier cashier) {

        if (cashier == null || history == null || historyView == null) {
            return;
        }

        LinkedList<Transaction> cashierTransactions =
            history.getByCashier(cashier.getUserID());

        if (cashierTransactions == null || cashierTransactions.size() == 0) {

            historyView.showMessage(
                "You have no transaction history."
            );

            return;
        }

        historyView.displayTransactions(cashierTransactions);
    }

    // Customer
     public void viewMyHistory(Customer customer) {

        if (customer == null || history == null || historyView == null) {
            return;
        }

        LinkedList<Transaction> customerTransactions =
            history.getByCustomer(customer.getUserID());

        if (customerTransactions == null || customerTransactions.size() == 0) {

            historyView.showMessage(
                "You have no booking history."
            );

            return;
        }

        historyView.displayTransactions(customerTransactions);
    }
}
