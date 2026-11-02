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
//I also found two problems worth fixing: recordSale() can add the same transaction more than once, and reverseSale() can subtract a transaction more than once.
private final LinkedList<String> recordedTransactionIDs =
        new LinkedList<String>();
private int salesCounter = 0;

    // Records a completed transaction
    public void recordSale(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                "Invalid input. Transaction cannot be null."
            );
        }

        if (!transaction.getStatus().equalsIgnoreCase("COMPLETED")) {
            throw new IllegalStateException(
                "Only completed transactions can be recorded as sales."
            );
        }

        if (transaction.getDateTime() == null) {
            throw new IllegalArgumentException(
                "Invalid input. Transaction date cannot be null."
            );
        }

        // Prevent duplicate sales recording
        if (recordedTransactionIDs.contains(
                transaction.getTransactionID())) {

            throw new IllegalArgumentException(
                "Invalid input. Transaction has already been recorded in sales."
            );
        }

        LocalDate transactionDate =
            transaction.getDateTime().toLocalDate();

        Sales dailySales = getSalesByDate(transactionDate);

        boolean newSalesRecord = false;

        // Create a new daily sales record when none exists
        if (dailySales == null) {

            if (salesCounter >= 999) {
                throw new IllegalStateException(
                    "Operation cannot be completed. Sales ID limit reached."
                );
            }

            int nextNumber = salesCounter + 1;

            String salesID = String.format(
                Locale.ROOT,
                "S%03d",
                nextNumber
            );

            dailySales = new Sales(
                salesID,
                transactionDate,
                0,
                0,
                0,
                0
            );

            newSalesRecord = true;
        }

        // Add transaction to the daily sales record
        dailySales.addTransaction(transaction);

        // Store the new sales record
        if (newSalesRecord) {
            sales.insertAtEnd(dailySales);
            salesCounter++;
        }

        // Track the recorded transaction
        recordedTransactionIDs.insertAtEnd(
            transaction.getTransactionID()
        );
    }


    // Reverses a completed transaction from sales
      public void reverseSale(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                "Invalid input. Transaction cannot be null."
            );
        }

        // Ensure the transaction was previously recorded
        if (!recordedTransactionIDs.contains(
                transaction.getTransactionID())) {

            throw new IllegalStateException(
                "Operation cannot be completed. Transaction was not recorded in sales."
            );
        }

        if (transaction.getDateTime() == null) {
            throw new IllegalArgumentException(
                "Invalid input. Transaction date cannot be null."
            );
        }

        LocalDate transactionDate =
            transaction.getDateTime().toLocalDate();

        Sales dailySales = getSalesByDate(transactionDate);

        if (dailySales == null) {
            throw new IllegalStateException(
                "Operation cannot be completed. Daily sales record was not found."
            );
        }

        // Remove transaction revenue
        dailySales.removeTransaction(transaction);

        // Remove the transaction from the tracking list
        recordedTransactionIDs.remove(
            transaction.getTransactionID()
        );

        // Remove empty daily sales records
        if (dailySales.getTransactionCount() == 0) {
            sales.remove(dailySales);
        }
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
