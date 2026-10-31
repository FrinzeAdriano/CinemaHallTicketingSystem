
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


    // Transaction Methods

    public void addTransaction(Transaction transaction) {

    }

    public void removeTransaction(Transaction transaction) {

    }


    // Computation Method

    public double getTotalRevenue() {
         return 0;}


    // Getters

    public String getSalesID() {
         return salesID;}

    public LocalDate getDate() {
         return date;}

    public double getTicketRevenue() {
         return ticketRevenue;}

    public double getProductRevenue() {
         return productRevenue;}

    public double getDiscountTotal() {
         return discountTotal;}

    public int getTransactionCount() {
         return transactionCount;}

}