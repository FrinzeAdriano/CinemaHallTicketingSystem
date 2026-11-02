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

    public CinemaManagementController(CinemaRepository cinemaRepo, MovieRepository movieRepo,
            CinemaView cinemaView, CinemaRoomListView roomView, SeatView seatView, ScheduleView scheduleView) {
        this.cinemaRepo = cinemaRepo;
        this.movieRepo = movieRepo;
        this.cinemaView = cinemaView;
        this.roomView = roomView;
        this.seatView = seatView;
        this.scheduleView = scheduleView;
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
                        // Edit cinema (found by name, 0 to go back)
                        cinemaRepo.viewCinemas();   // throws if there are no cinemas
                        Cinema cinema = LoopUtility.retryUntilValidResult(cinemaView::promptCinemaName,
                                cinemaRepo::findCinema, cinemaView::showError, "0");
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
        // Pick the cinema first (0 to go back)
        cinemaView.displayCinemas(cinemaRepo.viewCinemas());   // throws if there are no cinemas
        Cinema cinema = LoopUtility.retryUntilValidResult(cinemaView::promptCinemaName,
                cinemaRepo::findCinema, cinemaView::showError, "0");
        if (cinema == null) {
            return;
        }
 
        int choice = -1;
        while (choice != 0) {
            choice = roomView.showRoomMenu();
            try {
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
        Cinema cinema = LoopUtility.retryUntilValidResult(cinemaView::promptCinemaName,
                cinemaRepo::findCinema, cinemaView::showError, "0");
        if (cinema == null) {
            return;
        }
        CinemaRoom room = LoopUtility.retryUntilValidResult(roomView::promptRoomID,
                cinema::findRoom, roomView::showError, "0");
        if (room == null) {
            return;
        }

        while (LoopUtility.retryUntilValid(seatView::showSeatMenu, choice -> {
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
                        seatView.displayRoomLayout(room);
                        Boolean removed = LoopUtility.retryUntilValidResult(seatView::promptSeatID,
                                id -> seatView.confirm("Remove seat " + id.trim() + "?") && room.removeSeat(id),
                                seatView::showError, "0");
                        if (removed != null && removed) {
                            seatView.showMessage("Seat removed.");
                        }
                        break;
                    }
                    case "4": {
                        Seat seat = LoopUtility.retryUntilValidResult(seatView::promptSeatID,
                                room::findSeat, seatView::showError, "0");
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
                                        Node<Seat> node = found.getHead();
                                        while (node != null) {
                                            seatView.displaySeat(node.getData());
                                            node = node.getNext();
                                        }
                                    }
                                    break;
                                }
                                case "2": {
                                    LinkedList<Seat> found = LoopUtility.retryUntilValidResult(seatView::promptSearchValue,
                                            value -> room.filterSeat("TYPE", value), seatView::showError, "0");
                                    if (found != null) {
                                        Node<Seat> node = found.getHead();
                                        while (node != null) {
                                            seatView.displaySeat(node.getData());
                                            node = node.getNext();
                                        }
                                    }
                                    break;
                                }
                                case "3": {
                                    LinkedList<Seat> found = LoopUtility.retryUntilValidResult(seatView::promptSearchValue,
                                            value -> room.filterSeat("STATUS", value), seatView::showError, "0");
                                    if (found != null) {
                                        Node<Seat> node = found.getHead();
                                        while (node != null) {
                                            seatView.displaySeat(node.getData());
                                            node = node.getNext();
                                        }
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
                    default:
                        throw new IllegalArgumentException("Invalid input. Choose a number from 0 to 5.");
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
            cinema = LoopUtility.retryUntilValidResult(cinemaView::promptCinemaID, cinemaRepo::findCinema,
                    cinemaView::showError, "0");
            if (cinema == null) {
                return;
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
                switch (choice) {
                    case "1":
                        scheduleView.displaySchedules(room.getSchedules());
                        break;
                    case "2": {
                        Schedule schedule = new Schedule(room.generateScheduleID());
                        Movie movie = LoopUtility.retryUntilValidResult(scheduleView::promptMovieID,
                                movieRepo::findMovie, scheduleView::showError, "0");
                        if (movie != null) {
                            schedule.setMovie(movie);
                            schedule.setRoom(room);
                            if (LoopUtility.retryUntilValid(scheduleView::promptPrice, schedule::setPrice,
                                    scheduleView::showError, "0")
                                    && LoopUtility.retryUntilValid(scheduleView::promptShowDateTime,
                                    schedule::setShowDateTime, scheduleView::showError, "0")) {
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
