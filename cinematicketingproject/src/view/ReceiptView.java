package view;
import controllers.*;
import models.*;
import datastructures.*;
import repositories.*;
import utility.*;
import service.*;

public class ReceiptView extends BaseView {


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
        showMessage("Date Issued: " + receipt.getDateTimeIssued());
        showMessage("Transaction ID: " + transaction.getTransactionID());
        showMessage("Booking Type: " + transaction.getBookingType());


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

            if (ticket.getSeat() == null) {
                showMessage("Ticket seat information unavailable.");
                continue;
            }

            showMessage(
                ticket.getSeat().getSeatID()
                + " - " + ticket.getPrice()
            );
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


        // Displays transaction summary

        showMessage("-----------------------------");
        showMessage("Subtotal: " + transaction.getSubtotal());
        showMessage("Discount: " + transaction.getDiscountAmount());
        showMessage("Tax: " + transaction.getTaxAmount());
        showMessage("Total: " + transaction.getTotalAmount());
        showMessage("Amount Paid: " + transaction.getAmountPaid());
        showMessage("Change: " + transaction.getChange());
        showMessage("Payment Method: " + transaction.getPaymentMethod());
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