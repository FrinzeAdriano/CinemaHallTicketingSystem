
package service;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import models.*;

public class CheckoutService {

    private TransactionHistory history;
    private SalesRepository salesRepo;
    private InventoryRepository inventoryRepo;
    private VoucherRepository voucherRepo;
    private int receiptCounter;

public CheckoutService(TransactionHistory history, SalesRepository salesRepo, InventoryRepository inventoryRepo,
                       VoucherRepository voucherRepo, int receiptCounter) {
    this.history = history;
    this.salesRepo = salesRepo;
    this.inventoryRepo = inventoryRepo;
    this.voucherRepo = voucherRepo;
    this.receiptCounter = receiptCounter;
    }
    // Completes checkout and creates a receipt
    public Receipt checkout(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction is required.");
        }

        transaction.confirm();

        receiptCounter++;

        String receiptNo = String.format("RCP%04d", receiptCounter);

        transaction.setReceiptNo(receiptNo);

        history.addTransaction(transaction);
        salesRepo.recordSale(transaction);

        return new Receipt(
            receiptNo,
            LocalDateTime.now(),
            transaction
        );
    }


    // Cancels a transaction
    public boolean cancelTransaction(Transaction transaction) {

        if (transaction == null) {
            return false;
        }

        if (transaction.getStatus().equalsIgnoreCase("CANCELLED")) {
            return false;
        }

        boolean wasCompleted =
            transaction.getStatus().equalsIgnoreCase("COMPLETED");


        // Releases seats reserved by this transaction
        LinkedList tickets = transaction.getTickets();

        for (int i = 0; i < tickets.size(); i++) {

            Ticket ticket = (Ticket) tickets.get(i);

            ticket.getSchedule().getBookedSeatIDs().remove(
                ticket.getSeat().getSeatID()
            );

            ticket.setStatus("CANCELLED");
        }


        // Restores product stock if the transaction was completed
        if (wasCompleted) {

            LinkedList<OrderProduct> products =
                transaction.getProducts();

            for (int i = 0; i < products.size(); i++) {

                OrderProduct orderProduct = products.get(i);

                orderProduct.getProduct().addStock(
                    orderProduct.getQuantity()
                );
            }

            salesRepo.reverseSale(transaction);
        }

        transaction.cancel();

        return true;
    }

}
