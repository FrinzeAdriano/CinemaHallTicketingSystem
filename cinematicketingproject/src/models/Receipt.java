
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
    
    public Receipt(String receiptNo, LocalDateTime dateTimeIssued, Transaction transaction) {
        this.receiptNo = receiptNo;
        this.dateTimeIssued = dateTimeIssued;
        this.transaction = transaction;
    }
    
    // Getters

    public String getReceiptNo() {
         return receiptNo;}

    public LocalDateTime getDateTimeIssued() {
         return dateTimeIssued;}

    public Transaction getTransaction() {
         return transaction;}
}
