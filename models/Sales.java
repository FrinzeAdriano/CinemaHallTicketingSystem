
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import java.time.LocalDate;
import utility.*;
import service.*;

public class Sales {

    private final String salesID;
    private LocalDate date;
    private double ticketRevenue;
    private double productRevenue;
    private double discountTotal;
    private int transactionCount;


    // Constructor

    public Sales(String salesID, LocalDate date, double ticketRevenue,
                 double productRevenue, double discountTotal,
                 int transactionCount) {

        // Sales ID validation
        if (salesID == null ||
            !salesID.trim().matches("(?i)S\\d{3}") ||
            salesID.trim().equalsIgnoreCase("S000")) {

            throw new IllegalArgumentException(
                "Invalid input. Sales ID must follow the format S001 to S999."
            );
        }

        // Date validation
        if (date == null) {
            throw new IllegalArgumentException(
                "Invalid input. Sales date cannot be null."
            );
        }

        // Ticket revenue validation
        if (!Double.isFinite(ticketRevenue) || ticketRevenue < 0) {
            throw new IllegalArgumentException(
                "Invalid input. Ticket revenue cannot be negative or invalid."
            );
        }

        // Product revenue validation
        if (!Double.isFinite(productRevenue) || productRevenue < 0) {
            throw new IllegalArgumentException(
                "Invalid input. Product revenue cannot be negative or invalid."
            );
        }

        // Discount validation
        if (!Double.isFinite(discountTotal) || discountTotal < 0) {
            throw new IllegalArgumentException(
                "Invalid input. Discount total cannot be negative or invalid."
            );
        }

        // Transaction count validation
        if (transactionCount < 0) {
            throw new IllegalArgumentException(
                "Invalid input. Transaction count cannot be negative."
            );
        }

        // Total revenue validation
        double grossRevenue = ticketRevenue + productRevenue;

        if (!Double.isFinite(grossRevenue) ||
            discountTotal > grossRevenue) {

            throw new IllegalArgumentException(
                "Invalid input. Discount total cannot exceed gross revenue."
            );
        }

        this.salesID = salesID.trim().toUpperCase();
        this.date = date;
        this.ticketRevenue = ticketRevenue;
        this.productRevenue = productRevenue;
        this.discountTotal = discountTotal;
        this.transactionCount = transactionCount;
    }



    // Adds a completed transaction to the sales record
    // Adds a completed transaction to the sales record

    public void addTransaction(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                "Invalid input. Transaction cannot be null."
            );
        }

        if (!transaction.getStatus().equalsIgnoreCase("COMPLETED")) {
            throw new IllegalStateException(
                "Only completed transactions can be added to sales."
            );
        }

        if (transaction.getDateTime() == null ||
            !transaction.getDateTime().toLocalDate().equals(date)) {

            throw new IllegalArgumentException(
                "Invalid input. Transaction date does not match the sales date."
            );
        }

        // Calculate ticket revenue
        double ticketTotal = 0;

        LinkedList<Ticket> transactionTickets =
            transaction.getTickets();

        for (int i = 0; i < transactionTickets.size(); i++) {

            ticketTotal += transactionTickets.get(i).getPrice();
        }

        // Calculate product revenue
        double productTotal = 0;

        LinkedList<OrderProduct> transactionProducts =
            transaction.getProducts();

        for (int i = 0; i < transactionProducts.size(); i++) {

            productTotal += transactionProducts.get(i).getSubtotal();
        }

        // Get transaction discount
        double transactionDiscount =
            transaction.getDiscountAmount();

        // Calculate updated sales values before changing the record
        double updatedTicketRevenue = ticketRevenue + ticketTotal;
        double updatedProductRevenue = productRevenue + productTotal;
        double updatedDiscountTotal = discountTotal + transactionDiscount;

        // Validate calculated values
        if (!Double.isFinite(ticketTotal) ||
            !Double.isFinite(productTotal) ||
            !Double.isFinite(transactionDiscount) ||
            !Double.isFinite(updatedTicketRevenue) ||
            !Double.isFinite(updatedProductRevenue) ||
            !Double.isFinite(updatedDiscountTotal)) {

            throw new IllegalStateException(
                "Operation cannot be completed. Sales calculation is invalid."
            );
        }

        if (updatedDiscountTotal >
            updatedTicketRevenue + updatedProductRevenue) {

            throw new IllegalStateException(
                "Operation cannot be completed. Discount exceeds gross revenue."
            );
        }

        if (transactionCount == Integer.MAX_VALUE) {
            throw new IllegalStateException(
                "Operation cannot be completed. Transaction count limit reached."
            );
        }

        // Update sales record
        ticketRevenue = updatedTicketRevenue;
        productRevenue = updatedProductRevenue;
        discountTotal = updatedDiscountTotal;
        transactionCount++;
    }


    // Removes a completed transaction from the sales record
 public void removeTransaction(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                "Invalid input. Transaction cannot be null."
            );
        }

        if (transaction.getDateTime() == null ||
            !transaction.getDateTime().toLocalDate().equals(date)) {

            throw new IllegalArgumentException(
                "Invalid input. Transaction date does not match the sales date."
            );
        }

        // Calculate ticket revenue to remove
        double ticketTotal = 0;

        LinkedList<Ticket> transactionTickets =
            transaction.getTickets();

        for (int i = 0; i < transactionTickets.size(); i++) {

            ticketTotal += transactionTickets.get(i).getPrice();
        }

        // Calculate product revenue to remove
        double productTotal = 0;

        LinkedList<OrderProduct> transactionProducts =
            transaction.getProducts();

        for (int i = 0; i < transactionProducts.size(); i++) {

            productTotal += transactionProducts.get(i).getSubtotal();
        }

        // Get transaction discount
        double transactionDiscount =
            transaction.getDiscountAmount();

        // Check whether the sales record can accommodate the removal
        if (transactionCount <= 0 ||
            !Double.isFinite(ticketTotal) ||
            !Double.isFinite(productTotal) ||
            !Double.isFinite(transactionDiscount) ||
            ticketTotal > ticketRevenue ||
            productTotal > productRevenue ||
            transactionDiscount > discountTotal) {

            throw new IllegalStateException(
                "Operation cannot be completed. Transaction cannot be removed from this sales record."
            );
        }

        // Update sales record
        ticketRevenue = Math.max(0, ticketRevenue - ticketTotal);
        productRevenue = Math.max(0, productRevenue - productTotal);
        discountTotal = Math.max(0, discountTotal - transactionDiscount);
        transactionCount--;
    }



    // Calculates total revenue
     public double getTotalRevenue() {

        double totalRevenue =
            ticketRevenue + productRevenue - discountTotal;

        if (!Double.isFinite(totalRevenue) || totalRevenue < 0) {
            throw new IllegalStateException(
                "Operation cannot be completed. Total revenue is invalid."
            );
        }

        return totalRevenue;
    }



    // Getters

    public String getSalesID() {
        return salesID;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getTicketRevenue() {
        return ticketRevenue;
    }

    public double getProductRevenue() {
        return productRevenue;
    }

    public double getDiscountTotal() {
        return discountTotal;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

}