package view;

import models.*;
import datastructures.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionHistoryView extends BaseView {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SHOW_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Admin menus
    public int showHistoryMenu() {
        showMessage("========== TRANSACTION HISTORY ===========");
        showMessage("[1] View All Transactions");
        showMessage("[2] Filter Transactions");
        showMessage("[3] Search Transaction");
        showMessage("[0] Back");
        showMessage("------------------------------------------");
        return readInt("Choice: ", 0, 3);
    }

    public int promptFilterOption() {
        showMessage("========== FILTER TRANSACTIONS ===========");
        showMessage("[1] Status");
        showMessage("[2] Booking Type");
        showMessage("[3] Customer ID");
        showMessage("[4] Cashier ID");
        showMessage("[5] Date Range");
        showMessage("[0] Back");
        showMessage("------------------------------------------");
        return readInt("Choice: ", 0, 5);
    }

    // Customer booking history menu
    public int showBookingHistoryMenu(boolean forCashier) {
    showMessage(forCashier ? "\n============ MY TRANSACTIONS ============="
                           : "\n=========== MY BOOKING HISTORY ===========");
    showMessage("[1] Past transactions");
    showMessage("[2] Tickets");
    showMessage("[0] Back");
    showMessage("------------------------------------------");
    return readInt("Choose an option (0 to go back): ", 0, 2);
}

    // Table of options: the customer picks a transaction ID
    public void displayTransactionTable(LinkedList<Transaction> transactions) {
        String[] headers = {"ID", "Date", "Tickets", "Total", "Payment", "Status"};
        ArrayList<String[]> rows = new ArrayList<String[]>();

        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            String payment = t.getPaymentMethod() == null ? "N/A" : t.getPaymentMethod();

            if (t.getPaymentMethod() != null && t.getPaymentProvider() != null) {
                payment += " (" + t.getPaymentProvider() + ")";
            }

            rows.add(new String[] {
                t.getTransactionID(), t.getDateTime().format(DATE_FORMAT),
                String.valueOf(t.getTickets().size()), String.format("%.2f", t.getTotalAmount()),
                payment, t.getStatus()
            });
        }

        printTable("MY TRANSACTIONS", headers, rows);
    }

    // Table of options: the customer picks a ticket ID
    public void displayTicketTable(LinkedList<Ticket> tickets) {
        String[] headers = {"Ticket ID", "Movie", "Show Time", "Seat", "Net Price", "Status"};
        ArrayList<String[]> rows = new ArrayList<String[]>();

        for (int i = 0; i < tickets.size(); i++) {
            Ticket t = tickets.get(i);

            rows.add(new String[] {
                t.getTicketID(), t.getSchedule().getMovie().getTitle(),
                t.getSchedule().getShowDateTime().format(SHOW_FORMAT), t.getSeat().getSeatID(),
                String.format("%.2f", t.getNetPrice()), t.getStatus()
            });
        }

        printTable("MY TICKETS", headers, rows);
    }

    // The printed ticket
    public void displayTicketDetails(Ticket ticket) {
        showMessage("\n============= TICKET DETAILS =============");
        showMessage("Ticket ID: " + ticket.getTicketID());
        showMessage("Movie: " + ticket.getSchedule().getMovie().getTitle());
        showMessage("Schedule: " + ticket.getSchedule().getScheduleID());
        showMessage("Show Time: " + ticket.getSchedule().getShowDateTime().format(SHOW_FORMAT));
        showMessage("Seat: " + ticket.getSeat().getSeatID());
        showMessage("Price: " + String.format("%.2f", ticket.getPrice()));
        showMessage("Discount Type: " + (ticket.getDiscountType().equalsIgnoreCase("NONE") ? "None" : ticket.getDiscountType()));
        showMessage("Discount Amount: " + String.format("%.2f", ticket.getDiscountAmount()));
        showMessage("Net Price: " + String.format("%.2f", ticket.getNetPrice()));
        showMessage("Status: " + ticket.getStatus());
        showMessage("==========================================");
    }
    
    // Admin and cashier transaction lists
    public void displayTransactions(LinkedList<Transaction> transactions) {
        if (transactions == null || transactions.size() == 0) {
            showMessage("No transactions found.");
            return;
        }
        ArrayList<String[]> rows = new ArrayList<String[]>();
        showMessage("============== TRANSACTIONS ==============");

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

        showMessage("========== END OF TRANSACTIONS ===========");
    }

    public void displayTransactionDetails(Transaction transaction) {
        if (transaction == null) {
            showError("Transaction not found.");
            return;
        }

        showMessage("------------------------------------------");
        showMessage("Transaction ID: " + transaction.getTransactionID());
        showMessage("Booking Type: " + transaction.getBookingType());
        showMessage("Customer ID: " + (transaction.getCustomerID() == null ? "N/A" : transaction.getCustomerID()));
        showMessage("Cashier ID: " + (transaction.getCashierID() == null ? "N/A" : transaction.getCashierID()));
        showMessage("Date: " + (transaction.getDateTime() == null ? "N/A" : transaction.getDateTime().format(DATE_FORMAT)));
        showMessage("Ticket Count: " + transaction.getTickets().size());
        showMessage("Product Count: " + transaction.getProducts().size());
        showMessage("Subtotal: " + transaction.getSubtotal());
        showMessage("Discount: " + transaction.getDiscountAmount());
        showMessage("Tax: " + transaction.getTaxAmount());
        showMessage("Total: " + transaction.getTotalAmount());
        showMessage("Amount Paid: " + transaction.getAmountPaid());
        showMessage("Change: " + transaction.getChange());
        showMessage("Payment Method: " + (transaction.getPaymentMethod() == null ? "N/A"
            : transaction.getPaymentMethod()
                + (transaction.getPaymentProvider() == null ? "" : " (" + transaction.getPaymentProvider() + ")")));
        showMessage("Status: " + transaction.getStatus());
        showMessage("Receipt No.: " + (transaction.getReceiptNo() == null ? "N/A" : transaction.getReceiptNo()));
        showMessage("------------------------------------------");
    }
}