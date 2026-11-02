package controllers;

import datastructures.*;
import java.time.LocalDateTime;
import models.*;

import repositories.*;

import service.CheckoutService;
import utility.*;

import view.*;


public class BookingController {
    private MovieRepository movieRepo;
    private CinemaRepository cinemaRepo;
    private InventoryRepository inventoryRepo;
    private TransactionHistory history;
    private VoucherRepository voucherRepo;
    private CheckoutService checkoutService;
    private MovieRepositoryView movieListView;
    private ScheduleView scheduleView;
    private SeatView seatView;
    private TicketView ticketView;
    private InventoryView inventoryView;
    private ReceiptView receiptView;

    public BookingController(MovieRepository movieRepo, CinemaRepository cinemaRepo,
            InventoryRepository inventoryRepo, TransactionHistory history, VoucherRepository voucherRepo,
            CheckoutService checkoutService, MovieRepositoryView movieListView, ScheduleView scheduleView,
            SeatView seatView, TicketView ticketView, InventoryView inventoryView, ReceiptView receiptView) {
        this.movieRepo = movieRepo;
        this.cinemaRepo = cinemaRepo;
        this.inventoryRepo = inventoryRepo;
        this.history = history;
        this.voucherRepo = voucherRepo;
        this.checkoutService = checkoutService;
        this.movieListView = movieListView;
        this.scheduleView = scheduleView;
        this.seatView = seatView;
        this.ticketView = ticketView;
        this.inventoryView = inventoryView;
        this.receiptView = receiptView;
    }

    // Cashier
    public void sellTickets(Cashier cashier) {

        if (cashier == null) {
            return;
        }

        // Inactive accounts (or accounts with no status set) cannot sell tickets
        try {
            if (!cashier.getStatus().equalsIgnoreCase("Active")) {
                ticketView.showError("This account is inactive and cannot sell tickets.");
                return;
            }
        } catch (IllegalStateException e) {
            ticketView.showError("This account has no status set and cannot sell tickets.");
            return;
        }

        LinkedList<Schedule> schedules = cinemaRepo.getAllSchedules();

        if (schedules == null || schedules.size() == 0) {
            ticketView.showMessage("No available movie schedules.");
            return;
        }

        // Selects a schedule that is open, has a room and has not started.
        // ScheduleView.selectSchedule must return null when the user enters 0 (go back).
        Schedule schedule = scheduleView.selectSchedule(schedules);

        while (schedule != null && !(schedule.isActive() && schedule.getRoom() != null
                && schedule.getShowDateTime() != null
                && schedule.getShowDateTime().isAfter(LocalDateTime.now()))) {

            ticketView.showError("Invalid or inactive movie schedule. Please select another schedule.");
            schedule = scheduleView.selectSchedule(schedules);
        }

        if (schedule == null) {
            return;
        }

        LinkedList<Seat> seats = schedule.getRoom().getSeats();
        LinkedList<String> bookedSeatIDs = schedule.getBookedSeatIDs();

        // Counts the seats that are in service and not booked
        int availableSeats = 0;

        for (int i = 0; i < seats.size(); i++) {
            if (seats.get(i).getStatus() && !bookedSeatIDs.contains(seats.get(i).getSeatID())) {
                availableSeats++;
            }
        }

        if (availableSeats == 0) {
            ticketView.showMessage("No available seats.");
            return;
        }

        seatView.displaySeatGrid(schedule);

        // Repeats until the quantity is valid. 0 goes back.
        int ticketCount;

        while (true) {

            ticketCount = ticketView.promptTicketCount();

            if (ticketCount == 0) {
                return;
            }

            if (ticketCount < 0) {
                ticketView.showError("Ticket quantity must be greater than zero.");
            } else if (ticketCount <= availableSeats) {
                break;
            } else {
                ticketView.showError("Only " + availableSeats + " seats are available.");
            }
        }

        LinkedList<Ticket> selectedTickets = new LinkedList<Ticket>();
        LinkedList<String> reservedSeatIDs = new LinkedList<String>();
        boolean completed = false;

        // Every model and service call below throws IllegalArgumentException or
        // IllegalStateException for bad data; the catch shows the message.
        // The finally block releases the reserved seats unless the sale was completed.
        try {

            String transactionID = history.generateTransactionID();

            // Selects one seat per ticket. Enter 0 to cancel ticket selling.
            for (int ticketNumber = 1; ticketNumber <= ticketCount; ticketNumber++) {

                Seat selectedSeat = null;

                while (selectedSeat == null) {

                    String input = seatView.promptSeatID();

                    if (input.trim().equals("0")) {
                        ticketView.showMessage("Ticket selling cancelled.");
                        return;
                    }

                    try {

                        if (!InputValidator.isValidSeatID(input)) {
                            throw new IllegalArgumentException("Invalid seat ID.");
                        }

                        String seatID = input.trim().toUpperCase();

                        // Checks the schedule is open and not started, the seat exists, is in
                        // service and is not booked (this also stops picking the same seat twice)
                        schedule.bookSeat(seatID);
                        reservedSeatIDs.insertAtEnd(seatID);

                        selectedSeat = schedule.getRoom().findSeat(seatID);

                    } catch (IllegalArgumentException e) {
                        seatView.showError(e.getMessage());
                    }
                }

                // The seat's price modifier is a multiplier of the schedule price
                selectedTickets.insertAtEnd(new Ticket(
                    transactionID + "-T" + ticketNumber,
                    schedule,
                    selectedSeat,
                    schedule.getPrice() * selectedSeat.getPriceModifier(),
                    "BOOKED"
                ));
            }

            Transaction transaction = new Transaction(
                transactionID, "WALK_IN", null, cashier.getUserID(), LocalDateTime.now(),
                selectedTickets, new LinkedList<OrderProduct>(),
                null, 0, 0, null, 0, null, 0, "PENDING", null, null, null
            );

            // Allows the cashier to add food and drinks
            if (inventoryRepo != null && inventoryView != null) {
                addProductsToOrder(transaction);
            }

            ticketView.showMessage("[1] Cash");
            ticketView.showMessage("[2] E-wallet");
            ticketView.showMessage("[3] Card");

            // Repeats the same field until the choice is 1, 2 or 3
            int paymentChoice = ticketView.readInt("Payment method: ", 1, 3);

            while (paymentChoice < 1 || paymentChoice > 3) {
                ticketView.showError("Invalid choice. Enter 1, 2 or 3.");
                paymentChoice = ticketView.readInt("Payment method: ", 1, 3);
            }

            if (paymentChoice == 1) {
                transaction.setPaymentMethod("Cash");
            } else if (paymentChoice == 2) {
                transaction.setPaymentMethod("E-wallet");
            } else {
                transaction.setPaymentMethod("Card");
            }

            // Repeats until the payment covers the total. Enter 0 to cancel the sale.
            double amountPaid = 0;

            while (amountPaid == 0) {

                String input = ticketView.readString("Enter amount paid (0 to go back): ");

                if (input.trim().equals("0")) {
                    ticketView.showMessage("Ticket selling cancelled.");
                    return;
                }

                if (!InputValidator.isPositiveDouble(input)) {
                    ticketView.showError("Enter a valid amount greater than zero.");
                } else if (Double.parseDouble(input.trim()) < transaction.getTotalAmount()) {
                    ticketView.showError("Insufficient payment. Required amount: " + transaction.getTotalAmount());
                } else {
                    amountPaid = Double.parseDouble(input.trim());
                }
            }

            transaction.setAmountPaid(amountPaid);

            Receipt receipt = checkoutService.checkout(transaction);

            // The seats stay reserved from here on
            completed = true;

            receiptView.displayReceipt(receipt);

        } catch (IllegalArgumentException | IllegalStateException e) {
            ticketView.showError(e.getMessage());

        } finally {

            // Rolls back the seat reservations when the sale was cancelled or failed
            if (!completed) {
                for (int i = 0; i < reservedSeatIDs.size(); i++) {
                    try {
                        schedule.cancelBooking(reservedSeatIDs.get(i));
                    } catch (IllegalArgumentException e) {
                        // The show already started, so the seat no longer matters
                    }
                }
            }
        }
    }


    public void addProductsToOrder(Transaction transaction) {

        if (transaction == null || inventoryRepo == null || inventoryView == null) {
            return;
        }

        // Allows the customer or cashier to add multiple products
        while (true) {

            LinkedList<Product> products = inventoryRepo.viewProducts();

            // Only AVAILABLE and LOW_STOCK products can be bought
            LinkedList<Product> availableProducts = new LinkedList<Product>();

            for (int i = 0; products != null && i < products.size(); i++) {

                String status = products.get(i).getStatus();

                if ("AVAILABLE".equals(status) || "LOW_STOCK".equals(status)) {
                    availableProducts.insertAtEnd(products.get(i));
                }
            }

            if (availableProducts.size() == 0) {
                inventoryView.showMessage("No products available for purchase.");
                return;
            }

            inventoryView.displayProducts(availableProducts);

            // Selects a product. Enter 0 to finish adding products.
            Product selectedProduct = null;

            while (selectedProduct == null) {

                String input = inventoryView.promptProductID();

                if (input.trim().equals("0")) {
                    return;
                }

                for (int i = 0; i < availableProducts.size(); i++) {
                    if (availableProducts.get(i).getProductID().equalsIgnoreCase(input.trim())) {
                        selectedProduct = availableProducts.get(i);
                        break;
                    }
                }

                if (selectedProduct == null) {
                    inventoryView.showError("Product not found or not available.");
                }
            }

            // Requests the quantity. Transaction.addProduct checks the quantity and the stock
            // (including what is already in this order). Enter 0 to pick another product.
            boolean added = false;

            while (!added) {

                String input = inventoryView.readString("Enter quantity (0 to go back): ");

                if (input.trim().equals("0")) {
                    break;
                }

                try {

                    if (!InputValidator.isPositiveInt(input)) {
                        throw new IllegalArgumentException("Invalid quantity. Enter a positive whole number.");
                    }

                    transaction.addProduct(selectedProduct, Integer.parseInt(input.trim()));

                    inventoryView.showMessage("Product successfully added to your order.");
                    added = true;

                } catch (IllegalArgumentException | IllegalStateException e) {
                    inventoryView.showError(e.getMessage());
                }
            }
        }
    }


    // Customer
    public void bookTickets(Customer customer) {

        if (customer == null) {
            return;
        }

        // Inactive accounts (or accounts with no status set) cannot book tickets
        try {
            if (!customer.getStatus().equalsIgnoreCase("Active")) {
                ticketView.showError("This account is inactive and cannot make bookings.");
                return;
            }
        } catch (IllegalStateException e) {
            ticketView.showError("This account has no status set and cannot make bookings.");
            return;
        }

        LinkedList<Schedule> schedules = cinemaRepo.getAllSchedules();

        if (schedules == null || schedules.size() == 0) {
            ticketView.showMessage("No available movie schedules.");
            return;
        }

        // Selects a schedule that is open, has a room and has not started.
        // ScheduleView.selectSchedule must return null when the user enters 0 (go back).
        Schedule schedule = scheduleView.selectSchedule(schedules);

        while (schedule != null && !(schedule.isActive() && schedule.getRoom() != null
                && schedule.getShowDateTime() != null
                && schedule.getShowDateTime().isAfter(LocalDateTime.now()))) {

            ticketView.showError("Invalid or inactive movie schedule. Please select another schedule.");
            schedule = scheduleView.selectSchedule(schedules);
        }

        if (schedule == null) {
            return;
        }

        LinkedList<Seat> seats = schedule.getRoom().getSeats();
        LinkedList<String> bookedSeatIDs = schedule.getBookedSeatIDs();

        // Counts the seats that are in service and not booked
        int availableSeats = 0;

        for (int i = 0; i < seats.size(); i++) {
            if (seats.get(i).getStatus() && !bookedSeatIDs.contains(seats.get(i).getSeatID())) {
                availableSeats++;
            }
        }

        if (availableSeats == 0) {
            ticketView.showMessage("No available seats.");
            return;
        }

        seatView.displaySeatGrid(schedule);

        // Repeats until the quantity is valid. 0 goes back.
        int ticketCount;

        while (true) {

            ticketCount = ticketView.promptTicketCount();

            if (ticketCount == 0) {
                return;
            }

            if (ticketCount < 0) {
                ticketView.showError("Ticket quantity must be greater than zero.");
            } else if (ticketCount <= availableSeats) {
                break;
            } else {
                ticketView.showError("Only " + availableSeats + " seats are available.");
            }
        }

        LinkedList<Ticket> selectedTickets = new LinkedList<Ticket>();
        LinkedList<String> reservedSeatIDs = new LinkedList<String>();
        boolean completed = false;

        // The finally block releases the reserved seats unless the booking was saved.
        try {

            String transactionID = history.generateTransactionID();
            String bookingCode = "BK-" + transactionID;

            // Selects one seat per ticket. Enter 0 to cancel the booking.
            for (int ticketNumber = 1; ticketNumber <= ticketCount; ticketNumber++) {

                Seat selectedSeat = null;

                while (selectedSeat == null) {

                    String input = seatView.promptSeatID();

                    if (input.trim().equals("0")) {
                        ticketView.showMessage("Booking cancelled.");
                        return;
                    }

                    try {

                        if (!InputValidator.isValidSeatID(input)) {
                            throw new IllegalArgumentException("Invalid seat ID.");
                        }

                        String seatID = input.trim().toUpperCase();

                        // Checks the schedule is open and not started, the seat exists, is in
                        // service and is not booked (this also stops picking the same seat twice)
                        schedule.bookSeat(seatID);
                        reservedSeatIDs.insertAtEnd(seatID);

                        selectedSeat = schedule.getRoom().findSeat(seatID);

                    } catch (IllegalArgumentException e) {
                        seatView.showError(e.getMessage());
                    }
                }

                // The seat's price modifier is a multiplier of the schedule price
                selectedTickets.insertAtEnd(new Ticket(
                    transactionID + "-T" + ticketNumber,
                    schedule,
                    selectedSeat,
                    schedule.getPrice() * selectedSeat.getPriceModifier(),
                    "BOOKED"
                ));
            }

            Transaction transaction = new Transaction(
                transactionID, "ONLINE", customer.getUserID(), null, LocalDateTime.now(),
                selectedTickets, new LinkedList<OrderProduct>(),
                null, 0, 0, null, 0, null, 0, "PENDING", bookingCode, null, null
            );

            // Allows the customer to add food and drinks
            if (inventoryRepo != null && inventoryView != null) {
                addProductsToOrder(transaction);
            }

            // Stores the pending online booking; the seats stay reserved once it is saved
            history.addTransaction(transaction);
            completed = true;

            ticketView.showMessage("Booking successfully created.");
            ticketView.showMessage("Booking Code: " + bookingCode);
            ticketView.displayTickets(selectedTickets);

        } catch (IllegalArgumentException | IllegalStateException e) {
            ticketView.showError(e.getMessage());

        } finally {

            // Rolls back the seat reservations when the booking was cancelled or failed
            if (!completed) {
                for (int i = 0; i < reservedSeatIDs.size(); i++) {
                    try {
                        schedule.cancelBooking(reservedSeatIDs.get(i));
                    } catch (IllegalArgumentException e) {
                        // The show already started, so the seat no longer matters
                    }
                }
            }
        }
    }


    public void cancelBooking(Customer customer) {

        if (customer == null) {
            return;
        }

        // Only the current customer's transactions
        LinkedList<Transaction> customerTransactions = history.getByCustomer(customer.getUserID());

        if (customerTransactions == null || customerTransactions.size() == 0) {
            ticketView.showMessage("You have no booking history.");
            return;
        }

        ticketView.showMessage("Your Booking History:");

        for (int i = 0; i < customerTransactions.size(); i++) {

            Transaction current = customerTransactions.get(i);

            ticketView.showMessage(
                "Transaction ID: " + current.getTransactionID()
                + " | Booking Code: " + current.getBookingCode()
                + " | Status: " + current.getStatus()
            );
        }

        // Requests the booking code. Enter 0 to go back.
        Transaction transaction = null;

        while (transaction == null) {

            String code = ticketView.readString("Enter booking code (0 to go back): ");

            if (code.trim().equals("0")) {
                return;
            }

            for (int i = 0; i < customerTransactions.size(); i++) {

                String current = customerTransactions.get(i).getBookingCode();

                if (current != null && current.equalsIgnoreCase(code.trim())) {
                    transaction = customerTransactions.get(i);
                    break;
                }
            }

            if (transaction == null) {
                ticketView.showError("Booking not found under this customer.");
                continue;
            }

            // A booking cannot be cancelled when a ticket was used or its show has started
            LinkedList<Ticket> tickets = transaction.getTickets();

            for (int i = 0; transaction != null && i < tickets.size(); i++) {

                Ticket ticket = tickets.get(i);

                if (ticket.getStatus().equalsIgnoreCase("USED")) {
                    ticketView.showError("This booking has used tickets and cannot be cancelled.");
                    transaction = null;
                } else if (!ticket.getSchedule().getShowDateTime().isAfter(LocalDateTime.now())) {
                    ticketView.showError("The show has already started. This booking cannot be cancelled.");
                    transaction = null;
                }
            }
        }

        if (!ticketView.confirm("Are you sure you want to cancel this booking?")) {
            return;
        }

        try {

            if (checkoutService.cancelTransaction(transaction)) {
                ticketView.showMessage("Booking successfully cancelled.");
            } else {
                ticketView.showError("Unable to cancel booking.");
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            ticketView.showError(e.getMessage());
        }
    }
}