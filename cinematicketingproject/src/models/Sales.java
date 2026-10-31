
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

    public Sales(String salesID, LocalDate date, double ticketRevenue, double productRevenue, double discountTotal, int transactionCount) {
        this.salesID = salesID;
        this.date = date;
        this.ticketRevenue = ticketRevenue;
        this.productRevenue = productRevenue;
        this.discountTotal = discountTotal;
        this.transactionCount = transactionCount;
        }


    // Adds a completed transaction to the sales record
    public void addTransaction(Transaction transaction) {

        if (transaction == null) {
            return;
        }

        if (!transaction.getStatus().equalsIgnoreCase("COMPLETED")) {
            return;
        }

        if (!transaction.getDateTime().toLocalDate().equals(date)) {
            return;
        }

        double ticketTotal = 0;
        double productTotal = 0;

        for (int i = 0; i < transaction.getTickets().size(); i++) {
            ticketTotal += transaction.getTickets().get(i).getPrice();
        }

        for (int i = 0; i < transaction.getProducts().size(); i++) {
            productTotal += transaction.getProducts().get(i).getSubtotal();
        }

        ticketRevenue += ticketTotal;
        productRevenue += productTotal;
        discountTotal += transaction.getDiscountAmount();
        transactionCount++;
    }


    // Removes a completed transaction from the sales record
    public void removeTransaction(Transaction transaction) {

        if (transaction == null) {
            return;
        }

        double ticketTotal = 0;
        double productTotal = 0;

        for (int i = 0; i < transaction.getTickets().size(); i++) {
            ticketTotal += transaction.getTickets().get(i).getPrice();
        }

        for (int i = 0; i < transaction.getProducts().size(); i++) {
            productTotal += transaction.getProducts().get(i).getSubtotal();
        }

        ticketRevenue -= ticketTotal;
        productRevenue -= productTotal;
        discountTotal -= transaction.getDiscountAmount();
        transactionCount--;

        if (ticketRevenue < 0) {
            ticketRevenue = 0;
        }

        if (productRevenue < 0) {
            productRevenue = 0;
        }

        if (discountTotal < 0) {
            discountTotal = 0;
        }

        if (transactionCount < 0) {
            transactionCount = 0;
        }
    }


    // Calculates total revenue
    public double getTotalRevenue() {

        return ticketRevenue + productRevenue - discountTotal;
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