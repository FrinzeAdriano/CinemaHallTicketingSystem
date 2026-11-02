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

        showMessage("================ RECEIPT =================");
        showMessage("Receipt No.: " + receipt.getReceiptNo());
        showMessage("Date Issued: " + (receipt.getDateTimeIssued() == null ? "N/A" : receipt.getDateTimeIssued().format(DATE_FORMAT)));
        showMessage("Transaction ID: " + transaction.getTransactionID());
        showMessage("Booking Code: " + (transaction.getBookingCode() == null ? "N/A" : transaction.getBookingCode()));
        showMessage("Booking Type: " + transaction.getBookingType());
        showMessage("Customer ID: " + (transaction.getCustomerID() == null ? "N/A" : transaction.getCustomerID()));

        // Displays tickets
        showMessage("\n---------------- TICKETS -----------------");
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
        showMessage("\n---------------- PRODUCTS ----------------");
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
        showMessage("\n------------ PAYMENT SUMMARY -------------");
        showMessage("Voucher Code: " + (transaction.getVoucherCode() == null ? "None" : transaction.getVoucherCode()));
        showMessage("Subtotal: " + String.format("%.2f", transaction.getSubtotal()));
        showMessage("Discount: " + String.format("%.2f", transaction.getDiscountAmount()));
        showMessage("Cultural Tax: " + String.format("%.2f", transaction.getCulturalTax()));
        showMessage("Amusement Tax: " + String.format("%.2f", transaction.getAmusementTax()));
        showMessage("VAT: " + String.format("%.2f", transaction.getVat()));
        showMessage("Total Tax: " + String.format("%.2f", transaction.getTaxAmount()));
        showMessage("Total: " + String.format("%.2f", transaction.getTotalAmount()));
        showMessage("Amount Paid: " + String.format("%.2f", transaction.getAmountPaid()));
        showMessage("Change: " + String.format("%.2f", transaction.getChange()));
        showMessage("Payment Method: " + (transaction.getPaymentMethod() == null ? "N/A"
        : transaction.getPaymentMethod()
        + (transaction.getPaymentProvider() == null ? "" : " (" + transaction.getPaymentProvider() + ")")));
        showMessage("Status: " + transaction.getStatus());
        showMessage("==========================================");
    }

    // Displays a reprinted receipt
    public void displayReprint(Receipt receipt) {
        if (receipt == null) {
            showError("Receipt not found.");
            return;
        }

        showMessage("=========== REPRINTED RECEIPT ============");
        displayReceipt(receipt);
    }
}