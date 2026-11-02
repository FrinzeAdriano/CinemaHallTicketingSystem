package view;

import models.*;
import datastructures.*;
import java.time.format.DateTimeFormatter;

public class ReceiptView extends BaseView {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Displays a receipt
    public void displayReceipt(Receipt receipt) {
        if (receipt == null) {
            showError("Receipt not found.");
            return;
        }

        Transaction transaction = receipt.getTransaction();

        if (transaction == null) {
            showError("Receipt has no transaction.");
            return;
        }

        showMessage("---------------- RECEIPT ----------------");
        showMessage("Receipt No.: " + receipt.getReceiptNo());
        showMessage("Date Issued: " + (receipt.getDateTimeIssued() == null ? "N/A" : receipt.getDateTimeIssued().format(DATE_FORMAT)));
        showMessage("Transaction ID: " + transaction.getTransactionID());
        showMessage("Booking Code: " + (transaction.getBookingCode() == null ? "N/A" : transaction.getBookingCode()));
        showMessage("Booking Type: " + transaction.getBookingType());
        showMessage("Customer ID: " + (transaction.getCustomerID() == null ? "N/A" : transaction.getCustomerID()));

        // Displays tickets
        showMessage("---------------- TICKETS ----------------");
        LinkedList<Ticket> tickets = transaction.getTickets();

        if (tickets.size() == 0) showMessage("No tickets.");

        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);
            if (ticket == null) continue;

            showMessage("Ticket ID: " + ticket.getTicketID());
            showMessage("Schedule: " + (ticket.getSchedule() == null ? "N/A" : ticket.getSchedule().getScheduleID()));
            showMessage("Seat: " + (ticket.getSeat() == null ? "N/A" : ticket.getSeat().getSeatID()));
            showMessage("Price: " + String.format("%.2f", ticket.getPrice()));
            showMessage("Discount Type: " + (ticket.getDiscountType().equalsIgnoreCase("NONE") ? "None" : ticket.getDiscountType()));
            showMessage("Discount Amount: " + String.format("%.2f", ticket.getDiscountAmount()));
            showMessage("Net Price: " + String.format("%.2f", ticket.getNetPrice()));
            showMessage("Status: " + ticket.getStatus());
            showMessage("------------------------------------------");
        }

        // Displays products
        showMessage("---------------- PRODUCTS ----------------");
        LinkedList<OrderProduct> products = transaction.getProducts();

        if (products.size() == 0) {
            showMessage("No products.");
        } else {
            for (int i = 0; i < products.size(); i++) {
                OrderProduct product = products.get(i);
                if (product == null) continue;

                if (product.getProduct() == null) {
                    showMessage("Product information unavailable.");
                    continue;
                }

                // Aligned format: Name x Qty = Subtotal (Total width: 42 chars)
                String line = String.format("%-24s x %-2d = %10.2f", 
                    product.getProduct().getName(), 
                    product.getQuantity(), 
                    product.getSubtotal());
                showMessage(line);
            }
        }

        // Displays payment summary
        showMessage("------------ PAYMENT SUMMARY -------------");
        showMessage(String.format("%-26s %15s", "Voucher Code:", (transaction.getVoucherCode() == null ? "None" : transaction.getVoucherCode())));
        showMessage(String.format("%-26s %15.2f", "Subtotal:", transaction.getSubtotal()));
        showMessage(String.format("%-26s %15.2f", "Discount:", transaction.getDiscountAmount()));
        showMessage(String.format("%-26s %15.2f", "Cultural Tax:", transaction.getCulturalTax()));
        showMessage(String.format("%-26s %15.2f", "Amusement Tax:", transaction.getAmusementTax()));
        showMessage(String.format("%-26s %15.2f", "VAT:", transaction.getVat()));
        showMessage(String.format("%-26s %15.2f", "Total Tax:", transaction.getTaxAmount()));
        showMessage(String.format("%-26s %15.2f", "Total:", transaction.getTotalAmount()));
        showMessage(String.format("%-26s %15.2f", "Amount Paid:", transaction.getAmountPaid()));
        showMessage(String.format("%-26s %15.2f", "Change:", transaction.getChange()));
        showMessage(String.format("%-26s %15s", "Payment Method:", (transaction.getPaymentMethod() == null ? "N/A" : transaction.getPaymentMethod())));
        showMessage(String.format("%-26s %15s", "Status:", transaction.getStatus()));
        showMessage("------------------------------------------");
    }

    // Displays a reprinted receipt
    public void displayReprint(Receipt receipt) {
        if (receipt == null) {
            showError("Receipt not found.");
            return;
        }

        showMessage("--------- REPRINTED RECEIPT ----------");
        displayReceipt(receipt);
    }
}