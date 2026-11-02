
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;

public final class Ticket {
    private final String ticketID;
    private Schedule schedule;
    private Seat seat;
    private double price;
    private String status;
    
    private String discountType;
    private double discountAmount;

private static final double DISCOUNT_RATE = 0.20;
    
   public Ticket(String ticketID, Schedule schedule, Seat seat,
                  double price, String status) {

        // Ticket ID validation
        if (!InputValidator.isNotBlank(ticketID)) {
            throw new IllegalArgumentException(
                "Invalid input. Ticket ID cannot be blank."
            );
        }

        String formattedID = ticketID.trim();

        if (formattedID.matches(".*\\s+.*")) {

            throw new IllegalArgumentException(
                "Invalid input. Ticket ID cannot contain spaces."
            );
        }

        this.ticketID = formattedID;

        // Schedule validation
        if (schedule == null) {
            throw new IllegalArgumentException(
                "Invalid input. No schedule was provided for this ticket."
            );
        }

        this.schedule = schedule;

        // Seat validation
        if (seat == null) {
            throw new IllegalArgumentException(
                "Invalid input. No seat was provided for this ticket."
            );
        }

        this.seat = seat;

        // Price validation
        if (!InputValidator.isPositiveDouble(String.valueOf(price))) {
            throw new IllegalArgumentException(
                "Invalid input. Ticket price must be a finite amount greater than zero."
            );
        }

        this.price = price;

        // Status validation
        if (!InputValidator.isNotBlank(status)) {
            throw new IllegalArgumentException(
                "Invalid input. Ticket status cannot be blank."
            );
        }

        String formattedStatus = status.trim().toUpperCase();

        if (formattedStatus.length() > 20 ||
            formattedStatus.matches("(?s).*\\p{Cntrl}.*")) {

            throw new IllegalArgumentException(
                "Invalid input. Ticket status cannot exceed 20 characters or contain control characters."
            );
        }

        this.status = formattedStatus;
        this.discountType = "NONE";
        this.discountAmount = 0;
    }
   
   
   // Setter
    public void setStatus(String status) {

        if (!InputValidator.isNotBlank(status)) {
            throw new IllegalArgumentException(
                "Invalid input. Ticket status cannot be blank."
            );
        }

        String formattedStatus = status.trim().toUpperCase();

        if (!formattedStatus.equals("BOOKED") &&
            !formattedStatus.equals("CANCELLED") &&
            !formattedStatus.equals("USED")) {

            throw new IllegalArgumentException(
                "Invalid input. Ticket status must be BOOKED, CANCELLED, or USED."
            );
        }

        this.status = formattedStatus;
    }
    
    // Applies a Senior Citizen or PWD discount to this ticket
        public void applyDiscount(String discountType) {

            if (!InputValidator.isNotBlank(discountType)) {
                throw new IllegalArgumentException(
                    "Discount type cannot be blank."
                );
            }

            String formattedType = discountType.trim().toUpperCase();

            if (!formattedType.equals("SENIOR")
                    && !formattedType.equals("PWD")) {
                throw new IllegalArgumentException(
                    "Discount type must be SENIOR or PWD."
                );
            }

            if (!status.equals("BOOKED")) {
                throw new IllegalStateException(
                    "Discount can only be applied to booked tickets."
                );
            }

            this.discountType = formattedType;
            this.discountAmount = Math.round(
                price * DISCOUNT_RATE * 100.0
            ) / 100.0;
        }

        // Gets the discount type
        public String getDiscountType() {
            return discountType;
        }

        // Gets the discount amount
        public double getDiscountAmount() {
            return discountAmount;
        }

        // Gets the ticket price after discount
        public double getNetPrice() {
            return price - discountAmount;
        }
   // Getters
    public String getTicketID() {
    return ticketID;}

    public Schedule getSchedule() {
    return schedule;}

    public Seat getSeat() {
    return seat;}

    public double getPrice() {
    return price;}

    public String getStatus() {
    return status;}
}
