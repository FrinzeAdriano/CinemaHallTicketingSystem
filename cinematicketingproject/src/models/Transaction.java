
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
    private LinkedList<Ticket> tickets;
    private LinkedList<OrderProduct> products;
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

// Constructor

    public Transaction(String transactionID, String bookingType, String customerID, String cashierID,
                       LocalDateTime dateTime, LinkedList<Ticket> tickets, LinkedList<OrderProduct> products,
                       String discountType, double discountRate, double discountFixed, String voucherCode,
                       double taxRate, String paymentMethod, double amountPaid, String status,
                       String bookingCode, String approvedBy, String receiptNo) {

        // Transaction ID validation
        if (transactionID == null ||
            !transactionID.trim().matches("(?i)T\\d{3}") ||
            transactionID.trim().equalsIgnoreCase("T000")) {

            throw new IllegalArgumentException(
                "Invalid input. Transaction ID must follow the format T001 to T999."
            );
        }

        // Booking type validation
        if (!InputValidator.isNotBlank(bookingType) ||
            !(bookingType.trim().equalsIgnoreCase("WALK_IN") ||
              bookingType.trim().equalsIgnoreCase("ONLINE"))) {

            throw new IllegalArgumentException(
                "Invalid input. Booking type must be WALK_IN or ONLINE."
            );
        }

        // Date and collection validation
        if (dateTime == null) {
            throw new IllegalArgumentException(
                "Invalid input. Transaction date and time cannot be null."
            );
        }

        if (dateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                "Invalid input. Transaction date and time cannot be in the future."
            );
        }

        if (tickets == null || products == null) {
            throw new IllegalArgumentException(
                "Invalid input. Ticket and product collections cannot be null."
            );
        }

        // Discount rate validation
        if (!Double.isFinite(discountRate) ||
            discountRate < 0 || discountRate > 1) {

            throw new IllegalArgumentException(
                "Invalid input. Discount rate must be between 0 and 1."
            );
        }

        // Fixed discount validation
        if (!Double.isFinite(discountFixed) || discountFixed < 0) {
            throw new IllegalArgumentException(
                "Invalid input. Fixed discount cannot be negative."
            );
        }

        // Tax rate validation
        if (!Double.isFinite(taxRate) || taxRate < 0 || taxRate > 1) {
            throw new IllegalArgumentException(
                "Invalid input. Tax rate must be between 0 and 1."
            );
        }

        // Amount paid validation
        if (!Double.isFinite(amountPaid) || amountPaid < 0) {
            throw new IllegalArgumentException(
                "Invalid input. Amount paid cannot be negative."
            );
        }

        // Status validation
        if (!InputValidator.isNotBlank(status) ||
            !(status.trim().equalsIgnoreCase("PENDING") ||
              status.trim().equalsIgnoreCase("COMPLETED") ||
              status.trim().equalsIgnoreCase("CANCELLED"))) {

            throw new IllegalArgumentException(
                "Invalid input. Transaction status must be PENDING, COMPLETED, or CANCELLED."
            );
        }

        // Optional string validation
        if (discountType != null && !InputValidator.isNotBlank(discountType)) {
            throw new IllegalArgumentException(
                "Invalid input. Discount type cannot be blank."
            );
        }

        if (voucherCode != null && !InputValidator.isNotBlank(voucherCode)) {
            throw new IllegalArgumentException(
                "Invalid input. Voucher code cannot be blank."
            );
        }

        if (paymentMethod != null && !InputValidator.isNotBlank(paymentMethod)) {
            throw new IllegalArgumentException(
                "Invalid input. Payment method cannot be blank."
            );
        }

        if (approvedBy != null && !InputValidator.isNotBlank(approvedBy)) {
            throw new IllegalArgumentException(
                "Invalid input. Approved by cannot be blank."
            );
        }

        if (receiptNo != null && !InputValidator.isNotBlank(receiptNo)) {
            throw new IllegalArgumentException(
                "Invalid input. Receipt number cannot be blank."
            );
        }

        if (bookingCode != null && !InputValidator.isNotBlank(bookingCode)) {
            throw new IllegalArgumentException(
                "Invalid input. Booking code cannot be blank."
            );
        }

        if (customerID != null && !InputValidator.isNotBlank(customerID)) {
            throw new IllegalArgumentException(
                "Invalid input. Customer ID cannot be blank."
            );
        }

        if (cashierID != null && !InputValidator.isNotBlank(cashierID)) {
            throw new IllegalArgumentException(
                "Invalid input. Cashier ID cannot be blank."
            );
        }

        // Assign validated attributes
        this.transactionID = transactionID.trim().toUpperCase();
        this.bookingType = bookingType.trim().toUpperCase();
        this.customerID = customerID == null ? null : customerID.trim();
        this.cashierID = cashierID == null ? null : cashierID.trim();
        this.dateTime = dateTime;

        // Copy collections to prevent direct external modification
        this.tickets = new LinkedList<Ticket>();
        this.products = new LinkedList<OrderProduct>();

        for (int i = 0; i < tickets.size(); i++) {

            Ticket ticket = tickets.get(i);

            if (ticket == null) {
                throw new IllegalArgumentException(
                    "Invalid input. Ticket collection cannot contain null values."
                );
            }

            this.tickets.insertAtEnd(ticket);
        }

        for (int i = 0; i < products.size(); i++) {

            OrderProduct product = products.get(i);

            if (product == null) {
                throw new IllegalArgumentException(
                    "Invalid input. Product collection cannot contain null values."
                );
            }

            this.products.insertAtEnd(product);
        }

        this.discountType = discountType == null ? null : discountType.trim();
        this.discountRate = discountRate;
        this.discountFixed = discountFixed;
        this.voucherCode = voucherCode == null ? null : voucherCode.trim().toUpperCase();
        this.taxRate = taxRate;
        this.paymentMethod = paymentMethod == null ? null : paymentMethod.trim();
        this.amountPaid = amountPaid;
        this.status = status.trim().toUpperCase();
        this.bookingCode = bookingCode == null ? null : bookingCode.trim().toUpperCase();
        this.approvedBy = approvedBy == null ? null : approvedBy.trim();
        this.receiptNo = receiptNo == null ? null : receiptNo.trim().toUpperCase();
    }


    // Ticket Methods

    public void addTicket(Ticket t) {

        if (t == null) {
            throw new IllegalArgumentException(
                "Invalid input. Ticket cannot be null."
            );
        }

        if (!status.equals("PENDING")) {
            throw new IllegalStateException(
                "Tickets can only be added to pending transactions."
            );
        }

        if (!t.getStatus().equalsIgnoreCase("BOOKED")) {
            throw new IllegalArgumentException(
                "Invalid input. Only booked tickets can be added."
            );
        }

        for (int i = 0; i < tickets.size(); i++) {

            Ticket existingTicket = tickets.get(i);

            if (existingTicket.getTicketID().equalsIgnoreCase(t.getTicketID())) {
                throw new IllegalArgumentException(
                    "Invalid input. Ticket already exists in this transaction."
                );
            }

            if (existingTicket.getSchedule().getScheduleID()
                    .equalsIgnoreCase(t.getSchedule().getScheduleID()) &&
                existingTicket.getSeat().getSeatID()
                    .equalsIgnoreCase(t.getSeat().getSeatID())) {

                throw new IllegalArgumentException(
                    "Invalid input. The same seat and schedule cannot be added twice."
                );
            }
        }

        tickets.insertAtEnd(t);

        // Check for a valid total
        getSubtotal();
    }




     public boolean removeTicket(String ticketID) {

        if (!InputValidator.isNotBlank(ticketID)) {
            throw new IllegalArgumentException(
                "Invalid input. Ticket ID cannot be blank."
            );
        }

        if (!status.equals("PENDING")) {
            throw new IllegalStateException(
                "Tickets can only be removed from pending transactions."
            );
        }

        String formattedID = ticketID.trim();

        for (int i = 0; i < tickets.size(); i++) {

            Ticket ticket = tickets.get(i);

            if (ticket.getTicketID().equalsIgnoreCase(formattedID)) {
                tickets.delete(i);
                return true;
            }
        }

        return false;
    }


      // Product Methods

    public void addProduct(Product p, int qty) {

        if (p == null) {
            throw new IllegalArgumentException(
                "Invalid input. Product cannot be null."
            );
        }

        if (!InputValidator.isPositiveInt(String.valueOf(qty))) {
            throw new IllegalArgumentException(
                "Invalid input. Product quantity must be greater than zero."
            );
        }

        if (!status.equals("PENDING")) {
            throw new IllegalStateException(
                "Products can only be added to pending transactions."
            );
        }

        // Check the existing quantity of this product
        long currentQuantity = 0;

        for (int i = 0; i < products.size(); i++) {

            OrderProduct orderProduct = products.get(i);

            if (orderProduct.getProduct().getProductID()
                    .equalsIgnoreCase(p.getProductID())) {

                currentQuantity += orderProduct.getQuantity();
            }
        }

        // Prevent adding more products than available stock
        if (currentQuantity + qty > p.getStockQuantity()) {
            throw new IllegalStateException(
                "Operation cannot be completed. Insufficient product stock."
            );
        }

        OrderProduct orderProduct = new OrderProduct(p, qty, p.getPrice());

        products.insertAtEnd(orderProduct);

        // Check for a valid total
        getSubtotal();
    }

     public boolean removeProduct(String productID) {

        if (!InputValidator.isNotBlank(productID)) {
            throw new IllegalArgumentException(
                "Invalid input. Product ID cannot be blank."
            );
        }

        if (!status.equals("PENDING")) {
            throw new IllegalStateException(
                "Products can only be removed from pending transactions."
            );
        }

        String formattedID = productID.trim();

        for (int i = 0; i < products.size(); i++) {

            OrderProduct orderProduct = products.get(i);

            if (orderProduct.getProduct().getProductID()
                    .equalsIgnoreCase(formattedID)) {

                products.delete(i);
                return true;
            }
        }

        return false;
    }



        // Computation Methods

    public double getSubtotal() {

        double subtotal = 0;

        // Add ticket prices
        for (int i = 0; i < tickets.size(); i++) {

            Ticket ticket = tickets.get(i);

            subtotal += ticket.getPrice();

            if (!Double.isFinite(subtotal)) {
                throw new IllegalStateException(
                    "Operation cannot be completed. Transaction subtotal is invalid."
                );
            }
        }

        // Add product subtotals
        for (int i = 0; i < products.size(); i++) {

            OrderProduct product = products.get(i);

            subtotal += product.getSubtotal();

            if (!Double.isFinite(subtotal)) {
                throw new IllegalStateException(
                    "Operation cannot be completed. Transaction subtotal is invalid."
                );
            }
        }

        return subtotal;
    }



   // Calculates the discount

   public double getDiscountAmount() {

        double subtotal = getSubtotal();
        double discount;

        // Apply percentage discount when a discount rate is provided
        if (discountRate > 0) {
            discount = subtotal * discountRate;
        } else {
            discount = discountFixed;
        }

        // Discount cannot exceed the subtotal
        if (discount > subtotal) {
            discount = subtotal;
        }

        if (!Double.isFinite(discount)) {
            throw new IllegalStateException(
                "Operation cannot be completed. Discount amount is invalid."
            );
        }

        return discount;
    }




     // Calculates the tax

     public double getTaxAmount() {

        double taxableAmount = getSubtotal() - getDiscountAmount();
        double taxAmount = taxableAmount * taxRate;

        if (!Double.isFinite(taxAmount)) {
            throw new IllegalStateException(
                "Operation cannot be completed. Tax amount is invalid."
            );
        }

        return taxAmount;
    }




    // Calculates the final amount

       public double getTotalAmount() {

        double total = getSubtotal() - getDiscountAmount() + getTaxAmount();

        if (!Double.isFinite(total) || total < 0) {
            throw new IllegalStateException(
                "Operation cannot be completed. Transaction total is invalid."
            );
        }

        return total;
    }


    // Calculates the change

    public double getChange() {

        double change = amountPaid - getTotalAmount();

        if (!Double.isFinite(change)) {
            throw new IllegalStateException(
                "Change calculation exceeds the supported numeric range."
            );
        }

        if (change < 0) {
            return 0;
        }

        return change;
    }



      // Confirms the transaction

    public boolean confirm() {

        if (!status.equals("PENDING")) {
            throw new IllegalStateException(
                "Only pending transactions can be confirmed."
            );
        }

        // Check required booking information
        if (bookingType.equals("WALK_IN") &&
            !InputValidator.isNotBlank(cashierID)) {

            throw new IllegalStateException(
                "Operation cannot be completed. Cashier ID is required for walk-in transactions."
            );
        }

        if (bookingType.equals("ONLINE") &&
            !InputValidator.isNotBlank(customerID)) {

            throw new IllegalStateException(
                "Operation cannot be completed. Customer ID is required for online transactions."
            );
        }

        // Ensure the transaction has at least one item
        if (tickets.size() == 0 && products.size() == 0) {
            throw new IllegalStateException(
                "Operation cannot be completed. Transaction must contain at least one item."
            );
        }

        if (!InputValidator.isNotBlank(paymentMethod)) {
            throw new IllegalStateException(
                "Operation cannot be completed. Payment method is required."
            );
        }

        if (amountPaid < getTotalAmount()) {
            throw new IllegalStateException(
                "Operation cannot be completed. Amount paid is insufficient."
            );
        }

        status = "COMPLETED";

        return true;
    }



      // Cancels the transaction

    public boolean cancel() {

    if (status.equals("CANCELLED")) {
        return false;
    }

    // Check all tickets before making changes
    for (int i = 0; i < tickets.size(); i++) {

        Ticket ticket = tickets.get(i);

        if (ticket.getStatus().equalsIgnoreCase("USED")) {
            throw new IllegalStateException(
                "Operation cannot be completed. Used tickets cannot be cancelled."
            );
        }
    }

    // Cancel tickets and release their seats
    for (int i = 0; i < tickets.size(); i++) {

        Ticket ticket = tickets.get(i);

        if (ticket.getStatus().equalsIgnoreCase("BOOKED")) {

            Schedule schedule = ticket.getSchedule();

            String seatID = ticket.getSeat().getSeatID();

            LinkedList<String> bookedSeatIDs =
                schedule.getBookedSeatIDs();

            // Remove the seat ID from this schedule's booked seats
            for (int j = 0; j < bookedSeatIDs.size(); j++) {

                if (bookedSeatIDs.get(j).equalsIgnoreCase(seatID)) {
                    bookedSeatIDs.delete(j);
                    break;
                }
            }

            // Update ticket status
            ticket.setStatus("CANCELLED");
        }
    }

    // Update transaction status
    status = "CANCELLED";

    return true;
}


    // Setters
    public void setDiscountType(String discountType) {

        if (discountType != null && !InputValidator.isNotBlank(discountType)) {
            throw new IllegalArgumentException(
                "Invalid input. Discount type cannot be blank."
            );
        }

        this.discountType = discountType == null ? null : discountType.trim();
    }


     public void setDiscountRate(double discountRate) {

        if (!Double.isFinite(discountRate) ||
            discountRate < 0 || discountRate > 1) {

            throw new IllegalArgumentException(
                "Invalid input. Discount rate must be between 0 and 1."
            );
        }

        this.discountRate = discountRate;
    }

     public void setDiscountFixed(double discountFixed) {

        if (!Double.isFinite(discountFixed) || discountFixed < 0) {
            throw new IllegalArgumentException(
                "Invalid input. Fixed discount cannot be negative."
            );
        }

        this.discountFixed = discountFixed;
    }

    public void setVoucherCode(String voucherCode) {

        if (voucherCode != null && !InputValidator.isNotBlank(voucherCode)) {
            throw new IllegalArgumentException(
                "Invalid input. Voucher code cannot be blank."
            );
        }

        this.voucherCode = voucherCode == null ? null : voucherCode.trim().toUpperCase();
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

        if (!Double.isFinite(amountPaid) || amountPaid < 0) {
            throw new IllegalArgumentException(
                "Invalid input. Amount paid cannot be negative."
            );
        }

        this.amountPaid = amountPaid;
    }


    public void setApprovedBy(String approvedBy) {

        if (approvedBy != null && !InputValidator.isNotBlank(approvedBy)) {
            throw new IllegalArgumentException(
                "Invalid input. Approved by cannot be blank."
            );
        }

        this.approvedBy = approvedBy == null ? null : approvedBy.trim();
    }

     public void setReceiptNo(String receiptNo) {

        if (receiptNo != null && !InputValidator.isNotBlank(receiptNo)) {
            throw new IllegalArgumentException(
                "Invalid input. Receipt number cannot be blank."
            );
        }

        this.receiptNo = receiptNo == null ? null : receiptNo.trim().toUpperCase();
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

        LinkedList<Ticket> copy = new LinkedList<Ticket>();

        for (int i = 0; i < tickets.size(); i++) {
            copy.insertAtEnd(tickets.get(i));
        }

        return copy;
    }

    public LinkedList<OrderProduct> getProducts() {

        LinkedList<OrderProduct> copy = new LinkedList<OrderProduct>();

        for (int i = 0; i < products.size(); i++) {
            copy.insertAtEnd(products.get(i));
        }

        return copy;
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
