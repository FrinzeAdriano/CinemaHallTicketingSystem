package models;

import java.time.LocalDateTime;
import datastructures.LinkedList;
import datastructures.Node;
import datastructures.Queue;
import utility.InputValidator;

public class CinemaRoom {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 30;
    private static final int MIN_ROWS = 1;
    private static final int MAX_ROWS = 26;              // one letter per row (A-Z)
    private static final int MIN_SEATS_PER_ROW = 1;      // a row must have at least one seat

    private final String roomID;
    private String roomName;
    private int rows;
    private int seatsPerRow;
    private boolean status;                              // true = open, false = closed
    private Cinema cinema;
    private LinkedList<Seat> seats;
    private Queue<Schedule> schedules;

    // Constructor (ID is required because the table has no setter for it)
    public CinemaRoom(String roomID) {
        this.roomID = roomID;
        this.roomName = "TBA";
        this.rows = 0;
        this.seatsPerRow = 0;
        this.status = false;
        this.cinema = null;
        this.seats = new LinkedList<Seat>();
        this.schedules = new Queue<Schedule>();
    }

    // Behavior methods

    // Rules: rows and seats per row must be set first, and the layout cannot be
    // regenerated while the room has schedules. Replaces any existing seats with
    // rows x seatsPerRow seats named A1, A2, ... B1, B2, ...
    public void generateSeatLayout() {
        if (rows == 0 || seatsPerRow == 0) {
            throw new IllegalArgumentException("Invalid input. Rows and seats per row must be set before generating the layout.");
        }
        if (!schedules.isEmpty()) {
            throw new IllegalArgumentException("Invalid input. The layout cannot be regenerated while the room has schedules.");
        }
        seats = new LinkedList<Seat>();
        for (int r = 0; r < rows; r++) {
            char rowLetter = (char) ('A' + r);
            for (int n = 1; n <= seatsPerRow; n++) {
                seats.insertAtEnd(new Seat(String.valueOf(rowLetter) + n));
            }
        }
    }

    // Rules: the seat ID must not be blank and must be a valid format (like A1).
    // Returns the seat, or null if the room has no seat with that ID (case-insensitive).
    // Throws IllegalArgumentException for a blank or badly formatted ID.
    public Seat findSeat(String seatID) {
        if (!InputValidator.isNotBlank(seatID)) {
            throw new IllegalArgumentException("Invalid input. Seat ID cannot be blank.");
        }
        String key = seatID.trim();
        if (!InputValidator.isValidSeatID(key)) {
            throw new IllegalArgumentException("Invalid input. Seat ID must be a valid format, like A1.");
        }
        Node<Seat> node = seats.getHead();
        while (node != null) {
            if (node.getData().getSeatID().equalsIgnoreCase(key)) {
                return node.getData();
            }
            node = node.getNext();
        }
        return null;
    }


    // Categories: "ROW" (e.g. A), "TYPE" (e.g. VIP), "STATUS" (true or false).
    // Throws if the room has no seats, for an unknown category, or when nothing matches.
    public LinkedList<Seat> filterSeat(String category, String value) {
        if (seats.size() == 0) {
            throw new IllegalArgumentException("Invalid input. Room " + roomID + " has no seats yet.");
        }
        if (!InputValidator.isNotBlank(category) || !InputValidator.isNotBlank(value)) {
            throw new IllegalArgumentException("Invalid input. Filter category and value cannot be blank.");
        }
        String cat = category.trim().toUpperCase();
        String val = value.trim();
        if (!cat.equals("ROW") && !cat.equals("TYPE") && !cat.equals("STATUS")) {
            throw new IllegalArgumentException("Invalid input. Seat filter category must be ROW, TYPE, or STATUS.");
        }
        LinkedList<Seat> result = new LinkedList<Seat>();
        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            boolean match = false;
            if (cat.equals("ROW")) {
                match = String.valueOf(seat.getSeatRow()).equalsIgnoreCase(val);
            } else if (cat.equals("TYPE")) {
                match = seat.getSeatType().equalsIgnoreCase(val);
            } else {
                match = String.valueOf(seat.getStatus()).equalsIgnoreCase(val);
            }
            if (match) {
                result.insertAtEnd(seat);
            }
        }
        if (result.size() == 0) {
            throw new IllegalArgumentException("Invalid input. No seats found for " + cat + " " + val + ".");
        }
        return result;
    }

    // Rules: seat must not be null and its ID must not already exist in this room.
    // Returns true when added; throws IllegalArgumentException otherwise.
    public boolean addSeat(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("Invalid input. No seat was provided.");
        }
        if (findSeat(seat.getSeatID()) != null) {
            throw new IllegalArgumentException("Invalid input. Seat " + seat.getSeatID() + " already exists in this room.");
        }
        seats.insertAtEnd(seat);
        return true;
    }

    // Rules: the room must have seats, the seat must exist, and it must not be booked
    // in any schedule of this room.
    // Returns true when removed; throws IllegalArgumentException otherwise.
    public boolean removeSeat(String seatID) {
        if (seats.size() == 0) {
            throw new IllegalArgumentException("Invalid input. Room " + roomID + " has no seats to remove.");
        }
        Seat seat = findSeat(seatID);
        if (seat == null) {
            throw new IllegalArgumentException("Invalid input. Seat " + seatID.trim() + " was not found in this room.");
        }
        Node<Schedule> node = schedules.getFront();
        while (node != null) {
            if (node.getData().getBookedSeatIDs().contains(seat.getSeatID())) {
                throw new IllegalArgumentException("Invalid input. Seat " + seat.getSeatID() + " is booked and cannot be removed.");
            }
            node = node.getNext();
        }
        seats.remove(seat);
        return true;
    }

    // Categories: "MOVIE" (title contains value), "DATE" (yyyy-MM-dd), "ACTIVE" (true or false).
    // Throws if the room has no schedules, for an unknown category or bad value, or when nothing matches.
    public LinkedList<Schedule> filterSchedule(String category, String value) {
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("Invalid input. Room " + roomID + " has no schedules yet.");
        }
        if (!InputValidator.isNotBlank(category) || !InputValidator.isNotBlank(value)) {
            throw new IllegalArgumentException("Invalid input. Filter category and value cannot be blank.");
        }
        String cat = category.trim().toUpperCase();
        String val = value.trim();
        if (!cat.equals("MOVIE") && !cat.equals("DATE") && !cat.equals("ACTIVE")) {
            throw new IllegalArgumentException("Invalid input. Schedule filter category must be MOVIE, DATE, or ACTIVE.");
        }
        if (cat.equals("DATE") && !InputValidator.isValidDate(val)) {
            throw new IllegalArgumentException("Invalid input. Date must be a valid date in yyyy-MM-dd format.");
        }
        LinkedList<Schedule> result = new LinkedList<Schedule>();
        Node<Schedule> node = schedules.getFront();
        while (node != null) {
            Schedule schedule = node.getData();
            boolean match = false;
            if (cat.equals("MOVIE")) {
                match = schedule.getMovie().getTitle().toLowerCase().contains(val.toLowerCase());
            } else if (cat.equals("DATE")) {
                match = schedule.getShowDateTime().toLocalDate().toString().equals(val);
            } else {
                match = String.valueOf(schedule.isActive()).equalsIgnoreCase(val);
            }
            if (match) {
                result.insertAtEnd(schedule);
            }
            node = node.getNext();
        }
        if (result.size() == 0) {
            throw new IllegalArgumentException("Invalid input. No schedules found for " + cat + " " + val + ".");
        }
        return result;
    }

    // Rules: schedule must not be null, must belong to this room, the room must be open
    // and have seats, the ID must be unique, and the showtime must not overlap another schedule.
    // Returns true when added; throws IllegalArgumentException otherwise.
    public boolean addSchedule(Schedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("Invalid input. No schedule was provided.");
        }
        if (schedule.getRoom() != this) {
            throw new IllegalArgumentException("Invalid input. Schedule " + schedule.getScheduleID() + " belongs to a different room.");
        }
        if (!status) {
            throw new IllegalArgumentException("Invalid input. Room " + roomID + " is closed.");
        }
        if (seats.size() == 0) {
            throw new IllegalArgumentException("Invalid input. Room " + roomID + " has no seats yet.");
        }
        if (findSchedule(schedule.getScheduleID()) != null) {
            throw new IllegalArgumentException("Invalid input. Schedule " + schedule.getScheduleID() + " already exists in this room.");
        }
        LocalDateTime newStart = schedule.getShowDateTime();
        LocalDateTime newEnd = newStart.plusMinutes(schedule.getMovie().getDuration());
        Node<Schedule> node = schedules.getFront();
        while (node != null) {
            Schedule existing = node.getData();
            LocalDateTime start = existing.getShowDateTime();
            LocalDateTime end = start.plusMinutes(existing.getMovie().getDuration());
            if (newStart.isBefore(end) && start.isBefore(newEnd)) {
                throw new IllegalArgumentException(
                    "Invalid input. The showtime overlaps with schedule " + existing.getScheduleID() + ".");
            }
            node = node.getNext();
        }
        schedules.enqueue(schedule);
        return true;
    }

    // Rules: the room must have schedules, the schedule must exist, and it must have no booked seats.
    // Rebuilds the queue without it. Returns true when removed; throws otherwise.
    public boolean removeSchedule(String scheduleID) {
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("Invalid input. Room " + roomID + " has no schedules to remove.");
        }
        Schedule target = findSchedule(scheduleID);
        if (target == null) {
            throw new IllegalArgumentException("Invalid input. Schedule " + scheduleID + " was not found in this room.");
        }
        if (target.getBookedSeatIDs().size() > 0) {
            throw new IllegalArgumentException("Invalid input. Schedule " + scheduleID + " has booked seats and cannot be removed.");
        }
        Queue<Schedule> rebuilt = new Queue<Schedule>();
        while (!schedules.isEmpty()) {
            Schedule current = schedules.dequeue();
            if (current != target) {
                rebuilt.enqueue(current);
            }
        }
        schedules = rebuilt;
        return true;
    }

    // Returns null if no schedule with that ID exists.
    public Schedule findSchedule(String scheduleID) {
        if (scheduleID == null) {
            return null;
        }
        Node<Schedule> node = schedules.getFront();
        while (node != null) {
            if (node.getData().getScheduleID().equals(scheduleID.trim())) {
                return node.getData();
            }
            node = node.getNext();
        }
        return null;
    }

    // Setters (business rules live here).
    // An invalid value throws IllegalArgumentException with the error message;
    // the controller catches it and passes e.getMessage() to the view.

    // Room name: 2 to 30 characters after trimming.
    public void setRoomName(String name) {
        if (!InputValidator.isNotBlank(name)) {
            throw new IllegalArgumentException("Invalid input. Room name cannot be blank.");
        }
        String cleaned = name.trim();
        if (!InputValidator.inRange(cleaned.length(), MIN_NAME_LENGTH, MAX_NAME_LENGTH)) {
            throw new IllegalArgumentException(
                "Invalid input. Room name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }
        this.roomName = cleaned;
    }

    // Rows (added setter): whole number from 1 to 26, one letter per row.
    public void setRows(String rows) {
        if (!InputValidator.isPositiveInt(rows)) {
            throw new IllegalArgumentException("Invalid input. Rows must be a whole number.");
        }
        int value = Integer.parseInt(rows.trim());
        if (!InputValidator.inRange(value, MIN_ROWS, MAX_ROWS)) {
            throw new IllegalArgumentException(
                "Invalid input. Rows must be between " + MIN_ROWS + " and " + MAX_ROWS + ".");
        }
        this.rows = value;
    }

    // Seats per row (added setter): whole number of at least 1, no upper limit.
    public void setSeatsPerRow(String seatsPerRow) {
        if (!InputValidator.isPositiveInt(seatsPerRow)) {
            throw new IllegalArgumentException("Invalid input. Seats per row must be a whole number.");
        }
        int value = Integer.parseInt(seatsPerRow.trim());
        if (value < MIN_SEATS_PER_ROW) {
            throw new IllegalArgumentException(
                "Invalid input. Seats per row must be at least " + MIN_SEATS_PER_ROW + ".");
        }
        this.seatsPerRow = value;
    }

    // Status: true = open, false = closed. A room with schedules cannot be closed.
    public void setStatus(boolean status) {
        if (!status && !schedules.isEmpty()) {
            throw new IllegalArgumentException("Invalid input. A room with schedules cannot be closed.");
        }
        this.status = status;
    }

    // Cinema: null is allowed because Cinema.removeRoom detaches the room with setCinema(null).
    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    // Getters
    public String getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getRows() {
        return rows;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public boolean getStatus() {
        return status;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public LinkedList<Seat> getSeats() {
        return seats;
    }

    public Queue<Schedule> getSchedules() {
        return schedules;
    }
}