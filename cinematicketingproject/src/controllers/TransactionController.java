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
        if (history == null || historyView == null) return;

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
                    // Show the table first so the admin can see what to filter.
                    historyView.displayTransactions(history.viewTransactionHistory());
                    int filterChoice = historyView.promptFilterOption();
                    if (filterChoice == 0) break;

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
                        if (startDate == null) break;

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

                    final String field = filterChoice == 1 ? "status"
                        : filterChoice == 2 ? "bookingType"
                        : filterChoice == 3 ? "customerID" : "cashierID";

                    LoopUtility.retryUntilValidResult(
                        () -> historyView.readString("Filter value (0 to go back): "),
                        value -> {
                            historyView.displayTransactions(history.filterTransactionHistory(field, value.trim()));
                            return true;
                        }, historyView::showError, "0");
                    break;
                }

                case 3:
                    // Show the table first so the admin can see the IDs.
                    historyView.displayTransactions(history.viewTransactionHistory());
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
    // Cashier: reprint a receipt
    public void reprintReceipt(Cashier cashier) {
        if (cashier == null || history == null || receiptView == null || historyView == null) return;

        LinkedList<Transaction> transactions = history.getByCashier(cashier.getUserID());

        if (transactions == null || transactions.size() == 0) {
            receiptView.showMessage("You have no transactions.");
            return;
        }

        historyView.displayTransactions(transactions);

        while (true) {
            String id = receiptView.readString("Enter transaction ID to reprint (0 to go back): ");
            if (id.equals("0")) return;

            Transaction transaction = findTransaction(transactions, id);

            if (transaction == null) {
                receiptView.showError("Transaction not found under your account.");
                continue;
            }

            if (!InputValidator.isNotBlank(transaction.getReceiptNo())) {
                receiptView.showError("No receipt is available for this transaction.");
                continue;
            }

            try {
                receiptView.displayReprint(new Receipt(transaction.getReceiptNo(), transaction.getDateTime(), transaction));
            } catch (IllegalArgumentException e) {
                receiptView.showError(e.getMessage());
            }
            return;
        }
    }

    // Admin: void any transaction
    public void voidTransaction() {
        if (history == null || historyView == null || checkoutService == null) return;

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

        if (transaction == null) return;

        if (!historyView.confirm("Are you sure you want to void this transaction?")) {
            historyView.showMessage("Transaction void cancelled.");
            return;
        }

        voidAndReport(transaction);
    }

    // Cashier: void own walk-in transactions only
    public void voidTransaction(Cashier cashier) {
    if (cashier == null || history == null || historyView == null || checkoutService == null) return;

    LinkedList<Transaction> transactions = history.getByCashier(cashier.getUserID());

    if (transactions == null || transactions.size() == 0) {
        historyView.showMessage("You have no transactions to void.");
        return;
    }

    historyView.displayTransactionTable(transactions); 

        while (true) {
            String id = historyView.readString("Enter transaction ID to void (0 to go back): ");
            if (id.equals("0")) return;

            Transaction transaction = findTransaction(transactions, id);

            if (transaction == null) {
                historyView.showError("Transaction not found under your account.");
                continue;
            }

            if (!transaction.getBookingType().equalsIgnoreCase("WALK_IN")) {
                historyView.showError("Only walk-in transactions can be voided here.");
                continue;
            }

            if (!transaction.getStatus().equalsIgnoreCase("COMPLETED")) {
                historyView.showError("Only completed transactions can be voided.");
                continue;
            }

            // Checks whether tickets have been used or the show has started
            LinkedList<Ticket> tickets = transaction.getTickets();

            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);

                if (ticket.getStatus().equalsIgnoreCase("USED")) {
                    historyView.showError("Used tickets cannot be voided.");
                    return;
                }

                if (ticket.getSchedule() == null || ticket.getSchedule().getShowDateTime() == null
                        || !ticket.getSchedule().getShowDateTime().isAfter(java.time.LocalDateTime.now())) {
                    historyView.showError("The show has already started. This transaction cannot be voided.");
                    return;
                }
            }

            historyView.displayTransactionDetails(transaction);

            if (!historyView.confirm("Are you sure you want to void this transaction?")) {
                historyView.showMessage("Transaction void cancelled.");
                return;
            }

            voidAndReport(transaction);
            return;
        }
    }

    // Cashier
    public void viewMyTransactions(Cashier cashier) {
    if (cashier == null || history == null || historyView == null || receiptView == null) return;

    LinkedList<Transaction> transactions = history.getByCashier(cashier.getUserID());

    if (transactions == null || transactions.size() == 0) {
        historyView.showMessage("You have no transaction history.");
        return;
    }

    openHistoryMenu(transactions, true);
}

    // Customer: menu, then a table of options, then the printed receipt or ticket
    public void viewMyHistory(Customer customer) {
    if (customer == null || history == null || historyView == null || receiptView == null) return;

    LinkedList<Transaction> transactions = history.getByCustomer(customer.getUserID());

    if (transactions == null || transactions.size() == 0) {
        historyView.showMessage("You have no booking history.");
        return;
    }

    openHistoryMenu(transactions, false);
}

    // Table first, then the full receipt (with tickets) of the chosen transaction
    private void openHistoryMenu(LinkedList<Transaction> transactions, boolean forCashier) {
    int choice = -1;

    while (choice != 0) {
        choice = historyView.showBookingHistoryMenu(forCashier);

        if (choice == 1) viewPastTransactions(transactions);
        else if (choice == 2) viewMyTickets(transactions);
    }
}

    // Table of all the customer's tickets, then the chosen ticket
    private void viewPastTransactions(LinkedList<Transaction> transactions) {
    historyView.displayTransactionTable(transactions);

    while (true) {
        String id = historyView.readString("Enter transaction ID to view (0 to go back): ");
        if (id.equals("0")) return;

        Transaction transaction = findTransaction(transactions, id);

        if (transaction == null) {
            historyView.showError("Transaction not found under your account.");
            continue;
        }

        if (!InputValidator.isNotBlank(transaction.getReceiptNo())) {
            historyView.showError("No receipt is available for this transaction.");
            continue;
        }

        try {
            receiptView.displayReceipt(new Receipt(transaction.getReceiptNo(), transaction.getDateTime(), transaction));
        } catch (IllegalArgumentException e) {
            historyView.showError(e.getMessage());
        }
        return;
    }
}
// Table of all the tickets in the list, then the chosen ticket
private void viewMyTickets(LinkedList<Transaction> transactions) {
    LinkedList<Ticket> allTickets = new LinkedList<Ticket>();

    for (int i = 0; i < transactions.size(); i++) {
        LinkedList<Ticket> tickets = transactions.get(i).getTickets();
        for (int j = 0; j < tickets.size(); j++) allTickets.insertAtEnd(tickets.get(j));
    }

    if (allTickets.size() == 0) {
        historyView.showMessage("You have no tickets.");
        return;
    }

    historyView.displayTicketTable(allTickets);

    while (true) {
        String id = historyView.readString("Enter ticket ID to view (0 to go back): ");
        if (id.equals("0")) return;

        for (int i = 0; i < allTickets.size(); i++) {
            if (allTickets.get(i).getTicketID().equalsIgnoreCase(id)) {
                historyView.displayTicketDetails(allTickets.get(i));
                return;
            }
        }

        historyView.showError("Ticket not found under your account.");
    }
}
    // Finds a transaction by ID in a list, or null
    private Transaction findTransaction(LinkedList<Transaction> list, String id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTransactionID().equalsIgnoreCase(id.trim())) return list.get(i);
        }
        return null;
    }

    // Voids a transaction and prints the result
    private void voidAndReport(Transaction transaction) {
        try {
            if (checkoutService.cancelTransaction(transaction)) historyView.showMessage("Transaction successfully voided.");
            else historyView.showError("Unable to void transaction.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            historyView.showError(e.getMessage());
        }
    }
}