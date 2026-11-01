package view;
import controllers.*;
import models.*;
import datastructures.*;
import java.time.LocalDate;
import java.util.Locale;
import repositories.*;
import utility.*;
import service.*;
public class SalesView extends BaseView {


    // Displays the sales menu

    public int showSalesMenu() {

        showMessage("========== SALES REPORTS ==========");
        showMessage("[1] View All Sales");
        showMessage("[2] View Sales by Date");
        showMessage("[3] View Sales by Date Range");
        showMessage("[4] View Total Revenue");
        showMessage("[0] Back");

        return readInt("Choose an option: ", 0, 4);
    }


    // Requests a valid date

    public LocalDate promptDate() {

        while (true) {

            String input = readString("Enter date (YYYY-MM-DD): ");

            if (InputValidator.isValidDate(input)) {
                return LocalDate.parse(input.trim());
            }

            showError("Invalid date. Please use YYYY-MM-DD.");
        }
    }


    // Displays a list of sales records

    public void displaySales(LinkedList<Sales> sales) {

        if (sales == null || sales.size() == 0) {
            showMessage("No sales records found.");
            return;
        }

        showMessage("========== SALES RECORDS ==========");

        for (int i = 0; i < sales.size(); i++) {

            displaySalesSummary(sales.get(i));
        }

        showMessage("========== END OF SALES RECORDS ==========");
    }


    // Displays one sales summary

    public void displaySalesSummary(Sales sales) {

        if (sales == null) {
            showError("Sales record not found.");
            return;
        }

        showMessage("----------------------------------------");
        showMessage("Sales ID: " + sales.getSalesID());
        showMessage("Date: " + sales.getDate());

        showMessage("Ticket Revenue: " +
            String.format(Locale.ROOT, "%.2f", sales.getTicketRevenue()));

        showMessage("Product Revenue: " +
            String.format(Locale.ROOT, "%.2f", sales.getProductRevenue()));

        showMessage("Discount Total: " +
            String.format(Locale.ROOT, "%.2f", sales.getDiscountTotal()));

        showMessage("Transactions: " + sales.getTransactionCount());

        showMessage("Total Revenue: " +
            String.format(Locale.ROOT, "%.2f", sales.getTotalRevenue()));

        showMessage("----------------------------------------");
    }

}
