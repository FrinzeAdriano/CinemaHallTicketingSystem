
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
    
   public Ticket(String ticketID, Schedule schedule, Seat seat, double price, String status) {
    this.ticketID = ticketID;
    this.schedule = schedule;
    this.seat = seat;
    this.price = price;
    this.status = status;
}
   // Setter
    public void setStatus(String status) {
    this.status = status;}
    
    
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
