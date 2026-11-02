package controllers;

import models.Customer;
import repositories.MovieRepository;
import repositories.UserRepository;
import view.CinemaTicketManagementSystemView;
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

    public CustomerController(UserRepository userRepo, MovieRepository movieRepo, UserView userView,
            MovieRepositoryView movieListView, CinemaTicketManagementSystemView mainView,
            ProfileController profileController, BookingController bookingController,
            TransactionController transactionController) {
        this.userRepo = userRepo;
        this.movieRepo = movieRepo;
        this.userView = userView;
        this.movieListView = movieListView;
        this.mainView = mainView;
        this.profileController = profileController;
        this.bookingController = bookingController;
        this.transactionController = transactionController;
    }

    // Option numbers are an assumption; match them to showCustomerMenu().
    public void start(Customer customer) {
        int choice = -1;
        while (choice != 0) {
            choice = mainView.showCustomerMenu();
            switch (choice) {
                case 1: profileController.manageProfile(customer); break;
                case 2: browseMovies(); break;
                case 3: bookingController.bookTickets(customer); break;
                case 4: bookingController.cancelBooking(customer); break;
                case 5: transactionController.viewMyHistory(customer); break;
                default: break;   // 0 logs out
            }
        }
    }

    public void register() {
    }

    public void browseMovies() {
    }
}