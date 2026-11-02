package controllers;
import datastructures.*;
import java.time.LocalDate;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
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

                    if (filterChoice == 0) {
                        break;
                    }


                    // Filter by date range
                    if (filterChoice == 5) {

                        LocalDate startDate;
                        LocalDate endDate;

                        while (true) {

                            startDate = historyView.readDate(
                                "Enter start date (yyyy-MM-dd): "
                            );

                            endDate = historyView.readDate(
                                "Enter end date (yyyy-MM-dd): "
                            );

                            if (startDate == null || endDate == null) {
                                historyView.showError(
                                    "Invalid date range."
                                );
                                continue;
                            }

                            if (endDate.isBefore(startDate)) {
                                historyView.showError(
                                    "End date cannot be earlier than start date."
                                );
                                continue;
                            }

                            break;
                        }

                        historyView.displayTransactions(
                            history.filterByDateRange(
                                startDate, endDate
                            )
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

                        final String filterField = field;
                        final String[] filterValue = new String[1];


                        // Repeats until filter value is valid
                        LoopUtility.retryUntilValid(

                            new Supplier<String>() {
                                @Override
                                public String get() {
                                    return historyView.readString(
                                        "Enter filter value: "
                                    );
                                }
                            },

                            new Consumer<String>() {
                                @Override
                                public void accept(String value) {

                                    if (!InputValidator.isNotBlank(value)) {
                                        throw new IllegalArgumentException(
                                            "Filter value cannot be blank."
                                        );
                                    }

                                    String formattedValue = value.trim();

                                    // Validate transaction status
                                    if (filterField.equals("status")
                                            && !formattedValue.equalsIgnoreCase("PENDING")
                                            && !formattedValue.equalsIgnoreCase("COMPLETED")
                                            && !formattedValue.equalsIgnoreCase("CANCELLED")) {

                                        throw new IllegalArgumentException(
                                            "Invalid status. Enter PENDING, COMPLETED, or CANCELLED."
                                        );
                                    }

                                    // Validate booking type
                                    if (filterField.equals("bookingType")
                                            && !formattedValue.equalsIgnoreCase("ONLINE")
                                            && !formattedValue.equalsIgnoreCase("WALK_IN")) {

                                        throw new IllegalArgumentException(
                                            "Invalid booking type. Enter ONLINE or WALK_IN."
                                        );
                                    }

                                    filterValue[0] = formattedValue;
                                }
                            },

                            new Consumer<String>() {
                                @Override
                                public void accept(String message) {
                                    historyView.showError(message);
                                }
                            }
                        );


                        // Displays filtered transactions
                        try {

                            historyView.displayTransactions(
                                history.filterTransactionHistory(
                                    filterField, filterValue[0]
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

                    final Transaction[] transactionHolder =
                        new Transaction[1];


                    // Repeats until transaction ID exists
                    LoopUtility.retryUntilValid(

                        new Supplier<String>() {
                            @Override
                            public String get() {
                                return historyView.readString(
                                    "Enter transaction ID: "
                                );
                            }
                        },

                        new Consumer<String>() {
                            @Override
                            public void accept(String transactionID) {

                                if (!InputValidator.isNotBlank(transactionID)) {
                                    throw new IllegalArgumentException(
                                        "Transaction ID cannot be blank."
                                    );
                                }

                                Transaction transaction =
                                    history.findByID(transactionID.trim());

                                if (transaction == null) {
                                    throw new IllegalArgumentException(
                                        "Transaction not found. Please try again."
                                    );
                                }

                                transactionHolder[0] = transaction;
                            }
                        },

                        new Consumer<String>() {
                            @Override
                            public void accept(String message) {
                                historyView.showError(message);
                            }
                        }
                    );


                    historyView.displayTransactionDetails(
                        transactionHolder[0]
                    );

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

        final Transaction[] transactionHolder =
            new Transaction[1];


        // Repeats until transaction has an available receipt
        LoopUtility.retryUntilValid(

            new Supplier<String>() {
                @Override
                public String get() {
                    return receiptView.readString(
                        "Enter transaction ID: "
                    );
                }
            },

            new Consumer<String>() {
                @Override
                public void accept(String transactionID) {

                    if (!InputValidator.isNotBlank(transactionID)) {
                        throw new IllegalArgumentException(
                            "Transaction ID cannot be blank."
                        );
                    }

                    Transaction transaction =
                        history.findByID(transactionID.trim());

                    if (transaction == null) {
                        throw new IllegalArgumentException(
                            "Transaction not found. Please try again."
                        );
                    }

                    if (!InputValidator.isNotBlank(
                            transaction.getReceiptNo())) {

                        throw new IllegalArgumentException(
                            "No receipt is available for this transaction."
                        );
                    }

                    transactionHolder[0] = transaction;
                }
            },

            new Consumer<String>() {
                @Override
                public void accept(String message) {
                    receiptView.showError(message);
                }
            }
        );


        // Reconstructs the receipt
        try {

            Transaction transaction = transactionHolder[0];

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

        final Transaction[] transactionHolder =
            new Transaction[1];


        // Repeats until a valid, non-cancelled transaction is found
        LoopUtility.retryUntilValid(

            new Supplier<String>() {
                @Override
                public String get() {
                    return historyView.readString(
                        "Enter transaction ID to void: "
                    );
                }
            },

            new Consumer<String>() {
                @Override
                public void accept(String transactionID) {

                    if (!InputValidator.isNotBlank(transactionID)) {
                        throw new IllegalArgumentException(
                            "Transaction ID cannot be blank."
                        );
                    }

                    Transaction transaction =
                        history.findByID(transactionID.trim());

                    if (transaction == null) {
                        throw new IllegalArgumentException(
                            "Transaction not found. Please try again."
                        );
                    }

                    if (transaction.getStatus().equalsIgnoreCase("CANCELLED")) {
                        throw new IllegalArgumentException(
                            "This transaction has already been voided. Enter another transaction ID."
                        );
                    }

                    transactionHolder[0] = transaction;
                }
            },

            new Consumer<String>() {
                @Override
                public void accept(String message) {
                    historyView.showError(message);
                }
            }
        );


        Transaction transaction = transactionHolder[0];


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

                    LocalDate startDate;
                    LocalDate endDate;

                    while (true) {

                        startDate = salesView.promptDate();
                        endDate = salesView.promptDate();

                        if (startDate == null || endDate == null) {
                            salesView.showError(
                                "Invalid date range."
                            );
                            continue;
                        }

                        if (endDate.isBefore(startDate)) {
                            salesView.showError(
                                "End date cannot be earlier than start date."
                            );
                            continue;
                        }

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
                        + String.format(
                            Locale.ROOT, "%.2f", totalRevenue
                        )
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
