package controllers;
import datastructures.*;
import models.*;
import repositories.*;
import view.*;
import utility.*;
import service.*;
public class CashierController {

    private UserRepository userRepo;
    private MovieRepository movieRepo;
    private CinemaRepository cinemaRepo;
    private InventoryRepository inventoryRepo;
    private TransactionHistory history;
    private VoucherRepository voucherRepo;
    private CheckoutService checkoutService;

    private UserView userView;
    private MovieRepositoryView movieListView;
    private ScheduleView scheduleView;
    private SeatView seatView;
    private TicketView ticketView;
    private InventoryView inventoryView;
    private TransactionHistoryView historyView;
    private ReceiptView receiptView;




    // Cashier Methods

    public void start(Cashier cashier) {

    }

    public void manageProfile(Cashier cashier) {

    }

    public void editProfile(Cashier cashier) {

    }

    public void changePassword(Cashier cashier) {

    }


    // Ticket and Product Methods

    public void sellTickets(Cashier cashier) {

    }

    public void addProductsToOrder(Transaction transaction) {

    }


    // Transaction Methods

    public void voidTransaction(Cashier cashier) {

    }

    public void viewMyTransactions(Cashier cashier) {

    }

    public void reprintReceipt() {

    }


    // Inventory Methods

    public void viewInventory() {

    }

}
