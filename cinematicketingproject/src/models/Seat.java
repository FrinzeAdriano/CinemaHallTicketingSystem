package models;

import utility.InputValidator;

public class Seat {
    private static final String[] VALID_SEAT_TYPES = {"Regular", "Premium", "VIP"};

    private final String seatID;
    private final char seatRow;
    private final int seatNumber;
    private String seatType;
    private boolean status;
    private double priceModifier;

    // Constructor (ID is required because the table has no setters for seatID, seatRow, seatNumber).
    // A valid ID is a letter followed by a number from 1 to 999 (e.g. "A1", "b12" -> "B12").
    // Throws IllegalArgumentException if the ID is invalid.
    public Seat(String seatID) {
        if (!InputValidator.isValidSeatID(seatID)) {
            throw new IllegalArgumentException("Invalid input. Seat ID must be a letter followed by a number from 1 to 999 (e.g. A1).");
        }
        String cleaned = seatID.trim().toUpperCase();
        this.seatID = cleaned;
        this.seatRow = cleaned.charAt(0);
        this.seatNumber = Integer.parseInt(cleaned.substring(1));
        this.seatType = "Regular";
        this.status = true;
        this.priceModifier = 1.0;
    }

    // Setters (business rules live here).
    // An invalid value throws IllegalArgumentException with the error message;
    // the controller catches it and passes e.getMessage() to the view.

    // Seat type: must be Regular, Premium, or VIP (case-insensitive, stored in that form).
    public void setSeatType(String seatType) {
        if (!InputValidator.isNotBlank(seatType)) {
            throw new IllegalArgumentException("Invalid input. Seat type cannot be blank.");
        }
        String cleaned = seatType.trim();
        for (int i = 0; i < VALID_SEAT_TYPES.length; i++) {
            if (VALID_SEAT_TYPES[i].equalsIgnoreCase(cleaned)) {
                this.seatType = VALID_SEAT_TYPES[i];
                return;
            }
        }
        throw new IllegalArgumentException("Invalid input. Seat type must be one of: " + String.join(", ", VALID_SEAT_TYPES) + ".");
    }

    // Status: true = seat is usable, false = seat is out of service.
    public void setStatus(boolean status) {
        this.status = status;
    }

    // Price modifier (multiplier on the schedule price): a decimal greater than 0.
    public void setPriceModifier(String priceModifier) {
        if (!InputValidator.isPositiveDouble(priceModifier)) {
            throw new IllegalArgumentException("Invalid input. Price modifier must be a number greater than 0.");
        }
        this.priceModifier = Double.parseDouble(priceModifier.trim());
    }

    // Getters
    public String getSeatID() {
        return seatID;
    }

    public char getSeatRow() {
        return seatRow;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public boolean getStatus() {
        return status;
    }

    public double getPriceModifier() {
        return priceModifier;
    }
}