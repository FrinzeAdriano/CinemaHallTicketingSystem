
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
    
    
    public Voucher(String voucherCode) {
 
        if (!InputValidator.isNotBlank(voucherCode)
                || !voucherCode.trim().toUpperCase().matches("[A-Z0-9]{3,15}")) {
            throw new IllegalArgumentException(
                "Invalid input. Voucher code must be 3-15 letters or digits."
            );
        }
 
        this.voucherCode = voucherCode.trim().toUpperCase();
    }

    
    // Setters

    public void setDescription(String description) {
 
        if (!InputValidator.isNotBlank(description)) {
            throw new IllegalArgumentException(
                "Invalid input. No voucher description was provided."
            );
        }
 
        if (description.trim().length() > 100) {
            throw new IllegalArgumentException(
                "Invalid input. Voucher description cannot be longer than 100 characters."
            );
        }
 
        this.description = description.trim();
    }
 
    public void setValue(double value) {
 
        if (!Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException(
                "Invalid input. Voucher value must be greater than 0."
            );
        }
 
        if ("PERCENT".equalsIgnoreCase(discountMode) && value > 100) {
            throw new IllegalArgumentException(
                "Invalid input. A percent discount cannot be more than 100."
            );
        }
 
        this.value = value;
    }
 
    public void setExpiryDate(LocalDate expiryDate) {
 
        if (expiryDate == null) {
            throw new IllegalArgumentException(
                "Invalid input. No voucher expiry date was provided."
            );
        }
 
        if (expiryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                "Invalid input. Voucher expiry date cannot be in the past."
            );
        }
 
        this.expiryDate = expiryDate;
    }
 
    public void setMaxUses(int maxUses) {
 
        if (maxUses <= 0) {
            throw new IllegalArgumentException(
                "Invalid input. Maximum uses must be greater than 0."
            );
        }
 
        if (maxUses > 100000) {
            throw new IllegalArgumentException(
                "Invalid input. Maximum uses cannot be more than 100000."
            );
        }
 
        if (maxUses < timesUsed) {
            throw new IllegalArgumentException(
                "Invalid input. Maximum uses cannot be lower than the times already used."
            );
        }
 
        this.maxUses = maxUses;
    }
    
        public void setTimesUsed(int timesUsed) {
 
        if (timesUsed < 0) {
            throw new IllegalArgumentException(
                "Invalid input. Times used cannot be negative."
            );
        }
 
        if (maxUses > 0 && timesUsed > maxUses) {
            throw new IllegalArgumentException(
                "Invalid input. Times used cannot be more than the maximum uses."
            );
        }
 
        this.timesUsed = timesUsed;
    }
 
    public void setActive(boolean active) {
    this.active = active;
    }
 
    // Getters

    public String getVoucherCode() {
        return voucherCode;
    }
 
    public String getDescription() {
 
        if (!InputValidator.isNotBlank(description)) {
            throw new IllegalStateException(
                "Voucher description has not been set."
            );
        }
 
        return description;
    }
 
    public String getDiscountMode() {
 
        if (!InputValidator.isNotBlank(discountMode)) {
            throw new IllegalStateException(
                "Voucher discount mode has not been set."
            );
        }
 
        return discountMode;
    }
 
    public double getValue() {
         return value;}
 
    public LocalDate getExpiryDate() {
 
        if (expiryDate == null) {
            throw new IllegalStateException(
                "Voucher expiry date has not been set."
            );
        }
 
        return expiryDate;
    }

    public int getMaxUses() {
         return maxUses;}

    public int getTimesUsed() {
         return timesUsed;}

    public boolean isActive() {
         return active;}



}
