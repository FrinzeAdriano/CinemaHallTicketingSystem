
package controllers;

import datastructures.LinkedList;
import models.Customer;
import models.Movie;
import repositories.MovieRepository;
import repositories.UserRepository;
import view.CinemaTicketManagementSystemView;
import view.MovieRepositoryView;
import view.UserView;
import models.Cashier;
import repositories.InventoryRepository;
import view.InventoryView;


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
    public void register() {
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
