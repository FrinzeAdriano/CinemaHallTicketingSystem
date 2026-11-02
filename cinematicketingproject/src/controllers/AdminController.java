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


    // Manages transaction history (Member C)
    public void manageTransactionHistory() {

        if (historyView == null || history == null) {
            return;
        }

        while (true) {

            int choice = historyView.showHistoryMenu();

            switch (choice) {

                // Back
                case 0:
                    return;


                // View all transactions
                case 1:

                    historyView.displayTransactions(
                        history.viewTransactionHistory()
                    );

                    break;


                // Filter transactions
                case 2: {

                    int filterChoice =
                        historyView.promptFilterOption();

                    // Back to transaction history menu
                    if (filterChoice == 0) {
                        break;
                    }


                    // Filter by date range
                    if (filterChoice == 5) {

                        LocalDate startDate = historyView.readDate(
                            "Enter start date (yyyy-MM-dd): "
                        );

                        LocalDate endDate = historyView.readDate(
                            "Enter end date (yyyy-MM-dd): "
                        );

                        if (startDate == null || endDate == null) {
                            historyView.showError(
                                "Invalid date range."
                            );
                            break;
                        }

                        if (endDate.isBefore(startDate)) {
                            historyView.showError(
                                "End date cannot be earlier than start date."
                            );
                            break;
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
                        } else {
                            historyView.showError(
                                "Invalid filter option."
                            );
                            break;
                        }


                        String value = historyView.readString(
                            "Enter filter value: "
                        );

                        if (!InputValidator.isNotBlank(value)) {
                            historyView.showError(
                                "Filter value cannot be blank."
                            );
                            break;
                        }

                        try {

                            historyView.displayTransactions(
                                history.filterTransactionHistory(
                                    field, value.trim()
                                )
                            );

                        } catch (IllegalArgumentException e) {

                            historyView.showError(e.getMessage());
                        }
                    }

                    break;
                }


                // Search transaction
                case 3: {

                    String transactionID = historyView.readString(
                        "Enter transaction ID: "
                    );

                    if (!InputValidator.isNotBlank(transactionID)) {
                        historyView.showError(
                            "Transaction ID cannot be blank."
                        );
                        break;
                    }

                    Transaction transaction =
                        history.findByID(transactionID.trim());

                    if (transaction == null) {

                        historyView.showError(
                            "Transaction not found."
                        );

                    } else {

                        historyView.displayTransactionDetails(
                            transaction
                        );
                    }

                    break;
                }


                default:

                    historyView.showError(
                        "Invalid menu option."
                    );
            }
        }
    }
    // Reprints an existing receipt
public void reprintReceipt() {

        if (history == null || receiptView == null) {
            return;
        }


        // Requests transaction ID
        String transactionID = receiptView.readString(
            "Enter transaction ID: "
        );

        if (!InputValidator.isNotBlank(transactionID)) {
            receiptView.showError(
                "Transaction ID cannot be blank."
            );
            return;
        }


        // Searches for the transaction
        Transaction transaction =
            history.findByID(transactionID.trim());

        if (transaction == null) {
            receiptView.showError(
                "Transaction not found."
            );
            return;
        }


        // Checks receipt availability
        if (!InputValidator.isNotBlank(
                transaction.getReceiptNo())) {

            receiptView.showError(
                "No receipt is available for this transaction."
            );
            return;
        }


        // Reconstructs the receipt
        try {

            Receipt receipt = new Receipt(
                transaction.getReceiptNo(),
                transaction.getDateTime(),
                transaction
            );

            receiptView.displayReprint(receipt);

        } catch (IllegalArgumentException e) {

            receiptView.showError(e.getMessage());
        }
    }

    // Voids a transaction
 public void voidTransaction() {

        if (history == null || historyView == null
                || checkoutService == null) {

            return;
        }


        // Requests transaction ID
        String transactionID = historyView.readString(
            "Enter transaction ID to void: "
        );

        if (!InputValidator.isNotBlank(transactionID)) {
            historyView.showError(
                "Transaction ID cannot be blank."
            );
            return;
        }


        // Searches for the transaction
        Transaction transaction =
            history.findByID(transactionID.trim());

        if (transaction == null) {
            historyView.showError(
                "Transaction not found."
            );
            return;
        }


        // Checks transaction status
        if (transaction.getStatus().equalsIgnoreCase("CANCELLED")) {
            historyView.showError(
                "This transaction has already been voided."
            );
            return;
        }


        // Requests confirmation
        if (!historyView.confirm(
                "Are you sure you want to void this transaction?")) {

            return;
        }


        // Cancels the transaction
        try {

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

        } catch (IllegalArgumentException | IllegalStateException e) {

            historyView.showError(e.getMessage());
        }
    }


    // Views sales reports
 public void viewSalesReports() {

        if (salesRepo == null || salesView == null) {
            return;
        }

        while (true) {

            int choice = salesView.showSalesMenu();

            switch (choice) {

                // Back
                case 0:
                    return;


                // View all sales
                case 1:

                    salesView.displaySales(
                        salesRepo.viewSales()
                    );

                    break;


                // View sales by date
                case 2: {

                    LocalDate date = salesView.promptDate();

                    if (date == null) {
                        salesView.showError(
                            "Invalid date."
                        );
                        break;
                    }

                    Sales dailySales =
                        salesRepo.getSalesByDate(date);

                    if (dailySales == null) {

                        salesView.showMessage(
                            "No sales records found for this date."
                        );

                    } else {

                        salesView.displaySalesSummary(dailySales);
                    }

                    break;
                }


                // View sales by date range
                case 3: {

                    LocalDate startDate = salesView.promptDate();
                    LocalDate endDate = salesView.promptDate();

                    if (startDate == null || endDate == null) {
                        salesView.showError(
                            "Invalid date range."
                        );
                        break;
                    }

                    if (endDate.isBefore(startDate)) {
                        salesView.showError(
                            "End date cannot be earlier than start date."
                        );
                        break;
                    }

                    salesView.displaySales(
                        salesRepo.getSalesByRange(
                            startDate, endDate
                        )
                    );

                    break;
                }


                // View total revenue
                case 4: {

                    double totalRevenue =
                        salesRepo.getTotalRevenue();

                    salesView.showMessage(
                        "Total Revenue: "
                        + String.format("%.2f", totalRevenue)
                    );

                    break;
                }


                default:

                    salesView.showError(
                        "Invalid menu option."
                    );
            }
        }
    }


}
