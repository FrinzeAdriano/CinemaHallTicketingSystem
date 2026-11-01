package view;

import datastructures.Node;
import models.CinemaRoom;
import models.Schedule;
import models.Seat;

public class SeatView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public SeatView() {
        super();
    }

    // Menus and prompts

    public int showSeatMenu() {
        System.out.println();
        System.out.println("===== SEAT MANAGEMENT =====");
        System.out.println("1. View room layout");
        System.out.println("2. Add seat");
        System.out.println("3. Remove seat");
        System.out.println("4. Change seat type");
        System.out.println("5. Filter seats");
        System.out.println("0. Back");
        return readInt("Choose an option: ", 0, 5);
    }

    public String promptSeatID() {
        return readString("Enter seat ID (e.g. A1): ");
    }

    public String promptSeatType() {
        return readString("Enter seat type (e.g. Regular, VIP): ");
    }

    // Display methods

    // Shows the seats of the schedule's room: [ ] is free, [X] is booked in this schedule.
    public void displaySeatGrid(Schedule schedule) {
        System.out.println();
        System.out.println("===== SEATS FOR SCHEDULE " + schedule.getScheduleID() + " =====");
        System.out.println("                SCREEN");
        System.out.println("[ ] available   [X] booked");
        System.out.println();
        Node<Seat> node = schedule.getRoom().getSeats().getHead();
        char currentRow = 0;
        while (node != null) {
            Seat seat = node.getData();
            if (seat.getSeatRow() != currentRow) {
                if (currentRow != 0) {
                    System.out.println();
                }
                currentRow = seat.getSeatRow();
            }
            String mark = schedule.getBookedSeatIDs().contains(seat.getSeatID()) ? "X" : " ";
            System.out.print(seat.getSeatID() + "[" + mark + "] ");
            node = node.getNext();
        }
        System.out.println();
    }

    // Shows every seat of the room with its type and status.
    public void displayRoomLayout(CinemaRoom room) {
        System.out.println();
        System.out.println("===== LAYOUT OF " + room.getRoomName() + " (" + room.getRoomID() + ") =====");
        System.out.println("                SCREEN");
        Node<Seat> node = room.getSeats().getHead();
        char currentRow = 0;
        while (node != null) {
            Seat seat = node.getData();
            if (seat.getSeatRow() != currentRow) {
                if (currentRow != 0) {
                    System.out.println();
                }
                currentRow = seat.getSeatRow();
            }
            System.out.print(seat.getSeatID() + "(" + seat.getSeatType() + ") ");
            node = node.getNext();
        }
        System.out.println();
    }

    public void displaySeat(Seat seat) {
        System.out.println();
        System.out.println("----- SEAT DETAILS -----");
        System.out.println("ID             : " + seat.getSeatID());
        System.out.println("Row            : " + seat.getSeatRow());
        System.out.println("Number         : " + seat.getSeatNumber());
        System.out.println("Type           : " + seat.getSeatType());
        System.out.println("Status         : " + seat.getStatus());
        System.out.println("Price modifier : " + seat.getPriceModifier());
    }
}