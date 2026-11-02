
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import java.time.LocalDateTime;
import utility.*;
import service.*;

public final class  Receipt {
    
    private String receiptNo;
    private LocalDateTime dateTimeIssued;
    private Transaction transaction;
    
     public Receipt(String receiptNo, LocalDateTime dateTimeIssued,
                   Transaction transaction) {

        // Receipt number validation
        if (!InputValidator.isNotBlank(receiptNo)) {
            throw new IllegalArgumentException(
                "Invalid input. Receipt number cannot be blank."
            );
        }

        // Date and time validation
        if (dateTimeIssued == null) {
            throw new IllegalArgumentException(
                "Invalid input. Receipt issue date and time cannot be null."
            );
        }

        if (dateTimeIssued.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                "Invalid input. Receipt issue date and time cannot be in the future."
            );
        }

        // Transaction validation
        if (transaction == null) {
            throw new IllegalArgumentException(
                "Invalid input. Receipt transaction cannot be null."
            );
        }

        this.receiptNo = receiptNo.trim();
        this.dateTimeIssued = dateTimeIssued;
        this.transaction = transaction;
    }
    
    // Getters

    public String getReceiptNo() {
        return receiptNo;
    }

    public LocalDateTime getDateTimeIssued() {
        return dateTimeIssued;
    }

    public Transaction getTransaction() {
        return transaction;
    }

}
