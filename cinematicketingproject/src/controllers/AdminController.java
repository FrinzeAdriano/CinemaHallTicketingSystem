package controllers;

import models.Admin;
import view.CinemaTicketManagementSystemView;

public class AdminController {
    private CinemaTicketManagementSystemView mainView;
    private ProfileController profileController;
    private UserManagementController userController;
    private MovieManagementController movieController;
    private CinemaManagementController cinemaController;
    private InventoryController inventoryController;
    private VoucherController voucherController;
    private TransactionController transactionController;
    private SalesReportController salesController;

    public AdminController(CinemaTicketManagementSystemView mainView, ProfileController profileController,
            UserManagementController userController, MovieManagementController movieController,
            CinemaManagementController cinemaController, InventoryController inventoryController,
            VoucherController voucherController, TransactionController transactionController,
            SalesReportController salesController) {
        this.mainView = mainView;
        this.profileController = profileController;
        this.userController = userController;
        this.movieController = movieController;
        this.cinemaController = cinemaController;
        this.inventoryController = inventoryController;
        this.voucherController = voucherController;
        this.transactionController = transactionController;
        this.salesController = salesController;
    }

    // Shows the admin menu (5 categories). Each category opens its own submenu.
    // 0 in a submenu goes back to this menu; 0 here logs out.
    // An invalid number only shows an error and asks for the option again.
    // A feature controller that throws IllegalArgumentException or IllegalStateException
    // is caught, its message is shown, and the admin stays in the same submenu.
    public void start(Admin admin) {

        if (admin == null) {
            return;
        }

        int category = -1;

        while (category != 0) {

            category = mainView.showAdminMenu();

            switch (category) {

                case 0:
                    break;

                // 1. Account
                case 1: {
                    int choice = -1;

                    while (choice != 0) {

                        choice = mainView.showSubMenu("ACCOUNT", "My profile", "Manage users", null);

                        try {
                            switch (choice) {
                                case 0: break;
                                case 1: profileController.manageProfile(admin); break;
                                case 2: userController.manageUsers(); break;
                                default: mainView.showError("Invalid option. Choose 0 to 2.");
                            }
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            mainView.showError(e.getMessage());
                        }
                    }
                    break;
                }

                // 2. Movies and Showtimes
                case 2: {
                    int choice = -1;

                    while (choice != 0) {

                        choice = mainView.showSubMenu("MOVIES AND SHOWTIMES", "Manage movies", "Manage schedules", null);

                        try {
                            switch (choice) {
                                case 0: break;
                                case 1: movieController.manageMovies(); break;
                                case 2: cinemaController.manageSchedules(); break;
                                default: mainView.showError("Invalid option. Choose 0 to 2.");
                            }
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            mainView.showError(e.getMessage());
                        }
                    }
                    break;
                }

                // 3. Cinema Setup
                case 3: {
                    int choice = -1;

                    while (choice != 0) {

                        choice = mainView.showSubMenu("CINEMA SETUP", "Manage cinemas", "Manage rooms", "Manage seats");

                        try {
                            switch (choice) {
                                case 0: break;
                                case 1: cinemaController.manageCinemas(); break;
                                case 2: cinemaController.manageRooms(); break;
                                case 3: cinemaController.manageSeats(); break;
                                default: mainView.showError("Invalid option. Choose 0 to 3.");
                            }
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            mainView.showError(e.getMessage());
                        }
                    }
                    break;
                }

                // 4. Food, Drinks and Promos
                case 4: {
                    int choice = -1;

                    while (choice != 0) {

                        choice = mainView.showSubMenu("FOOD, DRINKS AND PROMOS", "Manage inventory", "Manage vouchers", null);

                        try {
                            switch (choice) {
                                case 0: break;
                                case 1: inventoryController.manageInventory(); break;
                                case 2: voucherController.manageVouchers(); break;
                                default: mainView.showError("Invalid option. Choose 0 to 2.");
                            }
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            mainView.showError(e.getMessage());
                        }
                    }
                    break;
                }

                // 5. Sales and Records
                case 5: {
                    int choice = -1;

                    while (choice != 0) {

                        choice = mainView.showSubMenu("SALES AND RECORDS", "Transaction history", "Sales reports", null);

                        try {
                            switch (choice) {
                                case 0: break;
                                case 1: transactionController.manageTransactionHistory(); break;
                                case 2: salesController.viewSalesReports(); break;
                                default: mainView.showError("Invalid option. Choose 0 to 2.");
                            }
                        } catch (IllegalArgumentException | IllegalStateException e) {
                            mainView.showError(e.getMessage());
                        }
                    }
                    break;
                }

                default:
                    mainView.showError("Invalid option. Choose 0 to 5.");
            }
        }
    }
}