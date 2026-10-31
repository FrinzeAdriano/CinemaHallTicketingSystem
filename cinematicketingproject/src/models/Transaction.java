
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


    // Adds a ticket
    public void addTicket(Ticket ticket) {
        tickets.insertAtEnd(ticket);
    }


    // Removes a ticket using its ID
    public void removeTicket(String ticketID) {

        int i = 0;

        while (i < tickets.size()) {

            Ticket ticket = (Ticket) tickets.get(i);

            if (ticket.getTicketID().equals(ticketID)) {
                tickets.delete(i);
                return;
            }

            i++;
        }
    }


    // Adds a product to the transaction
    public void addProduct(Product product, int quantity) {

        OrderProduct orderProduct =
            new OrderProduct(product, quantity, product.getPrice());

        products.insertAtEnd(orderProduct);
    }


    // Removes a product using its name
    public void removeProduct(String productName) {

        int i = 0;

        while (i < products.size()) {

            OrderProduct orderProduct = (OrderProduct) products.get(i);

            if (orderProduct.getProduct().getName().equalsIgnoreCase(productName)) {
                products.delete(i);
                return;
            }

            i++;
        }
    }


    // Calculates the subtotal
    public double getSubtotal() {

        double subtotal = 0;

        for (int i = 0; i < tickets.size(); i++) {
            subtotal += ((Ticket) tickets.get(i)).getPrice();
        }

        for (int i = 0; i < products.size(); i++) {
            subtotal += ((OrderProduct) products.get(i)).getSubtotal();
        }

        return subtotal;
    }


    // Calculates the discount
    public double getDiscountAmount() {

        double discount = 0;

        if (discountRate > 0) {
            discount = getSubtotal() * discountRate;
        } else {
            discount = discountFixed;
        }

        if (discount > getSubtotal()) {
            discount = getSubtotal();
        }

        return discount;
    }


    // Calculates the tax
    public double getTaxAmount() {

        double taxableAmount = getSubtotal() - getDiscountAmount();

        return taxableAmount * taxRate;
    }


    // Calculates the final amount
    public double getTotalAmount() {

        return getSubtotal()
             - getDiscountAmount()
             + getTaxAmount();
    }


    // Calculates the change
    public double getChange() {

        double change = amountPaid - getTotalAmount();

        if (change < 0) {
            return 0;
        }

        return change;
    }


    // Confirms the transaction
    public void confirm() {

        if (status.equalsIgnoreCase("CANCELLED")) {
            throw new IllegalStateException(
                "Cancelled transaction cannot be confirmed."
            );
        }

        if (status.equalsIgnoreCase("COMPLETED")) {
            throw new IllegalStateException(
                "Transaction is already completed."
            );
        }

        if (getSubtotal() <= 0) {
            throw new IllegalStateException(
                "Transaction has no items."
            );
        }

        if (amountPaid < getTotalAmount()) {
            throw new IllegalStateException(
                "Insufficient payment."
            );
        }

        status = "COMPLETED";
    }


    // Cancels the transaction
    public void cancel() {
        status = "CANCELLED";
    }


    // Setters
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public void setDiscountRate(double discountRate) {

        if (discountRate < 0 || discountRate > 1) {
            throw new IllegalArgumentException(
                "Discount rate must be between 0 and 1."
            );
        }

        this.discountRate = discountRate;
    }

    public void setDiscountFixed(double discountFixed) {
        this.discountFixed = discountFixed;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public void setPaymentMethod(String paymentMethod) {

        if (paymentMethod == null) {
            throw new IllegalArgumentException(
                "Payment method is required."
            );
        }

        if (paymentMethod.equalsIgnoreCase("Cash")) {
            this.paymentMethod = "Cash";
        } else if (paymentMethod.equalsIgnoreCase("E-wallet")) {
            this.paymentMethod = "E-wallet";
        } else if (paymentMethod.equalsIgnoreCase("Card")) {
            this.paymentMethod = "Card";
        } else {
            throw new IllegalArgumentException(
                "Invalid payment method."
            );
        }
    }

    public void setAmountPaid(double amountPaid) {

        if (!InputValidator.isPositiveDouble(String.valueOf(amountPaid))) {
            throw new IllegalArgumentException(
                "Amount paid must be greater than zero."
            );
        }

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
        return transactionID;
    }

    public String getBookingType() {
        return bookingType;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getCashierID() {
        return cashierID;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public LinkedList<Ticket> getTickets() {
        return tickets;
    }

    public LinkedList<OrderProduct> getProducts() {
        return products;
    }

    public String getDiscountType() {
        return discountType;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public double getDiscountFixed() {
        return discountFixed;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public String getStatus() {
        return status;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

}
