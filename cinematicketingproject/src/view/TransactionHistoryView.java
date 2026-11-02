package view;

import models.*;
import datastructures.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionHistoryView extends BaseView {

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public int showHistoryMenu() {
        showMessage("");
        showMessage("========== TRANSACTION HISTORY ==========");
        showMessage("[1] View All Transactions");
        showMessage("[2] Filter Transactions");
        showMessage("[3] Search Transaction");
        showMessage("[0] Back");
        showMessage("----------------------------------------");

        return readInt("Choose an option (0 to go back): ", 0, 3);
    }

    public int promptFilterOption() {
        showMessage("");
        showMessage("========== FILTER TRANSACTIONS ==========");
        showMessage("[1] Status");
        showMessage("[2] Booking Type");
        showMessage("[3] Customer ID");
        showMessage("[4] Cashier ID");
        showMessage("[5] Date Range");
        showMessage("[0] Back");
        showMessage("----------------------------------------");

        return readInt("Choose an option (0 to go back): ", 0, 5);
    }

    // Shows the transactions as a table. Used by View All, Filter, Search and the void/reprint screens.
    public void displayTransactions(LinkedList<Transaction> transactions) {
        if (transactions == null || transactions.size() == 0) {
            showMessage("No transactions found.");
            return;
        }

        ArrayList<String[]> rows = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            rows.add(new String[] {
                t.getTransactionID(),
                String.valueOf(t.getBookingType()),
                t.getCustomerID() == null ? "N/A" : t.getCustomerID(),
                t.getCashierID() == null ? "N/A" : t.getCashierID(),
                t.getDateTime() == null ? "N/A" : t.getDateTime().format(DATE_FORMAT),
                String.format(Locale.ROOT, "%.2f", t.getTotalAmount()),
                String.valueOf(t.getStatus()),
                t.getReceiptNo() == null ? "N/A" : t.getReceiptNo()
            });
        }

        printTable("TRANSACTIONS",
            new String[] {"ID", "Booking Type", "Customer", "Cashier", "Date", "Total", "Status", "Receipt No."},
            rows);
    }

    public void displayTransactionDetails(Transaction transaction) {
        if (transaction == null) {
            showError("Transaction not found.");
            return;
        }

        showMessage("----------------------------------------");
        showMessage("Transaction ID: " + transaction.getTransactionID());
        showMessage("Booking Type: " + transaction.getBookingType());

        showMessage(
            "Customer ID: " +
            (transaction.getCustomerID() == null
                ? "N/A" : transaction.getCustomerID())
        );

        showMessage(
            "Cashier ID: " +
            (transaction.getCashierID() == null
                ? "N/A" : transaction.getCashierID())
        );

        showMessage(
            "Date: " +
            (transaction.getDateTime() == null
                ? "N/A" : transaction.getDateTime().format(DATE_FORMAT))
        );

        showMessage("Ticket Count: " + transaction.getTickets().size());
        showMessage("Product Count: " + transaction.getProducts().size());

        showMessage("Subtotal: " + transaction.getSubtotal());
        showMessage("Discount: " + transaction.getDiscountAmount());
        showMessage("Tax: " + transaction.getTaxAmount());
        showMessage("Total: " + transaction.getTotalAmount());

        showMessage("Amount Paid: " + transaction.getAmountPaid());
        showMessage("Change: " + transaction.getChange());

        showMessage(
            "Payment Method: " +
            (transaction.getPaymentMethod() == null
                ? "N/A" : transaction.getPaymentMethod())
        );

        showMessage("Status: " + transaction.getStatus());

        showMessage(
            "Receipt No.: " +
            (transaction.getReceiptNo() == null
                ? "N/A" : transaction.getReceiptNo())
        );

        showMessage("----------------------------------------");
    }
}