package controllers;
import datastructures.*;
import models.*;
import repositories.*;
import view.*;
import utility.*;
import service.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CinemaTicketManagementSystem {

    private UserRepository userRepo;
    private MovieRepository movieRepo;
    private CinemaRepository cinemaRepo;
    private InventoryRepository inventoryRepo;
    private TransactionHistory history;
    private SalesRepository salesRepo;
    private VoucherRepository voucherRepo;
    private CheckoutService checkoutService;
    private AdminController adminController;
    private CustomerController customerController;
    private CinemaTicketManagementSystemView view;
    private User currentUser;

 
    // System Methods

    public void start() {

    }

    public boolean login(String username, String password) {
         return false;}

    public boolean logout() {
         return false;}

    public void route(User user) {

    }

    public void loadSampleData() {

    }

}

