package controllers;
import datastructures.*;
import models.*;
import repositories.*;
import view.*;
import utility.*;
import service.*;
import java.time.*;

public class CustomerController {

    private UserRepository userRepo;
    private MovieRepository movieRepo;
    private CinemaRepository cinemaRepo;
    private TransactionHistory history;
    private VoucherRepository voucherRepo;
    private CheckoutService checkoutService;

    private UserView userView;
    private MovieRepositoryView movieListView;
    private ScheduleView scheduleView;
    private SeatView seatView;
    private TicketView ticketView;
    private TransactionHistoryView historyView;
    private ReceiptView receiptView;

    // Customer Methods

    public void start(Customer customer) {

    }

    public void manageProfile(Customer customer) {

    }

    public void editProfile(Customer customer) {

    }

    public void changePassword(Customer customer) {

    }


    // Registration Method

    public void register() {

    }


    // Movie Browsing Method

    public void browseMovies() {

    }


    // Books tickets for an online customer
    public void bookTickets(Customer customer) {

        LinkedList schedules =
            cinemaRepo.getAllSchedules();

        if (schedules == null || schedules.size() == 0) {
            ticketView.showMessage("No available movie schedules.");
            return;
        }

        Schedule schedule = scheduleView.selectSchedule(schedules);

        if (schedule == null) {
            return;
        }

        // Gets the cinema room
        CinemaRoom room = schedule.getRoom();

        LinkedList seats = room.getSeats();

        LinkedList bookedSeatIDs =
            schedule.getBookedSeatIDs();


        // Counts available seats
        int availableSeats = 0;

        for (int i = 0; i < seats.size(); i++) {

            Seat seat = (Seat) seats.get(i);

            if (!bookedSeatIDs.contains(seat.getSeatID())) {
                availableSeats++;
            }
        }

        if (availableSeats == 0) {
            ticketView.showMessage("No available seats.");
            return;
        }


        // Displays the seat layout
        seatView.displaySeatGrid(schedule);

        int ticketCount = ticketView.promptTicketCount();

        if (ticketCount > availableSeats) {
            ticketView.showError(
                "Ticket quantity exceeds the available seats."
            );
            return;
        }


        LinkedList selectedTickets = new LinkedList();

        int ticketNumber = 1;


        // Selects seats
        while (ticketNumber <= ticketCount) {

            String seatID = seatView.promptSeatID();

            if (!InputValidator.isValidSeatID(seatID)) {
                seatView.showError("Invalid seat ID.");
                continue;
            }

            Seat selectedSeat = null;

            for (int i = 0; i < seats.size(); i++) {

                Seat seat = (Seat) seats.get(i);

                if (seat.getSeatID().equalsIgnoreCase(seatID)) {
                    selectedSeat = seat;
                    break;
                }
            }

            if (selectedSeat == null) {
                seatView.showError("Seat does not exist.");
                continue;
            }

            if (bookedSeatIDs.contains(selectedSeat.getSeatID())) {
                seatView.showError("Seat is already booked.");
                continue;
            }


            // Checks for duplicate seat selection
            boolean alreadySelected = false;

            for (int i = 0; i < selectedTickets.size(); i++) {

                Ticket selectedTicket =
                    (Ticket) selectedTickets.get(i);

                if (selectedTicket.getSeat().getSeatID()
                        .equalsIgnoreCase(selectedSeat.getSeatID())) {

                    alreadySelected = true;
                    break;
                }
            }

            if (alreadySelected) {
                seatView.showError("You already selected that seat.");
                continue;
            }


            // Calculates ticket price
            double ticketPrice =
                schedule.getPrice() + selectedSeat.getPriceModifier();

            String ticketID =
                history.generateTransactionID() + "-T" + ticketNumber;

            Ticket ticket = new Ticket(
                ticketID,
                schedule,
                selectedSeat,
                ticketPrice,
                "PENDING"
            );

            selectedTickets.insertAtEnd(ticket);

            ticketNumber++;
        }


        // Creates the transaction
        String transactionID = history.generateTransactionID();

        String bookingCode = "BK-" + transactionID;

        Transaction transaction = new Transaction(
            transactionID,
            "ONLINE",
            customer.getUserID(),
            null,
            LocalDateTime.now(),
            selectedTickets,
            new LinkedList(),
            null,
            0,
            0,
            null,
            0,
            null,
            0,
            "PENDING",
            bookingCode,
            null,
            null
        );


        // Stores the pending online booking
        history.addTransaction(transaction);


        // Reserves selected seats for this schedule
        for (int i = 0; i < selectedTickets.size(); i++) {

            Ticket ticket = (Ticket) selectedTickets.get(i);

            bookedSeatIDs.insertAtEnd(
                ticket.getSeat().getSeatID()
            );
        }

        ticketView.showMessage("Booking successfully created.");
        ticketView.showMessage("Booking Code: " + bookingCode);

        ticketView.displayTickets(selectedTickets);
    }

    // Cancels a customer's booking
    public void cancelBooking(Customer customer) {

        String bookingCode =
            historyView.readString("Enter booking code: ");

        Transaction transaction = null;

        LinkedList transactions =
            history.viewTransactionHistory();

        for (int i = 0; i < transactions.size(); i++) {

            Transaction current =
                (Transaction) transactions.get(i);

            if (current.getBookingCode() != null
                    && current.getBookingCode()
                        .equalsIgnoreCase(bookingCode)) {

                transaction = current;
                break;
            }
        }

        if (transaction == null) {
            historyView.showError("Booking not found.");
            return;
        }

        if (transaction.getCustomerID() == null
                || !transaction.getCustomerID()
                    .equalsIgnoreCase(customer.getUserID())) {

            historyView.showError(
                "You cannot cancel another customer's booking."
            );

            return;
        }

        if (historyView.confirm(
                "Are you sure you want to cancel this booking?")) {

            boolean cancelled =
                checkoutService.cancelTransaction(transaction);

            if (cancelled) {
                historyView.showMessage(
                    "Booking successfully cancelled."
                );
            } else {
                historyView.showError(
                    "Unable to cancel booking."
                );
            }
        }
    }


     // Displays the customer's transaction history
    public void viewMyHistory(Customer customer) {

        LinkedList transactions =
            history.getByCustomer(customer.getUserID());

        historyView.displayTransactions(transactions);
    }
}
