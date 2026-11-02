package repositories;
import controllers.*;
import models.*;
import datastructures.*;
import java.time.LocalDate;
import java.util.Locale;
import view.*;
import utility.*;
import service.*;

public class TransactionHistory {

     private final LinkedList<Transaction> transactionHistory =
        new LinkedList<Transaction>();


   
    // Adds a transaction

    public void addTransaction(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                "Invalid input. Transaction cannot be null."
            );
        }

        // Prevent duplicate transaction IDs
        for (int i = 0; i < transactionHistory.size(); i++) {

            Transaction existingTransaction =
                transactionHistory.get(i);

            if (existingTransaction.getTransactionID()
                    .equalsIgnoreCase(transaction.getTransactionID())) {

                throw new IllegalArgumentException(
                    "Invalid input. Transaction ID already exists."
                );
            }
        }

        transactionHistory.insertAtEnd(transaction);
    }

    // Returns all transactions
     public LinkedList<Transaction> viewTransactionHistory() {

        LinkedList<Transaction> copy =
            new LinkedList<Transaction>();

        for (int i = 0; i < transactionHistory.size(); i++) {
            copy.insertAtEnd(transactionHistory.get(i));
        }

        return copy;
    }


     // Finds a transaction using its ID
    public Transaction findByID(String transactionID) {

        if (!InputValidator.isNotBlank(transactionID)) {
            throw new IllegalArgumentException(
                "Invalid input. Transaction ID cannot be blank."
            );
        }

        String formattedID = transactionID.trim();

        for (int i = 0; i < transactionHistory.size(); i++) {

            Transaction transaction = transactionHistory.get(i);

            if (transaction.getTransactionID()
                    .equalsIgnoreCase(formattedID)) {

                return transaction;
            }
        }

        return null;
    }


    // Filters transactions using a field and value
    public LinkedList<Transaction> filterTransactionHistory(
            String field, String value) {

        if (!InputValidator.isNotBlank(field)) {
            throw new IllegalArgumentException(
                "Invalid input. Filter field cannot be blank."
            );
        }

        if (!InputValidator.isNotBlank(value)) {
            throw new IllegalArgumentException(
                "Invalid input. Filter value cannot be blank."
            );
        }

        String formattedField = field.trim().toLowerCase(Locale.ROOT);
        String formattedValue = value.trim();

        // Validate supported filter fields
        if (!formattedField.equals("status") &&
            !formattedField.equals("bookingtype") &&
            !formattedField.equals("customerid") &&
            !formattedField.equals("cashierid")) {

            throw new IllegalArgumentException(
                "Invalid input. Filter field must be status, bookingType, customerID, or cashierID."
            );
        }

        LinkedList<Transaction> filtered =
            new LinkedList<Transaction>();

        for (int i = 0; i < transactionHistory.size(); i++) {

            Transaction transaction = transactionHistory.get(i);

            boolean matched = false;

            if (formattedField.equals("status")) {

                matched = transaction.getStatus()
                    .equalsIgnoreCase(formattedValue);

            } else if (formattedField.equals("bookingtype")) {

                matched = transaction.getBookingType()
                    .equalsIgnoreCase(formattedValue);

            } else if (formattedField.equals("customerid")) {

                matched = transaction.getCustomerID() != null
                    && transaction.getCustomerID()
                        .equalsIgnoreCase(formattedValue);

            } else if (formattedField.equals("cashierid")) {

                matched = transaction.getCashierID() != null
                    && transaction.getCashierID()
                        .equalsIgnoreCase(formattedValue);
            }

            if (matched) {
                filtered.insertAtEnd(transaction);
            }
        }

        return filtered;
    }


    // Retrieves transactions belonging to a cashier
   
    public LinkedList<Transaction> getByCashier(String cashierID) {

        return filterTransactionHistory("cashierID", cashierID);
    }

    // Retrieves transactions belonging to a customer
     public LinkedList<Transaction> getByCustomer(String customerID) {

        return filterTransactionHistory("customerID", customerID);
    }

     public LinkedList<Transaction> filterByDateRange(
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

        LinkedList<Transaction> filtered =
            new LinkedList<Transaction>();

        for (int i = 0; i < transactionHistory.size(); i++) {

            Transaction transaction = transactionHistory.get(i);

            LocalDate transactionDate =
                transaction.getDateTime().toLocalDate();

            if (!transactionDate.isBefore(startDate)
                    && !transactionDate.isAfter(endDate)) {

                filtered.insertAtEnd(transaction);
            }
        }

        return filtered;
    }



    // Filters by booking type
     public LinkedList<Transaction> filterByBookingType(String bookingType) {

        return filterTransactionHistory("bookingType", bookingType);
    }

    // Filters by transaction status
    public LinkedList<Transaction> filterByStatus(String status) {

        return filterTransactionHistory("status", status);
    }


    // Checks whether a user has transactions
     public boolean hasTransactionsBy(String userID) {

        if (!InputValidator.isNotBlank(userID)) {
            throw new IllegalArgumentException(
                "Invalid input. User ID cannot be blank."
            );
        }

        String formattedID = userID.trim();

        for (int i = 0; i < transactionHistory.size(); i++) {

            Transaction transaction = transactionHistory.get(i);

            if ((transaction.getCustomerID() != null
                    && transaction.getCustomerID()
                        .equalsIgnoreCase(formattedID))
                || (transaction.getCashierID() != null
                    && transaction.getCashierID()
                        .equalsIgnoreCase(formattedID))) {

                return true;
            }
        }

        return false;
    }



    // Generates a transaction ID
    public String generateTransactionID() {

        int highestNumber = 0;

        // Find the highest existing transaction ID number
        for (int i = 0; i < transactionHistory.size(); i++) {

            Transaction transaction = transactionHistory.get(i);

            int currentNumber = Integer.parseInt(
                transaction.getTransactionID().substring(1)
            );

            if (currentNumber > highestNumber) {
                highestNumber = currentNumber;
            }
        }

        if (highestNumber >= 999) {
            throw new IllegalStateException(
                "Operation cannot be completed. Transaction ID limit reached."
            );
        }

        int nextNumber = highestNumber + 1;

        return String.format(
            Locale.ROOT,
            "T%03d",
            nextNumber
        );
    }

}
