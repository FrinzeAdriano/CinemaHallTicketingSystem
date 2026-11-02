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
// Cashier
public void sellTickets(Cashier cashier) {
    if (cashier == null) return;

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

    Schedule schedule = scheduleView.selectSchedule(schedules);

    while (schedule != null && !(schedule.isActive() && schedule.getRoom() != null
            && schedule.getShowDateTime() != null
            && schedule.getShowDateTime().isAfter(LocalDateTime.now()))) {
        ticketView.showError("Invalid or inactive movie schedule. Please select another schedule.");
        schedule = scheduleView.selectSchedule(schedules);
    }

    if (schedule == null) return;

   LinkedList<Ticket> selectedTickets = new LinkedList<Ticket>();
    LinkedList<String> reservedSeatIDs = new LinkedList<String>();
    boolean completed = false;

    try {
        String transactionID = history.generateTransactionID();
        Transaction transaction = null;

        // Repeats seat selection if the cashier goes back from the product menu
        while (transaction == null) {

            // Release seats reserved in a previous pass and start fresh
            releaseSeats(schedule, reservedSeatIDs);
            reservedSeatIDs = new LinkedList<String>();
            selectedTickets = new LinkedList<Ticket>();

            int availableSeats = countAvailableSeats(schedule);

            if (availableSeats == 0) {
                ticketView.showMessage("No available seats.");
                return;
            }

            seatView.displaySeatGrid(schedule);

            int ticketCount;
            while (true) {
                ticketCount = ticketView.promptTicketCount();
                if (ticketCount == 0) return;
                if (ticketCount < 0) {
                    ticketView.showError("Ticket quantity must be greater than zero.");
                } else if (ticketCount <= availableSeats) {
                    break;
                } else {
                    ticketView.showError("Only " + availableSeats + " seats are available.");
                }
            }

            for (int ticketNumber = 1; ticketNumber <= ticketCount; ticketNumber++) {
                Seat selectedSeat = null;
                int remaining = ticketCount - ticketNumber + 1;

                while (selectedSeat == null) {
                    ticketView.showMessage(
                        "Ticket " + ticketNumber + " of " + ticketCount
                        + " (" + remaining + " seat" + (remaining == 1 ? "" : "s")
                        + " left to select)"
                    );

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
                        schedule.bookSeat(seatID);
                        reservedSeatIDs.insertAtEnd(seatID);
                        selectedSeat = schedule.getRoom().findSeat(seatID);
                    } catch (IllegalArgumentException e) {
                        seatView.showError(e.getMessage());
                    }
                }

                selectedTickets.insertAtEnd(new Ticket(
                    transactionID + "-T" + ticketNumber,
                    schedule,
                    selectedSeat,
                    schedule.getPrice() * selectedSeat.getPriceModifier(),
                    "BOOKED"
                ));
            }

            Transaction draft = new Transaction(
                transactionID, "WALK_IN", null, cashier.getUserID(),
                LocalDateTime.now(), selectedTickets,
                new LinkedList<OrderProduct>(),
                null, 0, 0, null,
                null, 0, "PENDING",
                null, null, null
            );

            // Add food and drinks
            if (inventoryRepo != null && inventoryView != null) {
                int result = addProductsToOrder(draft);

                if (result == 3) {
                    ticketView.showMessage("Sale cancelled.");
                    return;
                }

                if (result == 0) {
                    ticketView.showMessage("Going back to seat selection.");
                    continue;   // release seats, show grid, ask quantity and seats again
                }
            }

            transaction = draft;
        }
         // Apply Senior Citizen/PWD discounts to individual tickets
        for (int i = 0; i < selectedTickets.size(); i++) {
            Ticket ticket = selectedTickets.get(i);

            ticketView.showMessage("\nTicket ID: " + ticket.getTicketID());
            ticketView.showMessage("Seat: " + ticket.getSeat().getSeatID());
            ticketView.showMessage("[1] Senior Citizen  [2] PWD  [0] No Discount");

            int choice = ticketView.readInt("Discount: ", 0, 2);
            if (choice == 0) continue;

            String discountType = choice == 1 ? "SENIOR" : "PWD";

            boolean validID = ticketView.confirm(
                "Was a valid " + discountType + " ID presented?"
            );

            if (validID) {
                ticket.applyDiscount(discountType);
                ticketView.showMessage(
                    "Discount applied: " + String.format("%.2f", ticket.getDiscountAmount())
                );
                ticketView.showMessage(
                    "Net ticket price: " + String.format("%.2f", ticket.getNetPrice())
                );
            } else {
                ticketView.showMessage("No discount applied.");
            }
        }

        // Payment summary
        ticketView.showMessage("\n============ PAYMENT SUMMARY =============");
        ticketView.showMessage("Subtotal: " + String.format("%.2f", transaction.getSubtotal()));
        ticketView.showMessage("Discount: -" + String.format("%.2f", transaction.getDiscountAmount()));
        ticketView.showMessage("Cultural Tax: " + String.format("%.2f", transaction.getCulturalTax()));
        ticketView.showMessage("Amusement Tax: " + String.format("%.2f", transaction.getAmusementTax()));
        ticketView.showMessage("VAT: " + String.format("%.2f", transaction.getVat()));
        ticketView.showMessage("Total Due: " + String.format("%.2f", transaction.getTotalAmount()));
        ticketView.showMessage("==========================================");

                boolean paymentChosen = false;

        while (!paymentChosen) {

            ticketView.showMessage("\n============ PAYMENT METHOD ==============");
            ticketView.showMessage("[1] Cash");
            ticketView.showMessage("[2] E-wallet");
            ticketView.showMessage("[3] Card");
            ticketView.showMessage("------------------------------------------");

            int paymentChoice = ticketView.readInt("Payment method: ", 1, 3);

            if (paymentChoice == 1) {
                transaction.setPaymentMethod("Cash");
                transaction.setPaymentProvider(null);
                paymentChosen = true;
            } else {
                transaction.setPaymentMethod(paymentChoice == 2 ? "E-wallet" : "Card");

                String provider = ticketView.promptPaymentProvider(transaction.getPaymentMethod());

                if (provider != null) {
                    transaction.setPaymentProvider(provider);
                    paymentChosen = true;
                }
            }
        }

        double amountPaid = 0;

        if (transaction.getPaymentMethod().equals("Cash")) {

            // Cash: the cashier enters the amount received
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

        } else {

            // E-wallet or Card: the exact total is charged, so no amount is asked
            amountPaid = transaction.getTotalAmount();

            ticketView.showMessage("Payment Amount: " + String.format("%.2f", amountPaid));

            if (!ticketView.confirm("Proceed with " + transaction.getPaymentMethod()
                    + " (" + transaction.getPaymentProvider() + ") payment of "
                    + String.format("%.2f", amountPaid) + "?")) {
                ticketView.showMessage("Ticket selling cancelled.");
                return;
            }
        }

        transaction.setAmountPaid(amountPaid);
        Receipt receipt = checkoutService.checkout(transaction);
        completed = true;
        receiptView.displayReceipt(receipt);

    } catch (IllegalArgumentException | IllegalStateException e) {
        ticketView.showError(e.getMessage());
    } finally {
        if (!completed) {
            for (int i = 0; i < reservedSeatIDs.size(); i++) {
                try {
                    schedule.cancelBooking(reservedSeatIDs.get(i));
                } catch (IllegalArgumentException e) {
                    // The show already started, so the seat cannot be released.
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
private int countAvailableSeats(Schedule schedule) {
    LinkedList<Seat> seats = schedule.getRoom().getSeats();
    LinkedList<String> bookedSeatIDs = schedule.getBookedSeatIDs();
    int available = 0;

    if (seats == null || bookedSeatIDs == null) {
        return 0;
    }

    for (int i = 0; i < seats.size(); i++) {
        Seat seat = seats.get(i);
        if (seat != null && seat.getStatus()
                && !bookedSeatIDs.contains(seat.getSeatID())) {
            available++;
        }
    }
    return available;
}

private void releaseSeats(Schedule schedule, LinkedList<String> seatIDs) {
    for (int i = 0; i < seatIDs.size(); i++) {
        try {
            schedule.cancelBooking(seatIDs.get(i));
        } catch (IllegalArgumentException e) {
            // The show already started, so the seat cannot be released.
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

    LinkedList<Ticket> selectedTickets = new LinkedList<Ticket>();
    LinkedList<String> reservedSeatIDs = new LinkedList<String>();
    boolean completed = false;

    try {
        String transactionID = history.generateTransactionID();
        String bookingCode = "BK-" + transactionID;
        Transaction transaction = null;

        // Repeats seat selection if the customer goes back from the product menu
        while (transaction == null) {

            // Release seats reserved in a previous pass and start fresh
            releaseSeats(schedule, reservedSeatIDs);
            reservedSeatIDs = new LinkedList<String>();
            selectedTickets = new LinkedList<Ticket>();

            int availableSeats = countAvailableSeats(schedule);

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

            // Selects one seat per ticket. Enter 0 to cancel.
            for (int ticketNumber = 1; ticketNumber <= ticketCount; ticketNumber++) {

                Seat selectedSeat = null;
                int remaining = ticketCount - ticketNumber + 1;

                while (selectedSeat == null) {
                    ticketView.showMessage(
                        "Ticket " + ticketNumber + " of " + ticketCount
                        + " (" + remaining + " seat" + (remaining == 1 ? "" : "s")
                        + " left to select)"
                    );

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

                        schedule.bookSeat(seatID);
                        reservedSeatIDs.insertAtEnd(seatID);

                        selectedSeat = schedule.getRoom().findSeat(seatID);

                    } catch (IllegalArgumentException e) {
                        seatView.showError(e.getMessage());
                    }
                }

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
            Transaction draft = new Transaction(
                transactionID, "ONLINE", customer.getUserID(), null,
                LocalDateTime.now(), selectedTickets,
                new LinkedList<OrderProduct>(),
                null, 0, 0, null, null, 0,
                "PENDING", bookingCode, null, null
            );

            // Allows the customer to add food and drinks
            if (inventoryRepo != null && inventoryView != null) {
                int result = addProductsToOrder(draft);

                if (result == 3) {
                    ticketView.showMessage("Booking cancelled.");
                    return;
                }

                if (result == 0) {
                    ticketView.showMessage("Going back to seat selection.");
                    continue;   // loop again: release seats, show grid, ask quantity and seats
                }
            }

            transaction = draft;
        }

        applyOnlineVoucher(transaction);
        
        ticketView.showMessage("\n============ BOOKING SUMMARY =============");
        ticketView.showMessage("Booking Code: " + bookingCode);
        ticketView.showMessage("------------------------------------------");

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
            ticketView.showMessage("------------------------------------------");
        }
    LinkedList<OrderProduct> orderedProducts = transaction.getProducts();

if (orderedProducts != null && orderedProducts.size() > 0) {

            ticketView.showMessage("\n=========== ORDERED PRODUCTS ============");

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
                ticketView.showMessage("------------------------------------------");
            }
        }
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

        ticketView.showMessage("Cultural Tax: " + String.format("%.2f", transaction.getCulturalTax()));
        ticketView.showMessage("Amusement Tax: " + String.format("%.2f", transaction.getAmusementTax()));
        ticketView.showMessage("VAT: " + String.format("%.2f", transaction.getVat()));
        ticketView.showMessage(
            "Total Amount Due: " +
            String.format("%.2f", transaction.getTotalAmount())
        );
        ticketView.showMessage("==========================================");

        // Online payment methods
         String provider = null;

        while (provider == null) {

            ticketView.showMessage("\n============ PAYMENT METHOD ==============");
            ticketView.showMessage("[1] E-wallet");
            ticketView.showMessage("[2] Card");
            ticketView.showMessage("------------------------------------------");

            int paymentChoice =
                ticketView.readInt("Select payment method: ", 1, 2);

            if (paymentChoice == 1) {
                transaction.setPaymentMethod("E-wallet");
            } else {
                transaction.setPaymentMethod("Card");
            }

            provider = ticketView.promptPaymentProvider(transaction.getPaymentMethod());
        }

        transaction.setPaymentProvider(provider);

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