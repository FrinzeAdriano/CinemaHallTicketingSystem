package repositories;
import controllers.*;
import models.*;
import datastructures.*;
import java.time.LocalDate;
import view.*;
import utility.*;
import service.*;

public class SalesRepository {

    private LinkedList sales = new LinkedList();


    // Records a completed transaction
    public void recordSale(Transaction transaction) {

        if (transaction == null) {
            return;
        }

        if (!transaction.getStatus().equalsIgnoreCase("COMPLETED")) {
            return;
        }

        LocalDate transactionDate =
            transaction.getDateTime().toLocalDate();

        Sales dailySales = getSalesByDate(transactionDate);

        if (dailySales == null) {

            dailySales = new Sales(
                "SALES-" + transactionDate,
                transactionDate,
                0,
                0,
                0,
                0
            );

            sales.insertAtEnd(dailySales);
        }

        dailySales.addTransaction(transaction);
    }

    // Reverses a completed transaction from sales
    public void reverseSale(Transaction transaction) {

        if (transaction == null) {
            return;
        }

        LocalDate transactionDate =
            transaction.getDateTime().toLocalDate();

        Sales dailySales = getSalesByDate(transactionDate);

        if (dailySales != null) {

            dailySales.removeTransaction(transaction);

            if (dailySales.getTransactionCount() == 0) {
                sales.remove(dailySales);
            }
        }
    }

    // Retrieves sales for a specific date
    public Sales getSalesByDate(LocalDate date) {

        for (int i = 0; i < sales.size(); i++) {

            Sales dailySales = (Sales) sales.get(i);

            if (dailySales.getDate().equals(date)) {
                return dailySales;
            }
        }

        return null;
    }


    // Retrieves sales within a date range
    public LinkedList getSalesByRange(
            LocalDate startDate, LocalDate endDate) {

        LinkedList filtered =
            new LinkedList();

        for (int i = 0; i < sales.size(); i++) {

            Sales dailySales = (Sales) sales.get(i);

            if (!dailySales.getDate().isBefore(startDate)
                    && !dailySales.getDate().isAfter(endDate)) {

                filtered.insertAtEnd(dailySales);
            }
        }

        return filtered;
    }

    // Returns all sales records
    public LinkedList viewSales() {
        return sales;
    }


     // Calculates total revenue from all sales records
    public double getTotalRevenue() {

        double totalRevenue = 0;

        for (int i = 0; i < sales.size(); i++) {
            totalRevenue += ((Sales) sales.get(i)).getTotalRevenue();
        }

        return totalRevenue;
    }

}
