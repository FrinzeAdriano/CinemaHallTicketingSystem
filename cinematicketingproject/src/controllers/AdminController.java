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

    // Shows the admin menu and routes each choice to a feature controller.
    // Option numbers are an assumption; match them to showAdminMenu().
    public void start(Admin admin) {
        int choice = -1;
        while (choice != 0) {
            choice = mainView.showAdminMenu();
            switch (choice) {
                case 1: profileController.manageProfile(admin); break;
                case 2: userController.manageUsers(); break;
                case 3: movieController.manageMovies(); break;
                case 4: cinemaController.manageCinemas(); break;
                case 5: cinemaController.manageRooms(); break;
                case 6: cinemaController.manageSeats(); break;
                case 7: cinemaController.manageSchedules(); break;
                case 8: inventoryController.manageInventory(); break;
                case 9: voucherController.manageVouchers(); break;
                case 10: transactionController.manageTransactionHistory(); break;
                case 11: salesController.viewSalesReports(); break;
                default: break;   // 0 logs out
            }
        }
    }
}