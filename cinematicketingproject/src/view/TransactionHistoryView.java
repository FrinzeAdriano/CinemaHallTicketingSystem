package view;
import controllers.*;
import models.*;
import datastructures.*;
import repositories.*;
import utility.*;
import service.*;


public class TransactionHistoryView extends BaseView {


   // Displays the transaction history menu

    public int showHistoryMenu() {

        showMessage("========== TRANSACTION HISTORY ==========");
        showMessage("[1] View All Transactions");
        showMessage("[2] Filter Transactions");
        showMessage("[3] Search Transaction");
        showMessage("[0] Back");

        return readInt("Choose an option: ", 0, 3);
    }


    // Requests a filter option

    public int promptFilterOption() {

        showMessage("========== FILTER TRANSACTIONS ==========");
        showMessage("[1] Status");
        showMessage("[2] Booking Type");
        showMessage("[3] Customer ID");
        showMessage("[4] Cashier ID");
        showMessage("[5] Date Range");
        showMessage("[0] Back");

        return readInt("Choose a filter: ", 0, 5);
    }


    // Displays a list of transactions

    public void displayTransactions(
            LinkedList<Transaction> transactions) {

        if (transactions == null || transactions.size() == 0) {
            showMessage("No transactions found.");
            return;
        }

        showMessage("========== TRANSACTIONS ==========");

        for (int i = 0; i < transactions.size(); i++) {

            displayTransactionDetails(transactions.get(i));
        }

        showMessage("========== END OF TRANSACTIONS ==========");
    }


    // Displays one transaction's details

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

        showMessage("Date: " + transaction.getDateTime());

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
