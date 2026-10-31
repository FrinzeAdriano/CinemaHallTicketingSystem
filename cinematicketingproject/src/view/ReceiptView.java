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

        showMessage("\n--- TICKETS ---");

        LinkedList tickets = transaction.getTickets();

        for (int i = 0; i < tickets.size(); i++) {

            Ticket ticket = (Ticket) tickets.get(i);

            showMessage(
                ticket.getSeat().getSeatID()
                + " - " + ticket.getPrice()
            );
        }

        showMessage("\n--- PRODUCTS ---");

        LinkedList products = transaction.getProducts();

        for (int i = 0; i < products.size(); i++) {

            OrderProduct product = (OrderProduct)products.get(i);

            showMessage(
                product.getProduct().getName()
                + " x " + product.getQuantity()
                + " = " + product.getSubtotal()
            );
        }

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

        showMessage("========== REPRINTED RECEIPT ==========");

        displayReceipt(receipt);
    }
}
