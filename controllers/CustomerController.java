package controllers;

import datastructures.LinkedList;
import models.Cashier;
import models.Customer;
import models.Movie;
import repositories.InventoryRepository;
import repositories.MovieRepository;
import repositories.UserRepository;
import utility.InputValidator;
import utility.LoopUtility;
import view.CinemaTicketManagementSystemView;
import view.InventoryView;
import view.MovieRepositoryView;
import view.UserView;


public class CustomerController {

    private UserRepository userRepo;
    private MovieRepository movieRepo;
    private UserView userView;
    private MovieRepositoryView movieListView;
    private CinemaTicketManagementSystemView mainView;
    private ProfileController profileController;
    private BookingController bookingController;
    private TransactionController transactionController;
    private InventoryRepository inventoryRepo;
    private InventoryView inventoryView;

    public CustomerController(UserRepository userRepo, MovieRepository movieRepo,
        UserView userView, MovieRepositoryView movieListView,
        CinemaTicketManagementSystemView mainView,
        ProfileController profileController, BookingController bookingController,
        TransactionController transactionController,
        InventoryRepository inventoryRepo, InventoryView inventoryView) {

        this.userRepo = userRepo;
        this.movieRepo = movieRepo;
        this.userView = userView;
        this.movieListView = movieListView;
        this.mainView = mainView;
        this.profileController = profileController;
        this.bookingController = bookingController;
        this.transactionController = transactionController;
        this.inventoryRepo = inventoryRepo;
        this.inventoryView = inventoryView;
    }

    public void start(Customer customer) {

        int choice = -1;

        while (choice != 0) {

            choice = mainView.showCustomerMenu();

            switch (choice) {
                case 1:
                    browseMovies();
                    break;
                case 2:
                    bookingController.bookTickets(customer);
                    break;
                case 3:
                    bookingController.cancelBooking(customer);
                    break;
                case 4:
                    transactionController.viewMyHistory(customer);
                    break;
                case 5:
                    profileController.manageProfile(customer);
                    break;
                case 0:
                    break;
                default:
                    mainView.showError("Invalid option.");
                    break;
            }
        }
    }

    public void start(Cashier cashier) {

        if (cashier == null) {
            return;
        }

        boolean running = true;

        while (running) {

            int choice = mainView.showCashierMenu();

            switch (choice) {

                case 1:
                    browseMovies();
                    break;

                case 2:
                    bookingController.sellTickets(cashier);
                    break;

                case 3:
                    transactionController.voidTransaction(cashier);
                    break;

                case 4:
                    transactionController.viewMyTransactions(cashier);
                    break;

                case 5:
                    transactionController.reprintReceipt(cashier);
                    break;

                case 6:
                    inventoryView.displayProducts(inventoryRepo.viewProducts());
                    break;

                case 7:
                    profileController.manageProfile(cashier);
                    break;

                case 0:
                    running = false;
                    break;

                default:
                    mainView.showError("Invalid option.");
                    break;
            }
        }
    }

    // Self-registration. The account is always a Customer, type "Online", status "Active".
    // Each field is asked again until it is valid; 0 at any prompt goes back without registering.
    public void register() {

        Customer customer = new Customer(userRepo.generateUserID("Customer"));

        System.out.println();
        System.out.println("===== REGISTER AS A CUSTOMER =====");

        if (LoopUtility.retryUntilValid(userView::promptUsername,
                name -> {
                    if (userRepo.isUsernameTaken(name)) {
                        throw new IllegalArgumentException("Invalid input. Username " + name.trim() + " is already taken.");
                    }
                    customer.setUserName(name);
                }, userView::showError, "0")
                && LoopUtility.retryUntilValid(userView::promptPassword,
                password -> setNewPassword(customer, password), userView::showError, "0")
                && LoopUtility.retryUntilValid(userView::promptFullName,
                customer::setFullName, userView::showError, "0")) {

            customer.setCustomerType("Online");
            customer.setStatus("Active");

            try {
                userRepo.addUser(customer);
                userView.showMessage("Registration successful! Your user ID is "
                    + customer.getUserID() + ". You can now log in as " + customer.getUserName() + ".");
            } catch (IllegalArgumentException e) {
                userView.showError(e.getMessage());
            }
        } else {
            userView.showMessage("Registration cancelled.");
        }
    }

    // Checks the password, asks the user to confirm it, then sets it (first-time setup, old password is null).
    private void setNewPassword(Customer customer, String password) {
        if (!InputValidator.isValidPassword(password)) {
            throw new IllegalArgumentException("Invalid input. Password must be at least 6 characters and contain at least 1 digit.");
        }
        if (!password.equals(userView.promptConfirmPassword())) {
            throw new IllegalArgumentException("Invalid input. Passwords do not match.");
        }
        customer.changePassword(null, password);
    }

    public void browseMovies() {
        if (movieRepo == null || movieListView == null) {
            return;
        }

        LinkedList<Movie> movies = movieRepo.viewMovies();

        if (movies == null || movies.size() == 0) {
            movieListView.showMessage("No movies available.");
            return;
        }

        movieListView.displayMovieList(movies);
    }
}