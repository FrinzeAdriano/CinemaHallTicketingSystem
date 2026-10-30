
package models;

import java.time.LocalDateTime;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;

public class Transaction {
   
    private String transactionID;
    private String bookingType;
    private String cashierID;
    private LocalDateTime dateTime;
    private LinkedList tickets = new LinkedList();
    private LinkedList products = new LinkedList();
    private double discount;
    private double taxRate;
    private String paymentMethod;
    private double  amountPaid;
    private String status;
    private String bookingCode;
    
    
}
