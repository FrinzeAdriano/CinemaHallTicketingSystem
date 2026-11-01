package controllers;

import datastructures.*;
import models.*;
import repositories.*;
import view.*;
import utility.*;
import service.*;

public class AdminController {

    private final UserRepository userRepo;
    private final MovieRepository movieRepo;
    private final CinemaRepository cinemaRepo;
    private final InventoryRepository inventoryRepo;
    private final TransactionHistory history;
    private final SalesRepository salesRepo;
    private final VoucherRepository voucherRepo;
    private final CheckoutService checkoutService;

    private final UserView userView;
    private final MovieView movieView;
    private final CinemaView cinemaView;
    private final CinemaRoomListView roomView;
    private final SeatView seatView;
    private final ScheduleView scheduleView;
    private final InventoryView inventoryView;
    private final TransactionHistoryView historyView;
    private final ReceiptView receiptView;
    private final SalesView salesView;
    private final VoucherView voucherView;

    // Constructor: receives the shared repositories and service from
    // CinemaTicketManagementSystem so every controller works on the same data.
    // Views hold no data, so this controller creates its own.
    public AdminController(UserRepository userRepo, MovieRepository movieRepo,
                           CinemaRepository cinemaRepo, InventoryRepository inventoryRepo,
                           TransactionHistory history, SalesRepository salesRepo,
                           VoucherRepository voucherRepo, CheckoutService checkoutService) {
        this.userRepo = userRepo;
        this.movieRepo = movieRepo;
        this.cinemaRepo = cinemaRepo;
        this.inventoryRepo = inventoryRepo;
        this.history = history;
        this.salesRepo = salesRepo;
        this.voucherRepo = voucherRepo;
        this.checkoutService = checkoutService;

        this.userView = new UserView();
        this.movieView = new MovieView();
        this.cinemaView = new CinemaView();
        this.roomView = new CinemaRoomListView();
        this.seatView = new SeatView();
        this.scheduleView = new ScheduleView();
        this.inventoryView = new InventoryView();
        this.historyView = new TransactionHistoryView();
        this.receiptView = new ReceiptView();
        this.salesView = new SalesView();
        this.voucherView = new VoucherView();
    }
 
    // Admin Methods

    public void start(Admin admin) {

    }

    public void manageProfile(Admin admin) {

    }

    public void editProfile(Admin admin) {

    }

    public void changePassword(Admin admin) {

    }


    // User Management Methods

    public void manageUsers() {

    }


    // Movie Management Methods

    public void manageMovies() {

    }


    // Cinema Management Methods

    public void manageCinemas() {

    }

    public void manageRooms(Cinema cinema) {
        
    }

    public void manageSeats() {

    }

    public void manageSchedules() {

    }


    // Inventory Management Methods

    public void manageInventory() {

    }

    public void addProduct() {

    }

    public void editProduct() {

    }

    public void restockProduct() {

    }

    public void deactivateProduct() {

    }

    public void viewLowStock() {

    }


    // Voucher Management Methods

    public void manageVouchers() {

    }


    // Transaction Management Methods

    public void manageTransactionHistory() {

    }

    public void reprintReceipt() {

    }

    public void voidTransaction() {

    }


    // Sales Methods

    public void viewSalesReports() {

    }

}
