package view;
import models.*;
import datastructures.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import utility.*;

public class SalesView extends BaseView {

    // Displays the sales menu
    public int showSalesMenu() {
        showMessage("\n============= SALES REPORTS ==============");
        showMessage("[1] View All Sales");
        showMessage("[2] View Sales by Date");
        showMessage("[3] View Sales by Date Range");
        showMessage("[4] View Total Revenue");
        showMessage("[0] Back");
        showMessage("------------------------------------------");
        return readInt("Choose an option (0 to go back): ", 0, 4);
    }

    // Asks for a date as text; validation and "0" (go back) are handled by LoopUtility.
    public String promptDateText(String label) {
        return readString(label + ", yyyy-MM-dd (0 to go back): ");
    }

    // Requests a valid date. Enter 0 to go back.
    public LocalDate promptDate() {
        while (true) {
            showMessage("------------------------------------------");
            String input = readString("Enter date (YYYY-MM-DD, 0 to go back): ");

            if (input.equals("0")) return null;
            if (InputValidator.isValidDate(input)) return LocalDate.parse(input.trim());

            showError("Invalid date. Please use YYYY-MM-DD.");
        }
    }

    // Displays the sales records as a table, one row per transaction
    public void displaySales(LinkedList<Sales> sales) {
        if (sales == null || sales.size() == 0) {
            showMessage("No sales records found.");
            return;
        }

        String[] headers = {"Sales ID", "Transaction", "Date", "Ticket Revenue", "Product Revenue", "Discount", "Total Revenue"};
        ArrayList<String[]> rows = new ArrayList<String[]>();

        for (int i = 0; i < sales.size(); i++) {
            Sales s = sales.get(i);

            rows.add(new String[] {
                s.getSalesID(), "T" + s.getSalesID().substring(1), String.valueOf(s.getDate()),
                String.format(Locale.ROOT, "%.2f", s.getTicketRevenue()),
                String.format(Locale.ROOT, "%.2f", s.getProductRevenue()),
                String.format(Locale.ROOT, "%.2f", s.getDiscountTotal()),
                String.format(Locale.ROOT, "%.2f", s.getTotalRevenue())
            });
        }

        printTable("SALES RECORDS", headers, rows);
    }

    // Displays one sales summary
    public void displaySalesSummary(Sales sales) {
        if (sales == null) {
            showError("Sales record not found.");
            return;
        }

        showMessage("------------------------------------------");
        showMessage("Sales ID: " + sales.getSalesID());
        showMessage("Date: " + sales.getDate());
        showMessage("Ticket Revenue: " + String.format(Locale.ROOT, "%.2f", sales.getTicketRevenue()));
        showMessage("Product Revenue: " + String.format(Locale.ROOT, "%.2f", sales.getProductRevenue()));
        showMessage("Discount Total: " + String.format(Locale.ROOT, "%.2f", sales.getDiscountTotal()));
        showMessage("Transactions: " + sales.getTransactionCount());
        showMessage("Total Revenue: " + String.format(Locale.ROOT, "%.2f", sales.getTotalRevenue()));
        showMessage("------------------------------------------");
    }
}