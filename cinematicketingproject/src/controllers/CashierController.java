package controllers;
import datastructures.*;
import java.time.LocalDateTime;
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

    // Sells tickets for a walk-in customer
    public void sellTickets(Cashier cashier) {

        LinkedList schedules =
            cinemaRepo.getAllSchedules();

        if (schedules == null || schedules.size() == 0) {
            ticketView.showMessage("No available movie schedules.");
            return;
        }

        Schedule schedule = scheduleView.selectSchedule(schedules);

        if (schedule == null) {
            return;
        }

        // Gets the cinema room
        CinemaRoom room = schedule.getRoom();

        LinkedList seats = room.getSeats();

        LinkedList bookedSeatIDs =
            schedule.getBookedSeatIDs();

        // Counts available seats
        int availableSeats = 0;

        for (int i = 0; i < seats.size(); i++) {

            Seat seat = (Seat) seats.get(i);

            if (!bookedSeatIDs.contains(seat.getSeatID())) {
                availableSeats++;
            }
        }

        if (availableSeats == 0) {
            ticketView.showMessage("No available seats.");
            return;
        }

        // Displays the seat layout
        seatView.displaySeatGrid(schedule);

        int ticketCount = ticketView.promptTicketCount();

        if (ticketCount > availableSeats) {
            ticketView.showError(
                "Ticket quantity exceeds the available seats."
            );
            return;
        }

        LinkedList selectedTickets =
            new LinkedList();

        int ticketNumber = 1;

        // Selects seats
        while (ticketNumber <= ticketCount) {

            String seatID = seatView.promptSeatID();

            if (!InputValidator.isValidSeatID(seatID)) {
                seatView.showError("Invalid seat ID.");
                continue;
            }

            Seat selectedSeat = null;

            for (int i = 0; i < seats.size(); i++) {

                Seat seat = (Seat)seats.get(i);

                if (seat.getSeatID().equalsIgnoreCase(seatID)) {
                    selectedSeat = seat;
                    break;
                }
            }

            if (selectedSeat == null) {
                seatView.showError("Seat does not exist.");
                continue;
            }

            if (bookedSeatIDs.contains(selectedSeat.getSeatID())) {
                seatView.showError("Seat is already booked.");
                continue;
            }

             boolean alreadySelected = false;

            for (int i = 0; i < selectedTickets.size(); i++) {

                Ticket selectedTicket =
                    (Ticket) selectedTickets.get(i);

                String previouslySelectedSeatID =
                    selectedTicket.getSeat().getSeatID();

                if (previouslySelectedSeatID.equalsIgnoreCase(
                        selectedSeat.getSeatID())) {

                    alreadySelected = true;
                    break;
                }
            }


            if (alreadySelected) {
                seatView.showError("You already selected that seat.");
                continue;
            }

            double ticketPrice =
                schedule.getPrice() + selectedSeat.getPriceModifier();

            String ticketID =
                history.generateTransactionID() + "-T" + ticketNumber;

            Ticket ticket = new Ticket(
                ticketID,
                schedule,
                selectedSeat,
                ticketPrice,
                "BOOKED"
            );

            selectedTickets.insertAtEnd(ticket);

            ticketNumber++;
        }


        // Creates the transaction
Transaction transaction = new Transaction(
    history.generateTransactionID(),
    "WALK_IN",
    null,
    cashier.getUserID(),
    LocalDateTime.now(),
    selectedTickets,
    new LinkedList<OrderProduct>(),
    null,
    0,
    0,
    null,
    0,
    null,
    0,
    "PENDING",
    null,
    null,
    null
);


        // Selects the payment method
        ticketView.showMessage("[1] Cash");
        ticketView.showMessage("[2] E-wallet");
        ticketView.showMessage("[3] Card");

        int paymentChoice =
            ticketView.readInt("Payment method: ", 1, 3);

        if (paymentChoice == 1) {
            transaction.setPaymentMethod("Cash");
        } else if (paymentChoice == 2) {
            transaction.setPaymentMethod("E-wallet");
        } else {
            transaction.setPaymentMethod("Card");
        }


        // Gets the amount paid
        while (true) {

            String amountInput =
                ticketView.readString("Enter amount paid: ");

            if (InputValidator.isPositiveDouble(amountInput)) {

                transaction.setAmountPaid(
                    Double.parseDouble(amountInput.trim())
                );

                break;
            }

            ticketView.showError(
                "Enter a valid amount greater than zero."
            );
        }


        // Checks payment sufficiency
        if (transaction.getAmountPaid() < transaction.getTotalAmount()) {
            ticketView.showError("Insufficient payment.");
            return;
        }


        // Processes checkout
        try {

            Receipt receipt = checkoutService.checkout(transaction);

            // Reserves selected seats after successful checkout
            for (int i = 0; i < selectedTickets.size(); i++) {

                Ticket ticket = (Ticket) selectedTickets.get(i);

                bookedSeatIDs.insertAtEnd(
                    ticket.getSeat().getSeatID()
                );
            }

            receiptView.displayReceipt(receipt);

        } catch (IllegalStateException e) {

            ticketView.showError(e.getMessage());
        }
    }


     // Adds products to an existing transaction
    public void addProductsToOrder(Transaction transaction) {

        if (transaction == null) {
            ticketView.showError("Transaction not found.");
            return;
        }

        while (true) {

            String productID =
                ticketView.readString("Enter product ID (0 to finish): ");

            if (productID.equals("0")) {
                break;
            }

            // Searches for the product using InventoryRepository
            LinkedList matchingProducts =
                inventoryRepo.filterProducts("ID", productID);

            Product product = null;

            if (matchingProducts != null
                    && matchingProducts.size() > 0) {

                product = (Product) matchingProducts.get(0);
            }

            if (product == null) {
                ticketView.showError("Product not found.");
                continue;
            }

            String quantityInput =
                ticketView.readString("Enter quantity: ");

            if (!InputValidator.isPositiveInt(quantityInput)) {
                ticketView.showError("Invalid quantity.");
                continue;
            }

            int quantity = Integer.parseInt(quantityInput.trim());

            transaction.addProduct(product, quantity);

            ticketView.showMessage("Product added to order.");
        }
    }




     // Voids a cashier's transaction
    public void voidTransaction(Cashier cashier) {

        String transactionID =
            historyView.readString("Enter transaction ID: ");

        Transaction transaction = history.findByID(transactionID);

        if (transaction == null) {
            historyView.showError("Transaction not found.");
            return;
        }

        if (transaction.getCashierID() == null
                || !transaction.getCashierID()
                    .equalsIgnoreCase(cashier.getUserID())) {

            historyView.showError(
                "You cannot cancel another cashier's transaction."
            );

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


     // Displays the current cashier's transactions
    public void viewMyTransactions(Cashier cashier) {

        LinkedList transactions =
            history.getByCashier(cashier.getUserID());

        historyView.displayTransactions(transactions);
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



    // Inventory Methods

    public void viewInventory() {

    }

}
