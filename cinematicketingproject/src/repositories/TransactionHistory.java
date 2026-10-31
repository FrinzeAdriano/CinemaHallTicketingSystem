package repositories;
import controllers.*;
import models.*;
import datastructures.*;
import java.time.LocalDate;
import view.*;
import utility.*;
import service.*;

public class TransactionHistory {

    private LinkedList  transactionHistory = new LinkedList();


     // Adds a transaction
    public void addTransaction(Transaction transaction) {

        if (transaction != null) {
            transactionHistory.insertAtEnd(transaction);
        }
    }

    // Returns all transactions
    public LinkedList viewTransactionHistory() {
        return transactionHistory;
    }

     // Finds a transaction using its ID
    public Transaction findByID(String transactionID) {

        for (int i = 0; i < transactionHistory.size(); i++) {

            Transaction transaction = (Transaction)transactionHistory.get(i);

            if (transaction.getTransactionID().equalsIgnoreCase(transactionID)) {
                return transaction;
            }
        }

        return null;
    }


    // Filters transactions using a field and value
    public LinkedList filterTransactionHistory(
            String field, String value) {

        LinkedList filtered =  new LinkedList();

        for (int i = 0; i < transactionHistory.size(); i++) {

            Transaction transaction =  (Transaction)transactionHistory.get(i);
            boolean matched = false;

            if (field.equalsIgnoreCase("status")) {
                matched = transaction.getStatus().equalsIgnoreCase(value);
            } else if (field.equalsIgnoreCase("bookingType")) {
                matched = transaction.getBookingType().equalsIgnoreCase(value);
            } else if (field.equalsIgnoreCase("customerID")) {
                matched = transaction.getCustomerID() != null
                    && transaction.getCustomerID().equalsIgnoreCase(value);
            } else if (field.equalsIgnoreCase("cashierID")) {
                matched = transaction.getCashierID() != null
                    && transaction.getCashierID().equalsIgnoreCase(value);
            }

            if (matched) {
                filtered.insertAtEnd(transaction);
            }
        }

        return filtered;
    }


    // Retrieves transactions belonging to a cashier
    public LinkedList getByCashier(String cashierID) {

        return filterTransactionHistory("cashierID", cashierID);
    }

    // Retrieves transactions belonging to a customer
    public LinkedList getByCustomer(String customerID) {

        return filterTransactionHistory("customerID", customerID);
    }

    public LinkedList filterByDateRange(
            LocalDate startDate, LocalDate endDate) {

        LinkedList filtered =
            new LinkedList();

        for (int i = 0; i < transactionHistory.size(); i++) {

            Transaction transaction = (Transaction) transactionHistory.get(i);

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
    public LinkedList filterByBookingType(String bookingType) {

        return filterTransactionHistory("bookingType", bookingType);
    }

    // Filters by transaction status
    public LinkedList filterByStatus(String status) {

        return filterTransactionHistory("status", status);
    }


    // Checks whether a user has transactions
    public boolean hasTransactionsBy(String userID) {

        for (int i = 0; i < transactionHistory.size(); i++) {

            Transaction transaction = (Transaction) transactionHistory.get(i);

            if ((transaction.getCustomerID() != null
                    && transaction.getCustomerID().equalsIgnoreCase(userID))
                || (transaction.getCashierID() != null
                    && transaction.getCashierID().equalsIgnoreCase(userID))) {

                return true;
            }
        }

        return false;
    }


    // Generates a transaction ID
    public String generateTransactionID() {

        int nextNumber = transactionHistory.size() + 1;

        return String.format("TR%04d", nextNumber);
    }
}
