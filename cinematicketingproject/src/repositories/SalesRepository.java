package repositories;
import controllers.*;
import models.*;
import datastructures.*;
import java.time.LocalDate;
import java.util.Locale;
import view.*;
import utility.*;
import service.*;

public class SalesRepository {

    private final LinkedList<Sales> sales =
        new LinkedList<Sales>();

private final LinkedList<String> recordedTransactionIDs =
        new LinkedList<String>();
private int salesCounter = 0;

    // Records a completed transaction
    // Records a completed transaction as its own sales record (S001 for T001, and so on)
public void recordSale(Transaction transaction) {
    if (transaction == null) {
        throw new IllegalArgumentException("Invalid input. Transaction cannot be null.");
    }

    if (!transaction.getStatus().equalsIgnoreCase("COMPLETED")) {
        throw new IllegalStateException("Only completed transactions can be recorded as sales.");
    }

    if (transaction.getDateTime() == null) {
        throw new IllegalArgumentException("Invalid input. Transaction date cannot be null.");
    }

    // Prevent duplicate sales recording
    if (recordedTransactionIDs.contains(transaction.getTransactionID())) {
        throw new IllegalArgumentException("Invalid input. Transaction has already been recorded in sales.");
    }

    Sales record = new Sales("S" + transaction.getTransactionID().substring(1),
        transaction.getDateTime().toLocalDate(), 0, 0, 0, 0);

    record.addTransaction(transaction);
    sales.insertAtEnd(record);
    recordedTransactionIDs.insertAtEnd(transaction.getTransactionID());
}


    // Reverses a completed transaction from sales
      public void reverseSale(Transaction transaction) {
    if (transaction == null) {
        throw new IllegalArgumentException("Invalid input. Transaction cannot be null.");
    }

    if (!recordedTransactionIDs.contains(transaction.getTransactionID())) {
        throw new IllegalStateException("Operation cannot be completed. Transaction was not recorded in sales.");
    }

    String salesID = "S" + transaction.getTransactionID().substring(1);

    for (int i = 0; i < sales.size(); i++) {
        if (sales.get(i).getSalesID().equals(salesID)) {
            sales.get(i).removeTransaction(transaction);
            sales.delete(i);
            recordedTransactionIDs.remove(transaction.getTransactionID());
            return;
        }
    }

    throw new IllegalStateException("Operation cannot be completed. Sales record was not found.");
}

    // Retrieves sales for a specific date
      public Sales getSalesByDate(LocalDate date) {

        if (date == null) {
            throw new IllegalArgumentException(
                "Invalid input. Sales date cannot be null."
            );
        }

        for (int i = 0; i < sales.size(); i++) {

            Sales dailySales = sales.get(i);

            if (dailySales.getDate().equals(date)) {
                return dailySales;
            }
        }

        return null;
    }


    // Retrieves sales within a date range
   public LinkedList<Sales> getSalesByRange(
            LocalDate startDate, LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                "Invalid input. Start date and end date cannot be null."
            );
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                "Invalid input. End date cannot be before start date."
            );
        }

        LinkedList<Sales> filtered =
            new LinkedList<Sales>();

        for (int i = 0; i < sales.size(); i++) {

            Sales dailySales = sales.get(i);

            if (!dailySales.getDate().isBefore(startDate)
                    && !dailySales.getDate().isAfter(endDate)) {

                filtered.insertAtEnd(dailySales);
            }
        }

        return filtered;
    }

    // Returns all sales records
    public LinkedList<Sales> viewSales() {

        LinkedList<Sales> copy =
            new LinkedList<Sales>();

        for (int i = 0; i < sales.size(); i++) {
            copy.insertAtEnd(sales.get(i));
        }

        return copy;
    }



     // Calculates total revenue from all sales records
    public double getTotalRevenue() {

        double totalRevenue = 0;

        for (int i = 0; i < sales.size(); i++) {

            totalRevenue += sales.get(i).getTotalRevenue();

            if (!Double.isFinite(totalRevenue)) {
                throw new IllegalStateException(
                    "Operation cannot be completed. Total revenue is invalid."
                );
            }
        }

        return totalRevenue;
    }


}
