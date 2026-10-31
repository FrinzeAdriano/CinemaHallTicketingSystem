
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import java.time.LocalDate;
import utility.*;
import service.*;

public class Voucher {
    
    private String voucherCode;
    private String description;
    private String discountMode;
    private double value;
    private LocalDate expiryDate;
    private int maxUses;
    private int timesUsed;
    private boolean active;
    
    
    public Voucher(String voucherCode, String description, String discountMode, double value, LocalDate expiryDate,
               int maxUses, int timesUsed, boolean active) {
        this.voucherCode = voucherCode;
        this.description = description;
        this.discountMode = discountMode;
        this.value = value;
        this.expiryDate = expiryDate;
        this.maxUses = maxUses;
        this.timesUsed = timesUsed;
        this.active = active;
    }
    
    // Setters

    public void setDescription(String description) {
    this.description = description;
    }

    public void setValue(double value) {
    this.value = value;
    }

    public void setExpiryDate(LocalDate expiryDate) {
    this.expiryDate = expiryDate;
    }

    public void setMaxUses(int maxUses) {
    this.maxUses = maxUses;
    }

    public void setTimesUsed(int timesUsed) {
    this.timesUsed = timesUsed;
    }

    public void setActive(boolean active) {
    this.active = active;
    }
    // Getters

    public String getVoucherCode() {
         return voucherCode;}

    public String getDescription() {
         return description;}

    public String getDiscountMode() {
         return discountMode;}

    public double getValue() {
         return value;}

    public LocalDate getExpiryDate() {
         return expiryDate;}

    public int getMaxUses() {
         return maxUses;}

    public int getTimesUsed() {
         return timesUsed;}

    public boolean isActive() {
         return active;}



}
