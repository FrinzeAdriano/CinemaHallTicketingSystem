package controllers;

import models.Cashier;
import models.Customer;
import models.Transaction;
import repositories.CinemaRepository;
import repositories.InventoryRepository;
import repositories.MovieRepository;
import repositories.TransactionHistory;
import repositories.VoucherRepository;
import service.CheckoutService;
import view.InventoryView;
import view.MovieRepositoryView;
import view.ReceiptView;
import view.ScheduleView;
import view.SeatView;
import view.TicketView;

public class BookingController {
    private MovieRepository movieRepo;
    private CinemaRepository cinemaRepo;
    private InventoryRepository inventoryRepo;
    private TransactionHistory history;
    private VoucherRepository voucherRepo;
    private CheckoutService checkoutService;
    private MovieRepositoryView movieListView;
    private ScheduleView scheduleView;
    private SeatView seatView;
    private TicketView ticketView;
    private InventoryView inventoryView;
    private ReceiptView receiptView;

    public BookingController(MovieRepository movieRepo, CinemaRepository cinemaRepo,
            InventoryRepository inventoryRepo, TransactionHistory history, VoucherRepository voucherRepo,
            CheckoutService checkoutService, MovieRepositoryView movieListView, ScheduleView scheduleView,
            SeatView seatView, TicketView ticketView, InventoryView inventoryView, ReceiptView receiptView) {
        this.movieRepo = movieRepo;
        this.cinemaRepo = cinemaRepo;
        this.inventoryRepo = inventoryRepo;
        this.history = history;
        this.voucherRepo = voucherRepo;
        this.checkoutService = checkoutService;
        this.movieListView = movieListView;
        this.scheduleView = scheduleView;
        this.seatView = seatView;
        this.ticketView = ticketView;
        this.inventoryView = inventoryView;
        this.receiptView = receiptView;
    }

    // Cashier
    public void sellTickets(Cashier cashier) {
    }

    public void addProductsToOrder(Transaction transaction) {
    }

    // Customer
    public void bookTickets(Customer customer) {
    }

    public void cancelBooking(Customer customer) {
    }
}
