
package models;

import java.time.LocalDateTime;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;

public class Transaction {

    private final String transactionID;
    private String bookingType;
    private String customerID;
    private String cashierID;
    private LocalDateTime dateTime;
    private LinkedList tickets = new LinkedList();
    private LinkedList products = new LinkedList();
    private String discountType;
    private double discountRate;
    private double discountFixed;
    private String voucherCode;
    private double taxRate;
    private String paymentMethod;
    private double amountPaid;
    private String status;
    private String bookingCode;
    private String approvedBy;
    private String receiptNo;


    // Constructor

    public Transaction(String transactionID, String bookingType, String customerID, String cashierID,
                       LocalDateTime dateTime, LinkedList tickets, LinkedList products,
                       String discountType, double discountRate, double discountFixed, String voucherCode,
                       double taxRate, String paymentMethod, double amountPaid, String status,
                       String bookingCode, String approvedBy, String receiptNo) {

        this.transactionID = transactionID;
        this.bookingType = bookingType;
        this.customerID = customerID;
        this.cashierID = cashierID;
        this.dateTime = dateTime;
        this.tickets = tickets;
        this.products = products;
        this.discountType = discountType;
        this.discountRate = discountRate;
        this.discountFixed = discountFixed;
        this.voucherCode = voucherCode;
        this.taxRate = taxRate;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.status = status;
        this.bookingCode = bookingCode;
        this.approvedBy = approvedBy;
        this.receiptNo = receiptNo;
        }


    // Ticket Methods

    public void addTicket(Ticket t) {

    }

    public boolean removeTicket(String ticketID) {
         return false;}


    // Product Methods

    public void addProduct(Product p, int qty) {

    }

    public boolean removeProduct(String productID) {
         return false;}


    // Computation Methods

    public double getSubtotal() {
         return 0;}

    public double getDiscountAmount() {
         return 0;}

    public double getTaxAmount() {
         return 0;}

    public double getTotalAmount() {
         return 0;}

    public double getChange() {
         return 0;}


    // Transaction Methods

    public boolean confirm() {
         return false;}

    public boolean cancel() {
         return false;}


    // Setters

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
        }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
        }

    public void setDiscountFixed(double discountFixed) {
        this.discountFixed = discountFixed;
        }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
        }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
        }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
        }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
        }


    // Getters

    public String getTransactionID() {
         return transactionID;}

    public String getBookingType() {
         return bookingType;}

    public String getCustomerID() {
         return customerID;}

    public String getCashierID() {
         return cashierID;}

    public LocalDateTime getDateTime() {
         return dateTime;}

    public LinkedList getTickets() {
         return tickets;}

    public LinkedList getProducts() {
         return products;}

    public String getDiscountType() {
         return discountType;}

    public double getDiscountRate() {
         return discountRate;}

    public double getDiscountFixed() {
         return discountFixed;}

    public String getVoucherCode() {
         return voucherCode;}

    public double getTaxRate() {
         return taxRate;}

    public String getPaymentMethod() {
         return paymentMethod;}

    public double getAmountPaid() {
         return amountPaid;}

    public String getStatus() {
         return status;}

    public String getBookingCode() {
         return bookingCode;}

    public String getApprovedBy() {
         return approvedBy;}

    public String getReceiptNo() {
         return receiptNo;}

}
