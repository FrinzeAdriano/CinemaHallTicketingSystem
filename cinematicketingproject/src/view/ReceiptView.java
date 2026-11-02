package view;

import models.*;
import datastructures.*;
import java.time.format.DateTimeFormatter;

public class ReceiptView extends BaseView {

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

        showMessage("========== RECEIPT ==========");
        showMessage("Receipt No.: " + receipt.getReceiptNo());

        showMessage(
            "Date Issued: " +
            (receipt.getDateTimeIssued() == null
                ? "N/A" : receipt.getDateTimeIssued().format(DATE_FORMAT))
        );

        showMessage("Transaction ID: " + transaction.getTransactionID());
        showMessage("Booking Code: " +
            (transaction.getBookingCode() == null
                ? "N/A" : transaction.getBookingCode()));
        showMessage("Booking Type: " + transaction.getBookingType());

        showMessage("Customer ID: " +
            (transaction.getCustomerID() == null
                ? "N/A" : transaction.getCustomerID()));

        // Displays tickets
        showMessage("\n--- TICKETS ---");

        LinkedList<Ticket> tickets = transaction.getTickets();

        if (tickets.size() == 0) {
            showMessage("No tickets.");
        }

        for (int i = 0; i < tickets.size(); i++) {
            Ticket ticket = tickets.get(i);

            if (ticket == null) {
                continue;
            }

            showMessage("Ticket ID: " + ticket.getTicketID());

            showMessage("Schedule: " +
                (ticket.getSchedule() == null
                    ? "N/A" : ticket.getSchedule().getScheduleID()));

            showMessage("Seat: " +
                (ticket.getSeat() == null
                    ? "N/A" : ticket.getSeat().getSeatID()));

            showMessage("Price: " + ticket.getPrice());
            showMessage("Status: " + ticket.getStatus());
            showMessage("----------------------------------------");
        }

        // Displays products
        showMessage("\n--- PRODUCTS ---");

        LinkedList<OrderProduct> products = transaction.getProducts();

        if (products.size() == 0) {
            showMessage("No products.");
        }

        for (int i = 0; i < products.size(); i++) {
            OrderProduct product = products.get(i);

            if (product == null) {
                continue;
            }

            if (product.getProduct() == null) {
                showMessage("Product information unavailable.");
                continue;
            }

            showMessage(
                product.getProduct().getName()
                + " x " + product.getQuantity()
                + " = " + product.getSubtotal()
            );
        }

        // Displays payment summary
        showMessage("\n--- PAYMENT SUMMARY ---");
        showMessage( "Voucher Code: " +
            (transaction.getVoucherCode() == null
                ? "None" : transaction.getVoucherCode())
        );
        showMessage("Subtotal: " + transaction.getSubtotal());
        showMessage("Discount: " + transaction.getDiscountAmount());
        showMessage("Tax: " + transaction.getTaxAmount());
        showMessage("Total: " + transaction.getTotalAmount());
        showMessage("Amount Paid: " + transaction.getAmountPaid());
        showMessage("Change: " + transaction.getChange());

        showMessage("Payment Method: " +
            (transaction.getPaymentMethod() == null
                ? "N/A" : transaction.getPaymentMethod()));

        showMessage("Status: " + transaction.getStatus());
        showMessage("=============================");
    }

    // Displays a reprinted receipt
    public void displayReprint(Receipt receipt) {

        if (receipt == null) {
            showError("Receipt not found.");
            return;
        }

        showMessage("========== REPRINTED RECEIPT ==========");
        displayReceipt(receipt);
    }
}