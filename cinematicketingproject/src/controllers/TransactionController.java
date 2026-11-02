package controllers;

import datastructures.LinkedList;
import java.time.LocalDate;
import models.*;
import repositories.SalesRepository;
import repositories.TransactionHistory;
import service.CheckoutService;
import utility.InputValidator;
import utility.LoopUtility;
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

        int choice = -1;
        while (choice != 0) {
            choice = historyView.showHistoryMenu();

            // Every option needs at least one transaction.
            if (choice >= 1 && choice <= 3 && history.viewTransactionHistory().size() == 0) {
                historyView.showError("Invalid input. There are no transactions yet.");
                continue;
            }

            switch (choice) {
                case 1:
                    historyView.displayTransactions(history.viewTransactionHistory());
                    break;

                case 2: {
                    int filterChoice = historyView.promptFilterOption();

                    if (filterChoice == 0) {
                        break;
                    }

                    if (filterChoice == 5) {
                        // Date range (0 to go back at either prompt)
                        LocalDate startDate = LoopUtility.retryUntilValidResult(
                            () -> historyView.readString("Start date, yyyy-MM-dd (0 to go back): "),
                            input -> {
                                if (!InputValidator.isValidDate(input)) {
                                    throw new IllegalArgumentException(
                                        "Invalid input. Start date must be a real date as yyyy-MM-dd.");
                                }
                                return LocalDate.parse(input.trim());
                            }, historyView::showError, "0");

                        if (startDate == null) {
                            break;
                        }

                        LocalDate endDate = LoopUtility.retryUntilValidResult(
                            () -> historyView.readString("End date, yyyy-MM-dd (0 to go back): "),
                            input -> {
                                if (!InputValidator.isValidDate(input)) {
                                    throw new IllegalArgumentException(
                                        "Invalid input. End date must be a real date as yyyy-MM-dd.");
                                }
                                LocalDate parsed = LocalDate.parse(input.trim());
                                if (parsed.isBefore(startDate)) {
                                    throw new IllegalArgumentException(
                                        "Invalid input. End date cannot be earlier than start date.");
                                }
                                return parsed;
                            }, historyView::showError, "0");

                        if (endDate != null) {
                            historyView.displayTransactions(history.filterByDateRange(startDate, endDate));
                        }
                        break;
                    }

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
                    final String filterField = field;

                    LoopUtility.retryUntilValidResult(
                        () -> historyView.readString("Filter value (0 to go back): "),
                        value -> {
                            historyView.displayTransactions(
                                history.filterTransactionHistory(filterField, value.trim()));
                            return true;
                        }, historyView::showError, "0");
                    break;
                }

                case 3:
                    LoopUtility.retryUntilValidResult(
                        () -> historyView.readString("Transaction ID (0 to go back): "),
                        id -> {
                            Transaction found = history.findByID(id);
                            if (found == null) {
                                throw new IllegalArgumentException(
                                    "Invalid input. Transaction " + id.trim() + " was not found.");
                            }
                            historyView.displayTransactionDetails(found);
                            return true;
                        }, historyView::showError, "0");
                    break;

                default:
                    break;   // 0 exits the loop
            }
        }
    }


       public void reprintReceipt(Cashier cashier) {

        if (cashier == null || history == null
                || receiptView == null || historyView == null) {
            return;
        }

        LinkedList<Transaction> transactions =
            history.getByCashier(cashier.getUserID());

        if (transactions == null || transactions.size() == 0) {

            receiptView.showMessage("You have no transactions.");
            return;
        }

        historyView.displayTransactions(transactions);

        while (true) {

            String transactionID = receiptView.readString(
                "Enter transaction ID to reprint (0 to go back): "
            );

            if (transactionID.trim().equals("0")) {
                return;
            }

            Transaction transaction = null;

            for (int i = 0; i < transactions.size(); i++) {

                Transaction current = transactions.get(i);

                if (current.getTransactionID()
                        .equalsIgnoreCase(transactionID.trim())) {

                    transaction = current;
                    break;
                }
            }

            if (transaction == null) {

                receiptView.showError(
                    "Transaction not found under your account."
                );

                continue;
            }

            if (!InputValidator.isNotBlank(transaction.getReceiptNo())) {

                receiptView.showError(
                    "No receipt is available for this transaction."
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

        if (history.viewTransactionHistory().size() == 0) {
            historyView.showError("Invalid input. There are no transactions yet.");
            return;
        }

        historyView.displayTransactions(history.viewTransactionHistory());

        Transaction transaction = LoopUtility.retryUntilValidResult(
            () -> historyView.readString("Transaction ID to void (0 to go back): "),
            id -> {
                Transaction found = history.findByID(id);
                if (found == null) {
                    throw new IllegalArgumentException(
                        "Invalid input. Transaction " + id.trim() + " was not found.");
                }
                if ("CANCELLED".equalsIgnoreCase(found.getStatus())) {
                    throw new IllegalArgumentException(
                        "Invalid input. This transaction has already been voided.");
                }
                return found;
            }, historyView::showError, "0");

        if (transaction == null) {
            return;
        }

        if (!historyView.confirm("Are you sure you want to void this transaction?")) {
            historyView.showMessage("Transaction void cancelled.");
            return;
        }

        try {
            if (checkoutService.cancelTransaction(transaction)) {
                historyView.showMessage("Transaction successfully voided.");
            } else {
                historyView.showError("Unable to void transaction.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            historyView.showError(e.getMessage());
        }
    }

    // CASHIER: Void own walk-in transactions only
    public void voidTransaction(Cashier cashier) {

        if (cashier == null || history == null
                || historyView == null || checkoutService == null) {
            return;
        }

        LinkedList<Transaction> transactions =
            history.getByCashier(cashier.getUserID());

        if (transactions == null || transactions.size() == 0) {

            historyView.showMessage(
                "You have no transactions to void."
            );

            return;
        }

        historyView.displayTransactions(transactions);

        while (true) {

            String transactionID = historyView.readString(
                "Enter transaction ID to void (0 to go back): "
            );

            if (transactionID.trim().equals("0")) {
                return;
            }

            Transaction transaction = null;

            for (int i = 0; i < transactions.size(); i++) {

                Transaction current = transactions.get(i);

                if (current.getTransactionID()
                        .equalsIgnoreCase(transactionID.trim())) {

                    transaction = current;
                    break;
                }
            }

            if (transaction == null) {

                historyView.showError(
                    "Transaction not found under your account."
                );

                continue;
            }

            if (!transaction.getBookingType().equalsIgnoreCase("WALK_IN")) {

                historyView.showError(
                    "Only walk-in transactions can be voided here."
                );

                continue;
            }

            if (!transaction.getStatus().equalsIgnoreCase("COMPLETED")) {

                historyView.showError(
                    "Only completed transactions can be voided."
                );

                continue;
            }

            // Checks whether tickets have been used or the show has started
            boolean canVoid = true;

            LinkedList<Ticket> tickets = transaction.getTickets();

            for (int i = 0; i < tickets.size(); i++) {

                Ticket ticket = tickets.get(i);

                if (ticket.getStatus().equalsIgnoreCase("USED")) {

                    historyView.showError(
                        "Used tickets cannot be voided."
                    );

                    canVoid = false;
                    break;
                }

                if (ticket.getSchedule() == null
                        || ticket.getSchedule().getShowDateTime() == null
                        || !ticket.getSchedule().getShowDateTime()
                            .isAfter(java.time.LocalDateTime.now())) {

                    historyView.showError(
                        "The show has already started. This transaction cannot be voided."
                    );

                    canVoid = false;
                    break;
                }
            }

            if (!canVoid) {
                return;
            }

            historyView.displayTransactionDetails(transaction);

            if (!historyView.confirm(
                    "Are you sure you want to void this transaction?")) {

                historyView.showMessage(
                    "Transaction void cancelled."
                );

                return;
            }

            try {

                if (checkoutService.cancelTransaction(transaction)) {

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

            return;
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