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
    private CashierController cashierController;
    private CustomerController customerController;
    private CinemaTicketManagementSystemView view;
    private User currentUser;

 
    // System Methods

    // Program entry point
    public static void main(String[] args) {
        new CinemaTicketManagementSystem().start();
    }
    
        public void start() {
        loadSampleData();
 
        boolean running = true;
        while (running) {
            int choice = view.showMainMenu();
            if (choice == 1) {
                String username = view.promptUsername();
                String password = view.promptPassword();
                if (login(username, password)) {
                    route(currentUser);
                    logout();
                }
            } else if (choice == 2) {
                customerController.register();
            } else {
                view.showMessage("Thank you for using the Cinema Ticket Management System. Goodbye!");
                running = false;
            }
        }
    }

    // Prints its own error message when it fails.
    public boolean login(String username, String password) {
        User user = null;
        Node n = userRepo.viewUsers().getHead();
        while (n != null) {
            User candidate = (User) n.getData();
            if (candidate.getUserName().equalsIgnoreCase(username)) {
                user = candidate;
                break;
            }
            n = n.getNext();
        }
        if (user == null || !user.getPassword().equals(password)) {
            view.showError("Invalid username or password.");
            return false;
        }
        if (!user.getStatus().equalsIgnoreCase("Active")) {
            view.showError("This account has been deactivated. Please contact an administrator.");
            return false;
        }
        currentUser = user;
        view.showMessage("Welcome, " + user.getFullName() + " (" + user.getRole() + ")!");
        return true;}

    public boolean logout() {
        if (currentUser == null) {
            return false;
        }
        view.showMessage("Goodbye, " + currentUser.getFullName() + ". You have been logged out.");
        currentUser = null;
        return true;
    }

    public void route(User user) {
        if (user instanceof Admin) {
            adminController.start((Admin) user);
        } else if (user instanceof Cashier) {
            cashierController.start((Cashier) user);
        } else if (user instanceof Customer) {
            customerController.start((Customer) user);
        } else {
            view.showError("Unknown user role.");
        }
    }

    // Demo data so the system is usable on first run.
    // Logins:  admin / admin123    cashier1 / cash123    customer1 / cust123
    // Demo data so the system is usable on first run.
    // Logins:  admin / admin123    cashier1 / cash123    customer1 / cust123
    public void loadSampleData() {
 
        // Users
        userRepo.addUser(new Admin(userRepo.generateUserID("Admin"), "admin", "admin123",
                "System Admin", "Active"));
        userRepo.addUser(new Cashier(userRepo.generateUserID("Cashier"), "cashier1", "cash123",
                "Sam Cashier", "Active", "Morning"));
        userRepo.addUser(new Customer(userRepo.generateUserID("Customer"), "customer1", "cust123",
                "Chris Customer", "Active", "Regular", LocalDate.now().toString()));
 
        // Movies (release dates are relative to today so the data never goes stale)
        Movie starfall = new Movie(movieRepo.generateMovieID(), "Starfall Odyssey", "Sci-Fi", 128, "PG-13",
                "A rescue crew races across a dying galaxy.", LocalDate.now().minusDays(14));
        movieRepo.addMovie(starfall);
 
        Movie lighthouse = new Movie(movieRepo.generateMovieID(), "The Last Lighthouse", "Drama", 112, "PG",
                "A keeper and a stranded sailor share one stormy winter.", LocalDate.now().minusDays(30));
        movieRepo.addMovie(lighthouse);
 
        Movie laughTrack = new Movie(movieRepo.generateMovieID(), "Laugh Track", "Comedy", 98, "PG",
                "Two rival comedians are stuck on the same tour bus.", LocalDate.now().minusDays(7));
        movieRepo.addMovie(laughTrack);
 
        Movie echoes = new Movie(movieRepo.generateMovieID(), "Echoes Below", "Horror", 104, "R-16",
                "Something answers back from the old mine.", LocalDate.now().minusDays(3));
        movieRepo.addMovie(echoes);
 
        movieRepo.addMovie(new Movie(movieRepo.generateMovieID(), "Neon Horizon", "Action", 119, "PG-13",
                "A courier outruns a city that never sleeps.", LocalDate.now().plusDays(30)));
 
        // Cinema, rooms and schedules
        Cinema cinema = new Cinema(cinemaRepo.generateCinemaID(), "Grand Cinema", "Downtown Mall", new LinkedList());
 
        CinemaRoom room1 = new CinemaRoom("R001", "Cinema 1", 8, 10, true, cinema, new LinkedList(), new Queue());
        room1.generateSeatLayout();
        cinema.addRoom(room1);
 
        CinemaRoom room2 = new CinemaRoom("R002", "Cinema 2", 6, 8, true, cinema, new LinkedList(), new Queue());
        room2.generateSeatLayout();
        cinema.addRoom(room2);
 
        cinemaRepo.addCinema(cinema);
 
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        room1.addSchedule(new Schedule("SCH001", starfall, room1, tomorrow.plusHours(13), 250.0, new LinkedList(), true));
        room1.addSchedule(new Schedule("SCH002", lighthouse, room1, tomorrow.plusHours(17), 250.0, new LinkedList(), true));
        room2.addSchedule(new Schedule("SCH003", laughTrack, room2, tomorrow.plusHours(15), 220.0, new LinkedList(), true));
        room2.addSchedule(new Schedule("SCH004", echoes, room2, tomorrow.plusHours(20), 220.0, new LinkedList(), true));
 
        // Inventory (Soda is deliberately low on stock so the low-stock report has something to show)
        inventoryRepo.addProduct(new Food(inventoryRepo.generateProductID(), "Popcorn (Large)", 180.0, 100,
                "Active", "Snack", "Large"));
        inventoryRepo.addProduct(new Food(inventoryRepo.generateProductID(), "Nachos with Cheese", 150.0, 60,
                "Active", "Snack", "Regular"));
        inventoryRepo.addProduct(new Drink(inventoryRepo.generateProductID(), "Soda", 90.0, 5,
                "Active", "Large", true));
        inventoryRepo.addProduct(new Drink(inventoryRepo.generateProductID(), "Bottled Water", 50.0, 80,
                "Active", "Regular", true));
        inventoryRepo.addProduct(new Drink(inventoryRepo.generateProductID(), "Hot Coffee", 85.0, 40,
                "Active", "Regular", false));
 
        // Vouchers
        voucherRepo.addVoucher(new Voucher("WELCOME10", "10% off for new customers", "PERCENT", 10,
                LocalDate.now().plusYears(1), 100, 0, true));
        voucherRepo.addVoucher(new Voucher("SAVE50", "50 off any purchase", "FIXED", 50,
                LocalDate.now().plusMonths(6), 50, 0, true));
    }
 
}

