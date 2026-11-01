
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

 public CheckoutService(TransactionHistory history,
                           SalesRepository salesRepo,
                           InventoryRepository inventoryRepo,
                           VoucherRepository voucherRepo,
                           int receiptCounter) {

        if (history == null || salesRepo == null ||
            inventoryRepo == null || voucherRepo == null) {

            throw new IllegalArgumentException(
                "Invalid input. Checkout service repositories cannot be null."
            );
        }

        if (receiptCounter < 0) {
            throw new IllegalArgumentException(
                "Invalid input. Receipt counter cannot be negative."
            );
        }

        this.history = history;
        this.salesRepo = salesRepo;
        this.inventoryRepo = inventoryRepo;
        this.voucherRepo = voucherRepo;
        this.receiptCounter = receiptCounter;
    }
 
 
    // Completes checkout and creates a receipt
   public Receipt checkout(Transaction transaction) {

        if (transaction == null) {
            throw new IllegalArgumentException(
                "Invalid input. Transaction cannot be null."
            );
        }

        if (!transaction.getStatus().equalsIgnoreCase("PENDING")) {
            throw new IllegalStateException(
                "Only pending transactions can proceed to checkout."
            );
        }

        if (receiptCounter == Integer.MAX_VALUE) {
            throw new IllegalStateException(
                "Operation cannot be completed. Receipt number limit reached."
            );
        }

        LinkedList<OrderProduct> products =
            transaction.getProducts();


        // Deduct product stock
        for (int i = 0; i < products.size(); i++) {

            OrderProduct orderProduct = products.get(i);

            boolean deducted =
                orderProduct.getProduct().deductStock(
                    orderProduct.getQuantity()
                );

            if (!deducted) {

                // Restore previously deducted stock
                for (int j = 0; j < i; j++) {

                    OrderProduct previousProduct = products.get(j);

                    previousProduct.getProduct().addStock(
                        previousProduct.getQuantity()
                    );
                }

                throw new IllegalStateException(
                    "Operation cannot be completed. Insufficient product stock."
                );
            }
        }


        // Confirm transaction
        try {

            if (!transaction.confirm()) {

                throw new IllegalStateException(
                    "Operation cannot be completed. Transaction confirmation failed."
                );
            }

        } catch (RuntimeException e) {

            // Restore all deducted product stock
            for (int i = 0; i < products.size(); i++) {

                OrderProduct orderProduct = products.get(i);

                orderProduct.getProduct().addStock(
                    orderProduct.getQuantity()
                );
            }

            throw e;
        }


        // Generate receipt number
        int nextReceiptCounter = receiptCounter + 1;

        String receiptNo = String.format(
            Locale.ROOT,
            "RCP%04d",
            nextReceiptCounter
        );

        transaction.setReceiptNo(receiptNo);


        // Save transaction and record sales
        history.addTransaction(transaction);
        salesRepo.recordSale(transaction);

        receiptCounter = nextReceiptCounter;


        // Create receipt
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

        boolean wasPending =
            transaction.getStatus().equalsIgnoreCase("PENDING");

        if (!wasCompleted && !wasPending) {
            throw new IllegalStateException(
                "Only pending or completed transactions can be cancelled."
            );
        }


        // Cancels tickets and releases booked seats
        if (!transaction.cancel()) {
            return false;
        }


        // Reverse sales and restore stock
        if (wasCompleted) {

            // Reverse the recorded sales
            salesRepo.reverseSale(transaction);

            // Restore product stock
            LinkedList<OrderProduct> products =
                transaction.getProducts();

            for (int i = 0; i < products.size(); i++) {

                OrderProduct orderProduct = products.get(i);

                boolean restored =
                    orderProduct.getProduct().addStock(
                        orderProduct.getQuantity()
                    );

                if (!restored) {
                    throw new IllegalStateException(
                        "Operation cannot be completed. Unable to restore product stock."
                    );
                }
            }
        }

        return true;
    }

}
