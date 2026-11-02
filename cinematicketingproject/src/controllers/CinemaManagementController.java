package controllers;

import models.*;
import repositories.*;
import utility.*;
import view.*;
import datastructures.*;


public class CinemaManagementController {
    private CinemaRepository cinemaRepo;
    private MovieRepository movieRepo;
    private CinemaView cinemaView;
    private CinemaRoomListView roomView;
    private SeatView seatView;
    private ScheduleView scheduleView;
    private MovieRepositoryView movieListView;
    
    public CinemaManagementController(CinemaRepository cinemaRepo, MovieRepository movieRepo,
            CinemaView cinemaView, CinemaRoomListView roomView, SeatView seatView, ScheduleView scheduleView, MovieRepositoryView movieListView) {
        this.cinemaRepo = cinemaRepo;
        this.movieRepo = movieRepo;
        this.cinemaView = cinemaView;
        this.roomView = roomView;
        this.seatView = seatView;
        this.scheduleView = scheduleView;
        this.movieListView = movieListView;
    }

    public void manageCinemas() {
        int choice = -1;
        while (choice != 0) {
            choice = cinemaView.showCinemaMenu();
            try {
                switch (choice) {
                    case 1: {
                        // Add cinema (0 at any prompt goes back without adding)
                        Cinema cinema = new Cinema(cinemaRepo.generateCinemaID());
                        if (LoopUtility.retryUntilValid(cinemaView::promptCinemaName, cinema::setName, cinemaView::showError, "0")
                                && LoopUtility.retryUntilValid(cinemaView::promptCinemaLocation, cinema::setLocation, cinemaView::showError, "0")) {
                            cinemaRepo.addCinema(cinema);   // throws if null or the ID already exists
                            cinemaView.showMessage("Cinema " + cinema.getCinemaID() + " was added.");
                        }
                        break;
                    }
                    case 2: {
                        // Edit cinema (table first, then found by ID, 0 to go back)
                        cinemaView.displayCinemas(cinemaRepo.viewCinemas());   // throws if there are no cinemas
                        Cinema cinema = LoopUtility.retryUntilValidResult(cinemaView::promptCinemaID,
                                cinemaRepo::findCinemaByID, cinemaView::showError, "0");
                        if (cinema != null) {
                            int editChoice = -1;
                            while (editChoice != 0) {
                                editChoice = cinemaView.showEditCinemaMenu(cinema);
                                switch (editChoice) {
                                    case 1:
                                        if (LoopUtility.retryUntilValid(cinemaView::promptCinemaName, cinema::setName, cinemaView::showError, "0")) {
                                            cinemaView.showMessage("Name updated.");
                                        }
                                        break;
                                    case 2:
                                        if (LoopUtility.retryUntilValid(cinemaView::promptCinemaLocation, cinema::setLocation, cinemaView::showError, "0")) {
                                            cinemaView.showMessage("Location updated.");
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        break;
                    }
                    case 3: {
                        // Remove cinema (by ID, 0 to go back). The ID is checked first,
                        // and the confirmation is only asked when the cinema exists.
                        cinemaView.displayCinemas(cinemaRepo.viewCinemas());   // throws if there are no cinemas
                        Boolean removed = LoopUtility.retryUntilValidResult(cinemaView::promptCinemaID,
                                id -> {
                                    boolean exists = false;
                                    Node<Cinema> node = cinemaRepo.viewCinemas().getHead();
                                    while (node != null) {
                                        if (node.getData().getCinemaID().equalsIgnoreCase(id.trim())) {
                                            exists = true;
                                            break;
                                        }
                                        node = node.getNext();
                                    }
                                    if (!exists) {
                                        throw new IllegalArgumentException("Invalid input. Cinema " + id.trim() + " was not found.");
                                    }
                                    return cinemaView.confirm("Remove cinema " + id.trim() + "?") && cinemaRepo.removeCinema(id);
                                },
                                cinemaView::showError, "0");
                        if (removed != null && removed) {
                            cinemaView.showMessage("Cinema was removed.");
                        }
                        break;
                    }
                    case 4: {
                        // Find cinema by name (0 to go back)
                        cinemaRepo.viewCinemas();   // throws if there are no cinemas
                        Cinema found = LoopUtility.retryUntilValidResult(cinemaView::promptCinemaName,
                                cinemaRepo::findCinema, cinemaView::showError, "0");
                        if (found != null) {
                            cinemaView.displayCinema(found);
                        }
                        break;
                    }
                    case 5:
                        cinemaView.displayCinemas(cinemaRepo.viewCinemas());   // throws if empty
                        break;
                    default:
                        break;   // 0 exits the loop
                }
            } catch (IllegalArgumentException e) {
                cinemaView.showError(e.getMessage());
            }
        }
    }
 
    public void manageRooms() {
        // Pick the cinema first (by ID, 0 to go back)
        Cinema cinema;
        try {
            cinemaView.displayCinemas(cinemaRepo.viewCinemas());   // throws if there are no cinemas
            cinema = LoopUtility.retryUntilValidResult(cinemaView::promptCinemaID,
                    cinemaRepo::findCinemaByID, cinemaView::showError, "0");
        } catch (IllegalArgumentException e) {
            cinemaView.showError(e.getMessage());
            return;
        }
        if (cinema == null) {
            return;
        }
 
        int choice = -1;
        while (choice != 0) {
            choice = roomView.showRoomMenu();
            try {
                // Edit, remove, generate layout and view all need at least one room.
                if (cinema.getCinemaRooms().size() == 0 && choice >= 2 && choice <= 5) {
                    throw new IllegalArgumentException("Invalid input. This cinema has no rooms yet.");
                }
                switch (choice) {
                    case 1: {
                        // Add room (ID is auto-generated; 0 at any prompt goes back without adding)
                        CinemaRoom room = new CinemaRoom(CinemaRoom.generateRoomID(cinema));
                        if (LoopUtility.retryUntilValid(roomView::promptRoomName, room::setRoomName, roomView::showError, "0")
                                && LoopUtility.retryUntilValid(roomView::promptRows, room::setRows, roomView::showError, "0")
                                && LoopUtility.retryUntilValid(roomView::promptSeatsPerRow, room::setSeatsPerRow, roomView::showError, "0")) {
                            room.setStatus(roomView.confirm("Open the room for use now?"));
                            cinema.addRoom(room);
                            roomView.showMessage("Room " + room.getRoomID() + " was added.");
                        }
                        break;
                    }
                    case 2: {
                        // Edit room (found by ID, 0 to go back)
                        roomView.displayRooms(cinema.getCinemaRooms());
                        CinemaRoom room = LoopUtility.retryUntilValidResult(roomView::promptRoomID,
                                cinema::findRoom, roomView::showError, "0");
                        int editChoice = (room == null) ? 0 : -1;
                        while (editChoice != 0) {
                            editChoice = roomView.showEditRoomMenu(room);
                            switch (editChoice) {
                                case 1:
                                    if (LoopUtility.retryUntilValid(roomView::promptRoomName, room::setRoomName, roomView::showError, "0")) {
                                        roomView.showMessage("Room name updated.");
                                    }
                                    break;
                                case 2:
                                    if (LoopUtility.retryUntilValid(roomView::promptRows, room::setRows, roomView::showError, "0")) {
                                        roomView.showMessage("Rows updated.");
                                    }
                                    break;
                                case 3:
                                    if (LoopUtility.retryUntilValid(roomView::promptSeatsPerRow, room::setSeatsPerRow, roomView::showError, "0")) {
                                        roomView.showMessage("Seats per row updated.");
                                    }
                                    break;
                                case 4:
                                    room.setStatus(roomView.confirm("Open the room for use?"));
                                    roomView.showMessage("Room status updated.");
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    }
                    case 3: {
                        // Remove room (by ID, 0 to go back). The ID is checked first (findRoom throws
                        // if it does not exist), and the confirmation is only asked for an existing room.
                        roomView.displayRooms(cinema.getCinemaRooms());
                        Boolean removed = LoopUtility.retryUntilValidResult(roomView::promptRoomID,
                                id -> {
                                    cinema.findRoom(id);   // throws if the room does not exist
                                    return roomView.confirm("Remove room " + id.trim() + "?") && cinema.removeRoom(id);
                                },
                                roomView::showError, "0");
                        if (removed != null && removed) {
                            roomView.showMessage("Room was removed.");
                        }
                        break;
                    }
                    case 4: {
                        // Generate seat layout (found by ID, 0 to go back)
                        roomView.displayRooms(cinema.getCinemaRooms());
                        CinemaRoom room = LoopUtility.retryUntilValidResult(roomView::promptRoomID,
                                cinema::findRoom, roomView::showError, "0");
                        if (room != null && roomView.confirm("This replaces any existing seats in " + room.getRoomID() + ". Continue?")) {
                            room.generateSeatLayout();
                            roomView.showMessage("Seat layout generated: " + room.getSeats().size() + " seats.");
                        }
                        break;
                    }
                    case 5:
                        roomView.displayRooms(cinema.getCinemaRooms());
                        break;
                    default:
                        break;   // 0 exits the loop
                }
            } catch (IllegalArgumentException e) {
                roomView.showError(e.getMessage());
            }
        }
    }

    public void manageSeats() {
        Cinema cinema;
        CinemaRoom room;
        try {
            cinemaView.displayCinemas(cinemaRepo.viewCinemas());   // throws if there are no cinemas
            cinema = LoopUtility.retryUntilValidResult(cinemaView::promptCinemaID,
                    cinemaRepo::findCinemaByID, cinemaView::showError, "0");
            if (cinema == null) {
                return;
            }
            if (cinema.getCinemaRooms().size() == 0) {
                throw new IllegalArgumentException("Invalid input. This cinema has no rooms yet.");
            }
            roomView.displayRooms(cinema.getCinemaRooms());
            room = LoopUtility.retryUntilValidResult(roomView::promptRoomID,
                    cinema::findRoom, roomView::showError, "0");
            if (room == null) {
                return;
            }
        } catch (IllegalArgumentException e) {
            cinemaView.showError(e.getMessage());
            return;
        }
 
        while (LoopUtility.retryUntilValid(seatView::showSeatMenu, choice -> {
            String option = choice.trim();
            // Everything except "Add seat" needs at least one seat in the room.
            if (room.getSeats().size() == 0 && (option.equals("1") || option.equals("3") || option.equals("4")
                    || option.equals("5") || option.equals("6"))) {
                throw new IllegalArgumentException("Invalid input. Room " + room.getRoomID() + " has no seats yet.");
            }
            try {
                switch (choice.trim()) {
                    case "1":
                        seatView.displayRoomLayout(room);
                        break;
                    case "2":
                        if (LoopUtility.retryUntilValid(seatView::promptSeatID,
                                id -> room.addSeat(new Seat(id)), seatView::showError, "0")) {
                            seatView.showMessage("Seat added.");
                        }
                        break;
                    case "3": {
                        // Remove seat: the ID is checked first (bad format or not found),
                        // and the confirmation is only asked for an existing seat.
                        seatView.displayRoomLayout(room);
                        Boolean removed = LoopUtility.retryUntilValidResult(seatView::promptSeatID,
                                id -> {
                                    Seat target = room.findSeat(id);   // throws for a blank or badly formatted ID
                                    if (target == null) {
                                        throw new IllegalArgumentException("Invalid input. Seat " + id.trim() + " was not found in this room.");
                                    }
                                    return seatView.confirm("Remove seat " + target.getSeatID() + "?") && room.removeSeat(id);
                                },
                                seatView::showError, "0");
                        if (removed != null && removed) {
                            seatView.showMessage("Seat removed.");
                        }
                        break;
                    }
                    case "4": {
                        // Edit seat: show every seat first, then ask for the ID.
                        // An ID that is not in the room shows an error instead of silently going back.
                        seatView.displaySeats(room.getSeats());
                        Seat seat = LoopUtility.retryUntilValidResult(seatView::promptSeatID,
                                id -> {
                                    Seat found = room.findSeat(id);
                                    if (found == null) {
                                        throw new IllegalArgumentException("Invalid input. Seat " + id.trim() + " was not found in this room.");
                                    }
                                    return found;
                                }, seatView::showError, "0");
                        if (seat != null) {
                            while (LoopUtility.retryUntilValid(() -> seatView.showEditSeatMenu(seat), editChoice -> {
                                switch (editChoice.trim()) {
                                    case "1":
                                        if (LoopUtility.retryUntilValid(seatView::promptSeatType,
                                                seat::setSeatType, seatView::showError, "0")) {
                                            seatView.showMessage("Seat type updated.");
                                        }
                                        break;
                                    case "2":
                                        seat.setStatus(seatView.confirm("Is this seat available? (No = unavailable)"));
                                        seatView.showMessage("Seat status updated.");
                                        break;
                                    case "3":
                                        if (LoopUtility.retryUntilValid(seatView::promptPriceModifier,
                                                seat::setPriceModifier, seatView::showError, "0")) {
                                            seatView.showMessage("Seat price modifier updated.");
                                        }
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Invalid input. Choose a number from 0 to 3.");
                                }
                            }, seatView::showError, "0")) {
                                // edit menu repeats until the user types 0
                            }
                        }
                        break;
                    }
                    case "5": {
                        while (LoopUtility.retryUntilValid(seatView::showSeatFilterMenu, filterChoice -> {
                            switch (filterChoice.trim()) {
                                case "1": {
                                    LinkedList<Seat> found = LoopUtility.retryUntilValidResult(seatView::promptSearchValue,
                                            value -> room.filterSeat("ROW", value), seatView::showError, "0");
                                    if (found != null) {
                                        seatView.displaySeats(found);
                                    }
                                    break;
                                }
                                case "2": {
                                    LinkedList<Seat> found = LoopUtility.retryUntilValidResult(seatView::promptSearchValue,
                                            value -> room.filterSeat("TYPE", value), seatView::showError, "0");
                                    if (found != null) {
                                        seatView.displaySeats(found);
                                    }
                                    break;
                                }
                                case "3": {
                                    LinkedList<Seat> found = LoopUtility.retryUntilValidResult(seatView::promptSearchValue,
                                            value -> room.filterSeat("STATUS", value), seatView::showError, "0");
                                    if (found != null) {
                                        seatView.displaySeats(found);
                                    }
                                    break;
                                }
                                default:
                                    throw new IllegalArgumentException("Invalid input. Choose a number from 0 to 3.");
                            }
                        }, seatView::showError, "0")) {
                            // filter menu repeats until the user types 0
                        }
                        break;
                    }
                    case "6": {
                        // Add or remove an aisle after a seat number, through every row (the seat IDs do not change).
                        seatView.displayRoomLayout(room);
                        String result = LoopUtility.retryUntilValidResult(seatView::promptSeatID,
                                id -> {
                                    Seat target = room.findSeat(id);   // throws for a blank or badly formatted ID
                                    if (target == null) {
                                        throw new IllegalArgumentException("Invalid input. Seat " + id.trim() + " was not found in this room.");
                                    }
                                    // An aisle is one line through the room, so check the whole column (seat number).
                                    boolean columnHasAisle = false;
                                    Node<Seat> n = room.getSeats().getHead();
                                    while (n != null) {
                                        if (n.getData().getSeatNumber() == target.getSeatNumber() && n.getData().hasAisleAfter()) {
                                            columnHasAisle = true;
                                        }
                                        n = n.getNext();
                                    }
                                    if (columnHasAisle) {
                                        if (!seatView.confirm("Remove the aisle after seat number " + target.getSeatNumber() + "?")) {
                                            return "";
                                        }
                                        n = room.getSeats().getHead();
                                        while (n != null) {
                                            if (n.getData().getSeatNumber() == target.getSeatNumber()) {
                                                n.getData().setAisleAfter(false);
                                            }
                                            n = n.getNext();
                                        }
                                        return "Aisle removed.";
                                    }
                                    if (!seatView.confirm("Put an aisle after seat number " + target.getSeatNumber() + "?")) {
                                        return "";
                                    }
                                    room.addAisle(id);
                                    return "Aisle added.";
                                },
                                seatView::showError, "0");
                        if (result != null && !result.isEmpty()) {
                            seatView.showMessage(result);
                        }
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Invalid input. Choose a number from 0 to 6.");
                }
            } catch (IllegalStateException e) {
                seatView.showError(e.getMessage());
            }
        }, seatView::showError, "0")) {
            // seat menu repeats until the user types 0
        }
    }

    public void manageSchedules() {
        Cinema cinema;
        CinemaRoom room;
        try {
            cinemaView.displayCinemas(cinemaRepo.viewCinemas());
            cinema = LoopUtility.retryUntilValidResult(cinemaView::promptCinemaID, cinemaRepo::findCinemaByID,
                    cinemaView::showError, "0");
            if (cinema == null) {
                return;
            }
            if (cinema.getCinemaRooms().size() == 0) {
                throw new IllegalArgumentException("Invalid input. This cinema has no rooms yet.");
            }
            roomView.displayRooms(cinema.getCinemaRooms());
            room = LoopUtility.retryUntilValidResult(roomView::promptRoomID, cinema::findRoom,
                    roomView::showError, "0");
            if (room == null) {
                return;
            }
        } catch (IllegalArgumentException e) {
            cinemaView.showError(e.getMessage());
            return;
        }

        boolean running = true;
        while (running) {
            String choice = scheduleView.showScheduleMenu();
            try {
                // Everything except "Add schedule" needs at least one schedule in the room.
                if (room.getSchedules().isEmpty() && (choice.equals("1") || choice.equals("3")
                        || choice.equals("4") || choice.equals("5"))) {
                    throw new IllegalArgumentException("Invalid input. Room " + room.getRoomID() + " has no schedules yet.");
                }
                switch (choice) {
                    case "1":
                        scheduleView.displaySchedules(room.getSchedules());
                        break;
                    case "2": {
                        movieListView.displayMovieList(movieRepo.viewMovies());   // throws if there are no movies to schedule
                        if (!room.getSchedules().isEmpty()) {
                            scheduleView.displaySchedules(room.getSchedules());   // schedules already set in this room
                        }
                    Schedule schedule = new Schedule(room.generateScheduleID());
                    System.out.println("----------------------------------------");
                    Movie movie = LoopUtility.retryUntilValidResult(scheduleView::promptMovieID,
                            id -> {
                                if (!InputValidator.isValidMovieID(id)) {
                                    throw new IllegalArgumentException("Invalid input. Movie ID format MOV001.");
                                }
                                Movie match = movieRepo.findByID(id);
                                if (match == null) {
                                    throw new IllegalArgumentException("Invalid input. Movie " + id.trim().toUpperCase() + " was not found.");
                                }
                                return match;
                            },
                            scheduleView::showError, "0");
                    if (movie != null) {
                        schedule.setMovie(movie);
                        schedule.setRoom(room);
                        String[] showDate = new String[1];   // holds the valid date so the time step can combine with it
                        if (LoopUtility.retryUntilValid(scheduleView::promptPrice, schedule::setPrice,
                                scheduleView::showError, "0")
                                && LoopUtility.retryUntilValid(scheduleView::promptShowDate,
                                d -> {
                                    if (!InputValidator.isValidDate(d)) {
                                        throw new IllegalArgumentException("Invalid input. Use a real date as yyyy-MM-dd.");
                                    }
                                    if (movie.getReleaseDate() == null) {
                                        throw new IllegalArgumentException("Invalid input. " + movie.getTitle() + " has no release date yet, so it cannot be scheduled.");
                                    }
                                    if (java.time.LocalDate.parse(d.trim()).isBefore(movie.getReleaseDate())) {
                                        throw new IllegalArgumentException("Invalid input. " + movie.getTitle() + " releases on "
                                                + movie.getReleaseDate() + ". Choose that date or later.");
                                    }
                                    showDate[0] = d.trim();
                                }, scheduleView::showError, "0")
                                && LoopUtility.retryUntilValid(scheduleView::promptShowTime,
                                t -> {
                                    if (!t.trim().matches("([01][0-9]|2[0-3]):[0-5][0-9]")) {
                                        throw new IllegalArgumentException("Invalid input. Use a real time as HH:mm, for example 14:30.");
                                    }
                                    schedule.setShowDateTime(showDate[0] + " " + t.trim());
                                }, scheduleView::showError, "0")) {
                            room.addSchedule(schedule);
                            scheduleView.showMessage("Schedule " + schedule.getScheduleID()
                                    + " added to room " + room.getRoomID() + ".");
                        }
                    }
                    break;
                }
                    case "3": {
                        scheduleView.displaySchedules(room.getSchedules());
                        Boolean removed = LoopUtility.retryUntilValidResult(scheduleView::promptScheduleID,
                                id -> scheduleView.confirm("Remove schedule " + id.trim() + "?")
                                        && room.removeSchedule(id),
                                scheduleView::showError, "0");
                        if (removed != null) {
                            scheduleView.showMessage(removed ? "Schedule removed." : "Removal cancelled.");
                        }
                        break;
                    }
                    case "4": {
                        scheduleView.displaySchedules(room.getSchedules());
                        Schedule schedule = LoopUtility.retryUntilValidResult(scheduleView::promptScheduleID,
                                id -> {
                                    Schedule found = room.findSchedule(id);
                                    if (found == null) {
                                        throw new IllegalArgumentException("Invalid input. Schedule " + id.trim()
                                                + " was not found in room " + room.getRoomID() + ".");
                                    }
                                    return found;
                                }, scheduleView::showError, "0");
                        boolean editing = schedule != null;
                        while (editing) {
                            switch (scheduleView.showEditScheduleMenu(schedule)) {
                                case "1":
                                    if (LoopUtility.retryUntilValid(scheduleView::promptPrice, schedule::setPrice,
                                            scheduleView::showError, "0")) {
                                        scheduleView.showMessage("Price updated.");
                                    }
                                    break;
                                case "2":
                                    schedule.setActive(!schedule.isActive());
                                    scheduleView.showMessage("Schedule " + schedule.getScheduleID() + " is now "
                                            + (schedule.isActive() ? "open for booking." : "closed for booking."));
                                    break;
                                case "0":
                                    editing = false;
                                    break;
                                default:
                                    scheduleView.showError("Invalid choice. Enter a number from 0 to 2.");
                                    break;
                            }
                        }
                        break;
                    }
                    case "5": {
                        String category = null;
                        switch (scheduleView.showScheduleFilterMenu()) {
                            case "1":
                                category = "MOVIE";
                                break;
                            case "2":
                                category = "DATE";
                                break;
                            case "3":
                                category = "ACTIVE";
                                break;
                            case "0":
                                break;
                            default:
                                scheduleView.showError("Invalid choice. Enter a number from 0 to 3.");
                                break;
                        }
                        if (category != null) {
                            final String filterCategory = category;
                            LinkedList<Schedule> found = LoopUtility.retryUntilValidResult(
                                    scheduleView::promptSearchValue,
                                    value -> room.filterSchedule(filterCategory, value),
                                    scheduleView::showError, "0");
                            if (found != null) {
                                scheduleView.displaySchedules(found);
                            }
                        }
                        break;
                    }
                    case "0":
                        running = false;
                        break;
                    default:
                        scheduleView.showError("Invalid choice. Enter a number from 0 to 5.");
                        break;
                }
            } catch (IllegalArgumentException e) {
                scheduleView.showError(e.getMessage());
            }
        }
    }
}
