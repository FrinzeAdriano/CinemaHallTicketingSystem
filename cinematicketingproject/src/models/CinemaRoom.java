
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;

public class CinemaRoom {

    private final String roomID;
    private String roomName;
    private int rows;
    private int seatsPerRow;
    private boolean status;
    private Cinema cinema;
    private LinkedList seats = new LinkedList();
    private Queue schedules = new Queue();


    // Constructor

    public CinemaRoom(String roomID, String roomName, int rows, int seatsPerRow,
                      boolean status, Cinema cinema, LinkedList seats,
                      Queue schedules) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
        this.status = status;
        this.cinema = cinema;
        this.seats = seats;
        this.schedules = schedules;
        }


    // Seat Methods

    public void generateSeatLayout() {

    }

    public Seat findSeat(String seatID) {
         return null;}

    public LinkedList filterSeat(String category, String value) {
         return null;}

    public boolean addSeat(Seat seat) {
         return false;}

    public boolean removeSeat(String seatID) {
         return false;}


    // Schedule Methods

    public LinkedList filterSchedule(String category, String value) {
         return null;}

    public boolean addSchedule(Schedule schedule) {
         return false;}

    public boolean removeSchedule(String scheduleID) {
         return false;}

    public Schedule findSchedule(String scheduleID) {
         return null;}


    // Setters

    public void setRoomName(String name) {
        this.roomName = name;
        }

    public void setStatus(boolean status) {
        this.status = status;
        }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
        }


    // Getters

    public String getRoomID() {
         return roomID;}

    public String getRoomName() {
         return roomName;}

    public int getRows() {
         return rows;}

    public int getSeatsPerRow() {
         return seatsPerRow;}

    public boolean getStatus() {
         return status;}

    public Cinema getCinema() {
         return cinema;}

    public LinkedList getSeats() {
         return seats;}

    public Queue getSchedules() {
         return schedules;}

}