package controllers;
import datastructures.*;
import java.time.LocalDate;
import models.*;
import repositories.*;
import view.*;
import utility.*;
import service.*;
public class AdminController {

    private UserRepository userRepo;
    private MovieRepository movieRepo;
    private CinemaRepository cinemaRepo;
    private InventoryRepository inventoryRepo;
    private TransactionHistory history;
    private SalesRepository salesRepo;
    private VoucherRepository voucherRepo;
    private CheckoutService checkoutService;

    private UserView userView;
    private MovieView movieView;
    private CinemaView cinemaView;
    private CinemaRoomListView roomView;
    private SeatView seatView;
    private ScheduleView scheduleView;
    private InventoryView inventoryView;
    private TransactionHistoryView historyView;
    private ReceiptView receiptView;
    private SalesView salesView;
    private VoucherView voucherView;


 

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

    public void manageRooms() {

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


    // Manages transaction history
public void manageTransactionHistory() {

    while (true) {

        int choice = historyView.showHistoryMenu();

        if (choice == 0) {
            return;
        }

        // View all transactions
        if (choice == 1) {

            historyView.displayTransactions(
                history.viewTransactionHistory()
            );

        }

        // Filter transactions
        else if (choice == 2) {

            int filterChoice = historyView.promptFilterOption();

            if (filterChoice == 5) {

                LocalDate startDate = salesView.promptDate();
                LocalDate endDate = salesView.promptDate();

                if (endDate.isBefore(startDate)) {
                    historyView.showError(
                        "End date cannot be earlier than start date."
                    );
                    continue;
                }

                historyView.displayTransactions(
                    history.filterByDateRange(startDate, endDate)
                );

            } else {

                String field = "";

                if (filterChoice == 1) {
                    field = "status";
                } else if (filterChoice == 2) {
                    field = "bookingType";
                } else if (filterChoice == 3) {
                    field = "customerID";
                } else if (filterChoice == 4) {
                    field = "cashierID";
                }

                String value = historyView.readString(
                    "Enter filter value: "
                );

                historyView.displayTransactions(
                    history.filterTransactionHistory(field, value)
                );
            }
        }

        // Search transaction
        else if (choice == 3) {

            String transactionID = historyView.readString(
                "Enter transaction ID: "
            );

            Transaction transaction =
                history.findByID(transactionID);

            if (transaction == null) {
                historyView.showError("Transaction not found.");
            } else {
                historyView.displayTransactionDetails(transaction);
            }
        }
    }
}
    // Reprints an existing receipt
public void reprintReceipt() {

    String transactionID =
        receiptView.readString("Enter transaction ID: ");

    Transaction transaction = history.findByID(transactionID);

    if (transaction == null) {
        receiptView.showError("Transaction not found.");
        return;
    }

    if (transaction.getReceiptNo() == null) {
        receiptView.showError("No receipt is available.");
        return;
    }

    Receipt receipt = new Receipt(
        transaction.getReceiptNo(),
        transaction.getDateTime(),
        transaction
    );

    receiptView.displayReprint(receipt);
}

    // Voids a transaction
public void voidTransaction() {

    String transactionID =
        historyView.readString("Enter transaction ID: ");

    Transaction transaction = history.findByID(transactionID);

    if (transaction == null) {
        historyView.showError("Transaction not found.");
        return;
    }

    if (historyView.confirm(
            "Are you sure you want to void this transaction?")) {

        boolean cancelled =
            checkoutService.cancelTransaction(transaction);

        if (cancelled) {
            historyView.showMessage(
                "Transaction successfully voided."
            );
        } else {
            historyView.showError(
                "Unable to void transaction."
            );
        }
    }
}


    // Views sales reports
public void viewSalesReports() {

    while (true) {

        int choice = salesView.showSalesMenu();

        if (choice == 0) {
            return;
        }

        // View all sales
        if (choice == 1) {

            salesView.displaySales(
                salesRepo.viewSales()
            );

        }

        // View sales by date
        else if (choice == 2) {

            LocalDate date = salesView.promptDate();

            Sales dailySales =
                salesRepo.getSalesByDate(date);

            if (dailySales == null) {
                salesView.showMessage(
                    "No sales records found for this date."
                );
            } else {
                salesView.displaySalesSummary(dailySales);
            }

        }

        // View sales by date range
        else if (choice == 3) {

            LocalDate startDate = salesView.promptDate();
            LocalDate endDate = salesView.promptDate();

            if (endDate.isBefore(startDate)) {
                salesView.showError(
                    "End date cannot be earlier than start date."
                );
                continue;
            }

            salesView.displaySales(
                salesRepo.getSalesByRange(startDate, endDate)
            );

        }

        // View total revenue
        else if (choice == 4) {

            double totalRevenue = salesRepo.getTotalRevenue();

            salesView.showMessage(
                "Total Revenue: " + totalRevenue
            );
        }
    }
}

}
