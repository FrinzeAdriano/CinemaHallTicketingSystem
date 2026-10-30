
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;

public class CinemaRoom {
    private String roomID;
    private String roomName;
    private int rows;
    private int seatsPerRow;
    boolean status;
    private int [][] seatLayout;
    private Queue showing = new Queue();
}
