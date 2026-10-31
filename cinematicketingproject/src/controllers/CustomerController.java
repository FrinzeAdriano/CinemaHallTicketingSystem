package controllers;
import datastructures.*;
import models.*;
import repositories.*;
import view.*;
import utility.*;
import service.*;
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


    // Booking Methods

    public void bookTickets(Customer customer) {

    }

    public void cancelBooking(Customer customer) {

    }


    // Transaction History Method

    public void viewMyHistory(Customer customer) {

    }

}
