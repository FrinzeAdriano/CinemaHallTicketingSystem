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
    int result = addProductsToOrder(transaction);
    if (result == 0) {
        ticketView.showMessage("Returning to previous menu.");
        return;
    }
    if (result == 3) {
        ticketView.showMessage("Sale cancelled.");
        return;
    }
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

public int addProductsToOrder(Transaction transaction) {

    if (transaction == null) return 0;
    if (inventoryRepo == null || inventoryView == null) return 2;

    while (true) {

        int choice = inventoryView.showProductOrderMenu();

        if (choice == 0 || choice == 2 || choice == 3) return choice;

        LinkedList<Product> products = inventoryRepo.viewProducts();
        LinkedList<Product> available = new LinkedList<Product>();
        LinkedList<OrderProduct> ordered = transaction.getProducts();

        for (int i = 0; i < products.size(); i++) {

            Product product = products.get(i);
            long quantity = 0;

            for (int j = 0; j < ordered.size(); j++) {
                if (ordered.get(j).getProduct().getProductID()
                        .equalsIgnoreCase(product.getProductID())) {
                    quantity += ordered.get(j).getQuantity();
                }
            }

            String status = product.getStatus();

            if (("AVAILABLE".equalsIgnoreCase(status)
                    || "LOW_STOCK".equalsIgnoreCase(status))
                    && product.getStockQuantity() > quantity) {
                available.insertAtEnd(product);
            }
        }

        if (available.size() == 0) {
            inventoryView.showMessage("No products available.");
            continue;
        }

        inventoryView.displayProducts(available);

        Product selected = null;

        while (selected == null) {

            String input = inventoryView.promptProductID();

            if (input.equals("0")) break;

            for (int i = 0; i < available.size(); i++) {
                if (available.get(i).getProductID()
                        .equalsIgnoreCase(input.trim())) {
                    selected = available.get(i);
                    break;
                }
            }

            if (selected == null) {
                inventoryView.showError("Product not found or unavailable.");
            }
        }

        if (selected == null) continue;

        while (true) {

            inventoryView.showMessage("----------------------------------------");
            String input = inventoryView.readString("Enter quantity (0 to go back): ");

            if (input.equals("0")) break;

            if (!InputValidator.isPositiveInt(input)) {
                inventoryView.showError("Invalid quantity. Enter a positive whole number.");
                continue;
            }

            int quantity = Integer.parseInt(input.trim());
            ordered = transaction.getProducts();
            long orderedQuantity = 0;

            for (int i = 0; i < ordered.size(); i++) {
                if (ordered.get(i).getProduct().getProductID()
                        .equalsIgnoreCase(selected.getProductID())) {
                    orderedQuantity += ordered.get(i).getQuantity();
                }
            }

            long remaining = selected.getStockQuantity() - orderedQuantity;

            if (quantity > remaining) {
                inventoryView.showError("Insufficient stock. Available: " + remaining);
                continue;
            }

            try {
                transaction.addProduct(selected, quantity);
                inventoryView.showMessage("Product successfully added to your order.");
                break;
            } catch (IllegalArgumentException | IllegalStateException e) {
                inventoryView.showError(e.getMessage());
            }
        }
    }
}

private void applyOnlineVoucher(Transaction transaction) {

    if (transaction == null || voucherRepo == null) {
        return;
    }

    while (true) {

        String code = ticketView.readString(
            "Enter voucher code (0 to skip): "
        ).trim();

        if (code.equals("0")) {
            ticketView.showMessage("Voucher skipped.");
            return;
        }

        Voucher voucher = voucherRepo.findByCode(code);

        if (voucher == null) {
            ticketView.showError("Voucher code not found.");
            continue;
        }

        if (!voucher.isActive()) {
            ticketView.showError("This voucher is inactive.");
            continue;
        }

        try {

            if (voucher.getExpiryDate().isBefore(java.time.LocalDate.now())) {
                ticketView.showError("This voucher has expired.");
                continue;
            }

            if (voucher.getTimesUsed() >= voucher.getMaxUses()) {
                ticketView.showError("This voucher has reached its usage limit.");
                continue;
            }

            String mode = voucher.getDiscountMode();
            double value = voucher.getValue();

            if (mode.equalsIgnoreCase("PERCENT")) {

                transaction.setDiscountType("VOUCHER");
                transaction.setDiscountRate(value / 100.0);
                transaction.setDiscountFixed(0);

            } else if (mode.equalsIgnoreCase("FIXED")) {

                transaction.setDiscountType("VOUCHER");
                transaction.setDiscountRate(0);
                transaction.setDiscountFixed(value);

            } else {

                ticketView.showError("Invalid voucher discount type.");
                continue;
            }

            transaction.setVoucherCode(voucher.getVoucherCode());

            ticketView.showMessage("Voucher successfully applied!");
            ticketView.showMessage("Voucher: " + voucher.getVoucherCode());
            ticketView.showMessage(
                "Discount: -" +
                String.format("%.2f", transaction.getDiscountAmount())
            );
            ticketView.showMessage(
                "New total: " +
                String.format("%.2f", transaction.getTotalAmount())
            );

            return;

        } catch (IllegalArgumentException | IllegalStateException e) {

            ticketView.showError(e.getMessage());}
    }
}


    // Customer
public void bookTickets(Customer customer) {

    if (customer == null || history == null || cinemaRepo == null
            || scheduleView == null || seatView == null
            || ticketView == null || checkoutService == null
            || receiptView == null) {
        return;
    }

    // Inactive accounts cannot book tickets
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

    // Select an active schedule that has not started
    Schedule schedule = scheduleView.selectSchedule(schedules);

    while (schedule != null && !(schedule.isActive()
            && schedule.getRoom() != null
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

    if (seats == null || bookedSeatIDs == null) {
        ticketView.showError("Seat information is unavailable.");
        return;
    }

    // Counts available seats
    int availableSeats = 0;

    for (int i = 0; i < seats.size(); i++) {
        Seat seat = seats.get(i);

        if (seat != null && seat.getStatus()
                && !bookedSeatIDs.contains(seat.getSeatID())) {
            availableSeats++;
        }
    }

    if (availableSeats == 0) {
        ticketView.showMessage("No available seats.");
        return;
    }

    seatView.displaySeatGrid(schedule);

    // Requests ticket quantity
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

    try {
        String transactionID = history.generateTransactionID();
        String bookingCode = "BK-" + transactionID;

        // Selects one seat per ticket. Enter 0 to cancel.
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

                    // Temporarily reserves the selected seat
                    schedule.bookSeat(seatID);
                    reservedSeatIDs.insertAtEnd(seatID);

                    selectedSeat = schedule.getRoom().findSeat(seatID);

                } catch (IllegalArgumentException e) {
                    seatView.showError(e.getMessage());
                }
            }

            // The seat price modifier is a multiplier
            double ticketPrice =
                schedule.getPrice() * selectedSeat.getPriceModifier();

            selectedTickets.insertAtEnd(new Ticket(
                transactionID + "-T" + ticketNumber,
                schedule,
                selectedSeat,
                ticketPrice,
                "BOOKED"
            ));
        }

        // Creates a pending transaction
        Transaction transaction = new Transaction(
            transactionID, "ONLINE", customer.getUserID(), null,
            LocalDateTime.now(), selectedTickets,
            new LinkedList<OrderProduct>(),
            null, 0, 0, null, 0, null, 0,
            "PENDING", bookingCode, null, null
        );

        // Allows the customer to add food and drinks
        if (inventoryRepo != null && inventoryView != null) {
            int result = addProductsToOrder(transaction);

            if (result == 0) {
            ticketView.showMessage("Returning to booking summary.");
              }

            if (result == 3) {
            ticketView.showMessage("Booking cancelled.");
            return;
}
        }
        applyOnlineVoucher(transaction);
        
        ticketView.showMessage("\n========== BOOKING SUMMARY ==========");
ticketView.showMessage("Booking Code: " + bookingCode);
ticketView.showMessage("--------------------------------------");

for (int i = 0; i < selectedTickets.size(); i++) {

    Ticket ticket = selectedTickets.get(i);

    ticketView.showMessage("Ticket " + (i + 1));
    ticketView.showMessage("Ticket ID: " + ticket.getTicketID());
    ticketView.showMessage("Schedule: " + ticket.getSchedule().getScheduleID());
    ticketView.showMessage("Seat: " + ticket.getSeat().getSeatID());
    ticketView.showMessage(
        "Price: " + String.format("%.2f", ticket.getPrice())
    );
    ticketView.showMessage("Status: " + ticket.getStatus());
    ticketView.showMessage("--------------------------------------");
}
    LinkedList<OrderProduct> orderedProducts = transaction.getProducts();

if (orderedProducts != null && orderedProducts.size() > 0) {

    ticketView.showMessage("\n========== ORDERED PRODUCTS ==========");

    for (int i = 0; i < orderedProducts.size(); i++) {

        OrderProduct orderProduct = orderedProducts.get(i);
        Product product = orderProduct.getProduct();

        double subtotal = product.getPrice() * orderProduct.getQuantity();

        ticketView.showMessage("Product: " + product.getName());
        ticketView.showMessage("Quantity: " + orderProduct.getQuantity());
        ticketView.showMessage(
            "Unit Price: " + String.format("%.2f", product.getPrice())
        );
        ticketView.showMessage(
            "Subtotal: " + String.format("%.2f", subtotal)
        );
        ticketView.showMessage("--------------------------------------");
    }
}
    ticketView.showMessage("--------------------------------------");
        ticketView.showMessage(
            "Subtotal: " + String.format("%.2f", transaction.getSubtotal())
        );

        ticketView.showMessage(
            "Voucher Code: " +
            (transaction.getVoucherCode() == null
                ? "None" : transaction.getVoucherCode())
        );

        ticketView.showMessage(
            "Discount: -" +
            String.format("%.2f", transaction.getDiscountAmount())
        );

        ticketView.showMessage(
            "Tax: " + String.format("%.2f", transaction.getTaxAmount())
        );
        ticketView.showMessage(
            "Total Amount Due: " +
            String.format("%.2f", transaction.getTotalAmount())
        );
ticketView.showMessage("======================================");

// Online payment methods
ticketView.showMessage("\n========== PAYMENT METHOD ==========");
ticketView.showMessage("[1] E-wallet");
ticketView.showMessage("[2] Card");
ticketView.showMessage("--------------------------------------");

int paymentChoice =
    ticketView.readInt("Select payment method: ", 1, 2);

if (paymentChoice == 1) {
    transaction.setPaymentMethod("E-wallet");
} else {
    transaction.setPaymentMethod("Card");
}

        // Requests payment. Enter 0 to cancel the booking.
        // Online payment requires the exact amount
double totalAmount = transaction.getTotalAmount();

ticketView.showMessage(
    "Payment Amount: " + String.format("%.2f", totalAmount)
);

// Confirms the payment
if (!ticketView.confirm(
        "Proceed with " + transaction.getPaymentMethod()
        + " payment of " + String.format("%.2f", totalAmount) + "?")) {

    ticketView.showMessage("Payment cancelled. Booking cancelled.");
    return;
}

transaction.setAmountPaid(totalAmount);

        // Completes checkout, records the transaction, and creates the receipt
        Receipt receipt = checkoutService.checkout(transaction);

        // Seats remain reserved only after successful checkout
        completed = true;

        ticketView.showMessage("Payment successful.");
        ticketView.showMessage("Booking successfully created.");
        receiptView.displayReceipt(receipt);

    } catch (IllegalArgumentException | IllegalStateException e) {
        ticketView.showError(e.getMessage());

    } finally {
        // Releases temporarily reserved seats if payment or checkout fails
        if (!completed) {
            for (int i = 0; i < reservedSeatIDs.size(); i++) {
                try {
                    schedule.cancelBooking(reservedSeatIDs.get(i));
                } catch (IllegalArgumentException e) {
                    // The seat can no longer be released if the show has started
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
            " Booking Code:: " + current.getBookingCode()
            + " | Status: " + current.getStatus()
            );
        }

        // Requests the booking code. Enter 0 to go back.
        Transaction transaction = null;

        while (transaction == null) {

            String code = ticketView.readString(
    "Enter booking code (0 to go back): "
);

if (code.trim().equals("0")) {
    return;
}

for (int i = 0; i < customerTransactions.size(); i++) {

    Transaction current = customerTransactions.get(i);

    String bookingCode = current.getBookingCode();
    String transactionID = current.getTransactionID();

    if ((bookingCode != null
            && bookingCode.equalsIgnoreCase(code.trim()))
            || (transactionID != null
            && transactionID.equalsIgnoreCase(code.trim()))) {

        transaction = current;
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