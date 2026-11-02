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
                        // Remove cinema (by ID, 0 to go back, confirmation before deleting)
                        cinemaView.displayCinemas(cinemaRepo.viewCinemas());   // throws if there are no cinemas
                        Boolean removed = LoopUtility.retryUntilValidResult(cinemaView::promptCinemaID,
                                id -> cinemaView.confirm("Remove cinema " + id.trim() + "?") && cinemaRepo.removeCinema(id),
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
                        // Add room (0 at any prompt goes back without adding)
                        CinemaRoom room = LoopUtility.retryUntilValidResult(roomView::promptRoomID,
                                CinemaRoom::new, roomView::showError, "0");
                        if (room != null
                                && LoopUtility.retryUntilValid(roomView::promptRoomName, room::setRoomName, roomView::showError, "0")
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
                        // Remove room (by ID, 0 to go back, confirmation before deleting)
                        roomView.displayRooms(cinema.getCinemaRooms());
                        Boolean removed = LoopUtility.retryUntilValidResult(roomView::promptRoomID,
                                id -> roomView.confirm("Remove room " + id.trim() + "?") && cinema.removeRoom(id),
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
    }

    public void manageSchedules() {
    }
}
