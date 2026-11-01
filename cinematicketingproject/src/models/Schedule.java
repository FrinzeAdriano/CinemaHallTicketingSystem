package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import datastructures.LinkedList;
import utility.InputValidator;

public class Schedule {
    private static final DateTimeFormatter DATE_TIME_FORMAT =
        DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private final String scheduleID;
    private Movie movie;
    private CinemaRoom room;
    private LocalDateTime showDateTime;
    private double price;
    private LinkedList<String> bookedSeatIDs;
    private boolean active;

    // Constructor (ID is required because the table has no setter for it)
    public Schedule(String scheduleID) {
        this.scheduleID = scheduleID;
        this.movie = null;
        this.room = null;
        this.showDateTime = null;
        this.price = 0;
        this.bookedSeatIDs = new LinkedList<String>();
        this.active = true;
    }

    // Behavior methods

    // Rules: schedule must be active with a movie, room and showtime set, the room must have seats,
   // the show must not have started, the seat must exist in the room and be in service,
   // and not already booked.
   // Returns true when booked; throws IllegalArgumentException otherwise.
   public boolean bookSeat(String seatID) {
       if (movie == null || room == null || showDateTime == null) {
           throw new IllegalArgumentException("Invalid input. The schedule is not fully set up yet.");
       }
       if (!active) {
           throw new IllegalArgumentException("Invalid input. Schedule " + scheduleID + " is closed for booking.");
       }
       if (!showDateTime.isAfter(LocalDateTime.now())) {
           throw new IllegalArgumentException("Invalid input. The show has already started.");
       }
       if (room.getSeats().size() == 0) {
           throw new IllegalArgumentException("Invalid input. Room " + room.getRoomID() + " has no seats.");
       }
       Seat seat = room.findSeat(seatID);
       if (seat == null) {
           throw new IllegalArgumentException("Invalid input. Seat " + seatID + " does not exist in this room.");
       }
       if (!seat.getStatus()) {
           throw new IllegalArgumentException("Invalid input. Seat " + seat.getSeatID() + " is out of service.");
       }
        // Booking check: the seat must not already be booked in this schedule.
        if (bookedSeatIDs.contains(seat.getSeatID())) {
            throw new IllegalArgumentException("Invalid input. Seat " + seat.getSeatID() + " is already booked.");
        }
       bookedSeatIDs.insertAtEnd(seat.getSeatID());
       return true;
   }

   // Rules: there must be booked seats, the seat must currently be booked,
   // and the show must not have started.
   // Returns true when cancelled; throws IllegalArgumentException otherwise.
   public boolean cancelBooking(String seatID) {
       if (room == null || showDateTime == null) {
           throw new IllegalArgumentException("Invalid input. The schedule is not fully set up yet.");
       }
       if (bookedSeatIDs.size() == 0) {
           throw new IllegalArgumentException("Invalid input. No seats are booked in schedule " + scheduleID + ".");
       }
       Seat seat = room.findSeat(seatID);
       if (seat == null || !bookedSeatIDs.contains(seat.getSeatID())) {
           throw new IllegalArgumentException("Invalid input. Seat " + seatID + " is not booked in this schedule.");
       }
       if (!showDateTime.isAfter(LocalDateTime.now())) {
           throw new IllegalArgumentException("Invalid input. The show has already started.");
       }
       bookedSeatIDs.remove(seat.getSeatID());
       return true;
   }

    // Setters (business rules live here).
    // An invalid value throws IllegalArgumentException with the error message;
    // the controller catches it and passes e.getMessage() to the view.

    // Movie: must not be null. Cannot change once seats are booked.
    public void setMovie(Movie movie) {
        if (bookedSeatIDs.size() > 0) {
            throw new IllegalArgumentException("Invalid input. The movie cannot be changed because seats are already booked.");
        }
        if (movie == null) {
            throw new IllegalArgumentException("Invalid input. A schedule needs a movie.");
        }
        this.movie = movie;
    }

    // Room: must not be null. Cannot change once seats are booked or once the schedule
    // has been added to its current room.
    public void setRoom(CinemaRoom room) {
        if (bookedSeatIDs.size() > 0) {
            throw new IllegalArgumentException("Invalid input. The room cannot be changed because seats are already booked.");
        }
        if (room == null) {
            throw new IllegalArgumentException("Invalid input. A schedule needs a room.");
        }
        if (this.room != null && this.room.findSchedule(scheduleID) == this) {
            throw new IllegalArgumentException("Invalid input. The room cannot be changed after the schedule is added to it.");
        }
        this.room = room;
    }

    // Price: a decimal greater than 0. Cannot change once seats are booked.
    public void setPrice(String price) {
        if (bookedSeatIDs.size() > 0) {
            throw new IllegalArgumentException("Invalid input. The price cannot be changed because seats are already booked.");
        }
        if (!InputValidator.isPositiveDouble(price)) {
            throw new IllegalArgumentException("Invalid input. Price must be a number greater than 0.");
        }
        this.price = Double.parseDouble(price.trim());
    }

    // Show date and time: yyyy-MM-dd HH:mm (24-hour), a real date, not in the past.
    // Cannot change once seats are booked.
    public void setShowDateTime(String showDateTime) {
        if (bookedSeatIDs.size() > 0) {
            throw new IllegalArgumentException("Invalid input. The showtime cannot be changed because seats are already booked.");
        }
        if (!InputValidator.isNotBlank(showDateTime)) {
            throw new IllegalArgumentException("Invalid input. Show date and time cannot be blank.");
        }
        LocalDateTime parsed;
        try {
            parsed = LocalDateTime.parse(showDateTime.trim(), DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid input. Use a real date and time as yyyy-MM-dd HH:mm.");
        }
        if (parsed.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid input. Show date and time cannot be in the past.");
        }
        this.showDateTime = parsed;
    }

    // Active: true = open for booking, false = closed for booking.
    public void setActive(boolean active) {
        this.active = active;
    }

    // Getters
    public String getScheduleID() {
        return scheduleID;
    }

    public Movie getMovie() {
        return movie;
    }

    public CinemaRoom getRoom() {
        return room;
    }

    public LocalDateTime getShowDateTime() {
        return showDateTime;
    }

    public double getPrice() {
        return price;
    }

    public LinkedList<String> getBookedSeatIDs() {
        return bookedSeatIDs;
    }

    public boolean isActive() {
        return active;
    }
}