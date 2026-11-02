package controllers;
import datastructures.*;
import models.*;
import repositories.*;
import view.*;
import utility.*;
import service.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private BookingController bookingController;
    private TransactionController transactionController;
    private ProfileController profileController;
    private MovieRepositoryView movieListView;
    private InventoryView inventoryView;
    
 
    // System Methods
    public static void main(String[] args) {
        new CinemaTicketManagementSystem().start();
    }
    
    public void start() {
        // Build the shared repositories once.
        userRepo = new UserRepository();
        movieRepo = new MovieRepository();
        cinemaRepo = new CinemaRepository();
        inventoryRepo = new InventoryRepository();
        history = new TransactionHistory();
        salesRepo = new SalesRepository();
        voucherRepo = new VoucherRepository();
        checkoutService = new CheckoutService(history, salesRepo, inventoryRepo, voucherRepo, 0);
 
        // Views
        view = new CinemaTicketManagementSystemView();
        UserView userView = new UserView();
        MovieView movieView = new MovieView();
        movieListView = new MovieRepositoryView();
        CinemaView cinemaView = new CinemaView();
        CinemaRoomListView roomView = new CinemaRoomListView();
        SeatView seatView = new SeatView();
        ScheduleView scheduleView = new ScheduleView();
        TicketView ticketView = new TicketView();
        inventoryView = new InventoryView();
        VoucherView voucherView = new VoucherView();
        TransactionHistoryView historyView = new TransactionHistoryView();
        ReceiptView receiptView = new ReceiptView();
        SalesView salesView = new SalesView();
 
        // Feature controllers
        profileController = new ProfileController(userRepo, userView);

        UserManagementController userController =
            new UserManagementController(userRepo, userView);

        MovieManagementController movieController =
            new MovieManagementController(movieRepo, movieView, movieListView);

        CinemaManagementController cinemaController =
            new CinemaManagementController(cinemaRepo, movieRepo,
                cinemaView, roomView, seatView, scheduleView);

        InventoryController inventoryController =
            new InventoryController(inventoryRepo, inventoryView);

        VoucherController voucherController =
            new VoucherController(voucherRepo, voucherView);

        transactionController = new TransactionController(
            history, salesRepo, checkoutService, historyView, receiptView
        );

        SalesReportController salesController =
            new SalesReportController(salesRepo, salesView);

        bookingController = new BookingController(
            movieRepo, cinemaRepo, inventoryRepo, history, voucherRepo,
            checkoutService, movieListView, scheduleView, seatView,
            ticketView, inventoryView, receiptView
        );
 
        // Role controllers
        adminController = new AdminController(view, profileController, userController, movieController,
                cinemaController, inventoryController, voucherController, transactionController, salesController);
        customerController = new CustomerController(userRepo,movieRepo,userView,
    movieListView,view,profileController,bookingController,transactionController,inventoryRepo,inventoryView);
 
        loadSampleData();
 
        boolean running = true;
        while (running) {
            int choice = view.showMainMenu();
            if (choice == 1) {
                // Business rule: 3 login attempts, then back to the main menu.
                // Typing 0 at the username or password prompt goes back to the main menu.
                int attempts = 0;
                boolean loggedIn = false;
                boolean cancelled = false;
                while (attempts < 3 && !loggedIn && !cancelled) {
                    String username = view.promptUsername();
                    if (username.trim().equals("0")) {
                        cancelled = true;
                        break;
                    }
                    String password = view.promptPassword();
                    if (password.trim().equals("0")) {
                        cancelled = true;
                        break;
                    }
                    loggedIn = login(username, password);
                    attempts++;
                }
                if (loggedIn) {
                    route(currentUser);
                    logout();
                } else if (!cancelled) {
                    view.showError("Too many failed login attempts. Returning to the main menu.");
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
        return true;
    }
 
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
            customerController.start((Cashier) user);
        } else if (user instanceof Customer) {
            customerController.start((Customer) user);
        } else {
            view.showError("Unknown user role.");
        }
    }
 
    // Demo data so the system is usable on first run.
    // Logins:  admin / admin123    cashier1 / cash123    customer1 / cust123
    public void loadSampleData() {
 
        // Users
        Admin admin = new Admin(userRepo.generateUserID("Admin"));
        admin.setUserName("admin");
        admin.changePassword(null, "admin123");
        admin.setFullName("System Admin");
        admin.setStatus("Active");
        userRepo.addUser(admin);
 
        Cashier cashier = new Cashier(userRepo.generateUserID("Cashier"));
        cashier.setUserName("cashier1");
        cashier.changePassword(null, "cash123");
        cashier.setFullName("Sam Cashier");
        cashier.setStatus("Active");
        cashier.setShift("Morning");
        userRepo.addUser(cashier);
 
        Customer customer = new Customer(userRepo.generateUserID("Customer"));
        customer.setUserName("customer1");
        customer.changePassword(null, "cust123");
        customer.setFullName("Chris Customer");
        customer.setStatus("Active");
        customer.setCustomerType("Online");
        userRepo.addUser(customer);
 
        // Cinema (movies, rooms and schedules are not loaded yet: their models changed)
        Cinema cinema = new Cinema(cinemaRepo.generateCinemaID());
        cinema.setName("Grand Cinema");
        cinema.setLocation("Downtown Mall");
        cinemaRepo.addCinema(cinema);
        // Movie
        Movie movie = new Movie(movieRepo.generateMovieID());
        movie.setTitle("Test Movie");
        movie.setGenre("Action");
        movie.setDuration("120");
        movie.setRating("PG");
        movie.setDescription("Sample movie for booking tests.");
        movie.setReleaseDate(LocalDate.now().toString());
        movieRepo.addMovie(movie);

        // Cinema room
        CinemaRoom room = new CinemaRoom(
            CinemaRoom.generateRoomID(cinema)
        );

        room.setRoomName("Room 1");
        room.setRows("3");
        room.setSeatsPerRow("5");
        room.setStatus(true);
        room.generateSeatLayout();

        cinema.addRoom(room);

        // Schedule
        Schedule schedule = new Schedule("SCH001");
        schedule.setMovie(movie);
        schedule.setRoom(room);

        LocalDateTime showTime = LocalDateTime.now()
            .plusDays(1)
            .withHour(18)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

        schedule.setShowDateTime(
            showTime.format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"))
        );

        schedule.setPrice("250");
        room.addSchedule(schedule);
        // Inventory (Soda is deliberately low on stock so the low-stock report has something to show)
        Food popcorn = new Food(inventoryRepo.generateProductID());
        popcorn.setName("Popcorn (Large)");
        popcorn.setPrice(180.0);
        popcorn.setStockQuantity(100);
        popcorn.setFoodType("POPCORN");
        popcorn.setServingSize("Large");
        inventoryRepo.addProduct(popcorn);
 
        Food nachos = new Food(inventoryRepo.generateProductID());
        nachos.setName("Nachos with Cheese");
        nachos.setPrice(150.0);
        nachos.setStockQuantity(60);
        nachos.setFoodType("FRIES");
        nachos.setServingSize("Regular");
        inventoryRepo.addProduct(nachos);
 
        Drink soda = new Drink(inventoryRepo.generateProductID());
        soda.setName("Soda");
        soda.setPrice(90.0);
        soda.setStockQuantity(5);
        soda.setStatus("LOW_STOCK");
        soda.setSize("Large");
        soda.setCold(true);
        inventoryRepo.addProduct(soda);
 
        Drink water = new Drink(inventoryRepo.generateProductID());
        water.setName("Bottled Water");
        water.setPrice(50.0);
        water.setStockQuantity(80);
        water.setSize("Regular");
        water.setCold(true);
        inventoryRepo.addProduct(water);
 
        Drink coffee = new Drink(inventoryRepo.generateProductID());
        coffee.setName("Hot Coffee");
        coffee.setPrice(85.0);
        coffee.setStockQuantity(40);
        coffee.setSize("Regular");
        coffee.setCold(false);
        inventoryRepo.addProduct(coffee);
        
        // Vouchers
        Voucher welcome = new Voucher("WELCOME10");
        welcome.setDescription("10% off for new customers");
        welcome.setDiscountMode("PERCENT");   // must come before setValue (PERCENT is capped at 100)
        welcome.setValue("10");
        welcome.setExpiryDate(LocalDate.now().plusYears(1).toString());
        welcome.setMaxUses("1");
        welcome.setTimesUsed(0);              // takes an int, so this one stays as is
        welcome.setActive(true);
        voucherRepo.addVoucher(welcome);

        Voucher save50 = new Voucher("SAVE50");
        save50.setDescription("50 off any purchase");
        save50.setDiscountMode("FIXED");
        save50.setValue("50");
        save50.setExpiryDate(LocalDate.now().plusMonths(6).toString());
        save50.setMaxUses("1");
        save50.setTimesUsed(0);
        save50.setActive(true);
        voucherRepo.addVoucher(save50);
    }
}