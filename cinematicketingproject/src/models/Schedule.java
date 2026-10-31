
package models;

import java.time.LocalDateTime;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;

public class Schedule {
    private final String  scheduleID;
    private Movie movie;
    private CinemaRoom room;
    private LocalDateTime showDateTime;
    private double price;
    private LinkedList bookedSeatIDs = new LinkedList();
    private boolean active;
    
    
    public Schedule(String scheduleID, Movie movie, CinemaRoom room,
                LocalDateTime showDateTime, double price,
                LinkedList bookedSeatIDs, boolean active) {

    this.scheduleID = scheduleID;
    this.movie = movie;
    this.room = room;
    this.showDateTime = showDateTime;
    this.price = price;
    this.bookedSeatIDs = bookedSeatIDs;
    this.active = active;
}
    // Setters
    public void setPrice(double price) {
    this.price = price;}

    public void setShowDateTime(LocalDateTime showDateTime) {
    this.showDateTime = showDateTime;}

    public void setActive(boolean active) {
    this.active = active;
    }
        // Getters
    public String getScheduleID() {
    return scheduleID;}

    public Movie getMovie() {
    return movie;}

    public CinemaRoom getRoom() {
    return room;}

    public LocalDateTime getShowDateTime() {
    return showDateTime;}

    public double getPrice() {
    return price;}

    public LinkedList getBookedSeatIDs() {
    return bookedSeatIDs;}

    public boolean isActive() {
    return active;}


    
}
