package models;

import java.time.LocalDate;
import utility.InputValidator;

public class Voucher {
    private static final int MAX_DESCRIPTION_LENGTH = 100;
    private static final int MAX_USES_LIMIT = 100000;
    private static final double MAX_PERCENT = 100;

    private final String voucherCode;
    private String description;
    private String discountMode;      // "PERCENT" or "FIXED"
    private double value;
    private LocalDate expiryDate;
    private int maxUses;
    private int timesUsed;
    private boolean active;

    // Constructor (code is required because the table has no setter for it).
    // A valid code is 3 to 15 letters or digits, stored in uppercase.
    // Throws IllegalArgumentException if the code is invalid.
    public Voucher(String voucherCode) {
        if (!InputValidator.isNotBlank(voucherCode)
                || !voucherCode.trim().toUpperCase().matches("[A-Z0-9]{3,15}")) {
            throw new IllegalArgumentException("Invalid input. Voucher code must be 3-15 letters or digits.");
        }
        this.voucherCode = voucherCode.trim().toUpperCase();
    }

    // Setters (business rules live here).
    // An invalid value throws IllegalArgumentException with the error message;
    // the controller catches it and passes e.getMessage() to the view.

    public void setDiscountMode(String discountMode) {

    if (!InputValidator.isNotBlank(discountMode)) {
        throw new IllegalArgumentException(
            "Invalid input. Discount mode cannot be blank."
        );
    }

    String mode = discountMode.trim().toUpperCase();

    if (!mode.equals("PERCENT") && !mode.equals("FIXED")) {
        throw new IllegalArgumentException(
            "Invalid input. Discount mode must be PERCENT or FIXED."
        );
    }

    if (mode.equals("PERCENT") && value > 100) {
        throw new IllegalArgumentException(
            "Invalid input. A percent discount cannot be more than 100."
        );
    }

    this.discountMode = mode;
}
    
    public void setDescription(String description) {
        if (!InputValidator.isNotBlank(description)) {
            throw new IllegalArgumentException("Invalid input. Voucher description cannot be blank.");
        }
        String cleaned = description.trim();
        if (cleaned.length() > MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                "Invalid input. Voucher description cannot be longer than " + MAX_DESCRIPTION_LENGTH + " characters.");
        }
        this.description = cleaned;
    }


    // Value: a decimal greater than 0. A PERCENT voucher cannot be more than 100.
    public void setValue(String value) {
        if (!InputValidator.isPositiveDouble(value)) {
            throw new IllegalArgumentException("Invalid input. Discount value must be a number greater than 0.");
        }
        double parsed = Double.parseDouble(value.trim());
        if ("PERCENT".equals(discountMode) && parsed > MAX_PERCENT) {
            throw new IllegalArgumentException("Invalid input. A percent discount cannot be more than 100.");
        }
        this.value = parsed;
    }

    // Expiry date: yyyy-MM-dd, a real date, not in the past.
    public void setExpiryDate(String expiryDate) {
        if (!InputValidator.isNotBlank(expiryDate)) {
            throw new IllegalArgumentException("Invalid input. Expiry date cannot be blank.");
        }
        if (!InputValidator.isValidDate(expiryDate)) {
            throw new IllegalArgumentException("Invalid input. Expiry date must be a real date as yyyy-MM-dd.");
        }
        LocalDate parsed = LocalDate.parse(expiryDate.trim());
        if (parsed.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid input. Expiry date cannot be in the past.");
        }
        this.expiryDate = parsed;
    }

    // Maximum uses: whole number from 1 to 100000, and not lower than the times already used.
    public void setMaxUses(String maxUses) {
        if (!InputValidator.isPositiveInt(maxUses)) {
            throw new IllegalArgumentException("Invalid input. Maximum uses must be a whole number greater than 0.");
        }
        int parsed = Integer.parseInt(maxUses.trim());
        if (parsed > MAX_USES_LIMIT) {
            throw new IllegalArgumentException("Invalid input. Maximum uses cannot be more than " + MAX_USES_LIMIT + ".");
        }
        if (parsed < timesUsed) {
            throw new IllegalArgumentException("Invalid input. Maximum uses cannot be lower than the times already used.");
        }
        this.maxUses = parsed;
    }

    // Times used: set by checkout, so it takes a number and not a string.
    public void setTimesUsed(int timesUsed) {
        if (timesUsed < 0) {
            throw new IllegalArgumentException("Invalid input. Times used cannot be negative.");
        }
        if (maxUses > 0 && timesUsed > maxUses) {
            throw new IllegalArgumentException("Invalid input. Times used cannot be more than the maximum uses.");
        }
        this.timesUsed = timesUsed;
    }

    // Active: true = can be used at checkout, false = deactivated.
    public void setActive(boolean active) {
        this.active = active;
    }

    // Getters
    public String getVoucherCode() {
        return voucherCode;
    }

    public String getDescription() {
        if (!InputValidator.isNotBlank(description)) {
            throw new IllegalStateException("Voucher description has not been set.");
        }
        return description;
    }

    public String getDiscountMode() {
        if (!InputValidator.isNotBlank(discountMode)) {
            throw new IllegalStateException("Voucher discount mode has not been set.");
        }
        return discountMode;
    }

    public double getValue() {
        return value;
    }

    public LocalDate getExpiryDate() {
        if (expiryDate == null) {
            throw new IllegalStateException("Voucher expiry date has not been set.");
        }
        return expiryDate;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public boolean isActive() {
        return active;
    }
}