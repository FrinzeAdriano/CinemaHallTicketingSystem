package controllers;

import datastructures.*;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
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
       public void sellTickets(final Cashier cashier) {

        if (cashier == null || cinemaRepo == null || history == null
                || scheduleView == null || seatView == null
                || ticketView == null || checkoutService == null
                || receiptView == null) {

            return;
        }

        // Gets all available schedules
        LinkedList<Schedule> schedules =
            cinemaRepo.getAllSchedules();

        if (schedules == null || schedules.size() == 0) {
            ticketView.showMessage(
                "No available movie schedules."
            );
            return;
        }

        // Selects a valid schedule
        Schedule selectedSchedule;

        while (true) {

            selectedSchedule = scheduleView.selectSchedule(schedules);

            if (selectedSchedule == null) {
                return;
            }

            if (selectedSchedule.isActive()
                    && selectedSchedule.getShowDateTime() != null
                    && selectedSchedule.getShowDateTime().isAfter(LocalDateTime.now())) {

                break;
            }

            ticketView.showError(
                "Invalid or inactive movie schedule. Please select another schedule."
            );
        }

        final Schedule schedule = selectedSchedule;

        // Gets the cinema room
        CinemaRoom room = schedule.getRoom();

        if (room == null) {
            ticketView.showError(
                "The selected schedule has no assigned cinema room."
            );
            return;
        }

        // Gets the room seats and booked seat IDs
        final LinkedList<Seat> seats = room.getSeats();

        final LinkedList<String> bookedSeatIDs =
            schedule.getBookedSeatIDs();

        if (seats == null || seats.size() == 0
                || bookedSeatIDs == null) {

            ticketView.showError(
                "Cinema room seat information is unavailable."
            );
            return;
        }

        // Counts available seats
        int availableSeats = 0;

        for (int i = 0; i < seats.size(); i++) {

            Seat seat = seats.get(i);

            if (seat == null || seat.getSeatID() == null) {
                continue;
            }

            boolean isBooked = false;

            for (int j = 0; j < bookedSeatIDs.size(); j++) {

                String bookedID = bookedSeatIDs.get(j);

                if (bookedID != null
                        && bookedID.equalsIgnoreCase(seat.getSeatID())) {

                    isBooked = true;
                    break;
                }
            }

            if (!isBooked) {
                availableSeats++;
            }
        }

        if (availableSeats == 0) {
            ticketView.showMessage("No available seats.");
            return;
        }

        // Displays the seat layout
        seatView.displaySeatGrid(schedule);

        // Requests ticket quantity
        int ticketCount;

        while (true) {

            ticketCount = ticketView.promptTicketCount();

            if (ticketCount > 0 && ticketCount <= availableSeats) {
                break;
            }

            if (ticketCount <= 0) {
                ticketView.showError(
                    "Ticket quantity must be greater than zero."
                );
            } else {
                ticketView.showError(
                    "Only " + availableSeats + " seats are available."
                );
            }
        }

        // Generates transaction ID once
        String transactionID;

        try {

            transactionID = history.generateTransactionID();

        } catch (IllegalStateException e) {

            ticketView.showError(e.getMessage());
            return;
        }

        // Stores selected tickets
        final LinkedList<Ticket> selectedTickets =
            new LinkedList<Ticket>();

        int ticketNumber = 1;

        // Selects seats
        while (ticketNumber <= ticketCount) {

            final LinkedList<Ticket> currentTickets =
                selectedTickets;

            // Repeats until a valid seat ID is entered.
            // Enter 0 to cancel ticket selling.
            Seat selectedSeat = LoopUtility.retryUntilValidResult(

                new Supplier<String>() {
                    @Override
                    public String get() {
                        return seatView.promptSeatID();
                    }
                },

                new Function<String, Seat>() {
                    @Override
                    public Seat apply(String seatID) {

                        if (!InputValidator.isValidSeatID(seatID)) {
                            throw new IllegalArgumentException(
                                "Invalid seat ID."
                            );
                        }

                        String formattedSeatID = seatID.trim();

                        Seat foundSeat = null;

                        // Searches for the selected seat
                        for (int i = 0; i < seats.size(); i++) {

                            Seat seat = seats.get(i);

                            if (seat != null && seat.getSeatID() != null
                                    && seat.getSeatID()
                                        .equalsIgnoreCase(formattedSeatID)) {

                                foundSeat = seat;
                                break;
                            }
                        }

                        if (foundSeat == null) {
                            throw new IllegalArgumentException(
                                "Seat does not exist."
                            );
                        }

                        // Checks whether the seat is booked
                        for (int i = 0; i < bookedSeatIDs.size(); i++) {

                            String bookedID = bookedSeatIDs.get(i);

                            if (bookedID != null
                                    && bookedID.equalsIgnoreCase(
                                        foundSeat.getSeatID())) {

                                throw new IllegalArgumentException(
                                    "Seat is already booked."
                                );
                            }
                        }

                        // Checks for duplicate seat selection
                        for (int i = 0; i < currentTickets.size(); i++) {

                            Ticket selectedTicket = currentTickets.get(i);

                            if (selectedTicket != null
                                    && selectedTicket.getSeat() != null
                                    && selectedTicket.getSeat().getSeatID()
                                        .equalsIgnoreCase(
                                            foundSeat.getSeatID())) {

                                throw new IllegalArgumentException(
                                    "You already selected that seat."
                                );
                            }
                        }

                        return foundSeat;
                    }
                },

                new Consumer<String>() {
                    @Override
                    public void accept(String message) {
                        seatView.showError(message);
                    }
                },

                "0"
            );

            // Stops selling if the cashier enters 0
            if (selectedSeat == null) {
                ticketView.showMessage(
                    "Ticket selling cancelled."
                );
                return;
            }

            // Calculates ticket price
            double ticketPrice =
                schedule.getPrice() + selectedSeat.getPriceModifier();

            // Creates the ticket
            try {

                String ticketID =
                    transactionID + "-T" + ticketNumber;

                Ticket ticket = new Ticket(
                    ticketID,
                    schedule,
                    selectedSeat,
                    ticketPrice,
                    "BOOKED"
                );

                selectedTickets.insertAtEnd(ticket);

                ticketNumber++;

            } catch (IllegalArgumentException e) {

                ticketView.showError(e.getMessage());
                return;
            }
        }

        // Creates the walk-in transaction
        Transaction transaction;

        try {

            transaction = new Transaction(
                transactionID,
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

        } catch (IllegalArgumentException e) {

            ticketView.showError(e.getMessage());
            return;
        }

        // Allows the cashier to add food and drinks
        if (inventoryRepo != null && inventoryView != null) {
            addProductsToOrder(transaction);
        }

        // Selects the payment method
        ticketView.showMessage("[1] Cash");
        ticketView.showMessage("[2] E-wallet");
        ticketView.showMessage("[3] Card");

        int paymentChoice =
            ticketView.readInt("Payment method: ", 1, 3);

        try {

            if (paymentChoice == 1) {
                transaction.setPaymentMethod("Cash");
            } else if (paymentChoice == 2) {
                transaction.setPaymentMethod("E-wallet");
            } else {
                transaction.setPaymentMethod("Card");
            }

        } catch (IllegalArgumentException e) {

            ticketView.showError(e.getMessage());
            return;
        }

        // Requests amount paid.
        // Repeats the same field until payment is sufficient.
        Double amountPaid = LoopUtility.retryUntilValidResult(

            new Supplier<String>() {
                @Override
                public String get() {
                    return ticketView.readString(
                        "Enter amount paid: "
                    );
                }
            },

            new Function<String, Double>() {
                @Override
                public Double apply(String amountInput) {

                    if (!InputValidator.isPositiveDouble(amountInput)) {
                        throw new IllegalArgumentException(
                            "Enter a valid amount greater than zero."
                        );
                    }

                    double amount =
                        Double.parseDouble(amountInput.trim());

                    if (amount < transaction.getTotalAmount()) {
                        throw new IllegalArgumentException(
                            "Insufficient payment. Required amount: "
                            + transaction.getTotalAmount()
                        );
                    }

                    return amount;
                }
            },

            new Consumer<String>() {
                @Override
                public void accept(String message) {
                    ticketView.showError(message);
                }
            },

            null
        );

        if (amountPaid == null) {
            return;
        }

        // Stores the amount paid
        try {

            transaction.setAmountPaid(amountPaid);

        } catch (IllegalArgumentException e) {

            ticketView.showError(e.getMessage());
            return;
        }

        // Processes checkout
        try {

            Receipt receipt = checkoutService.checkout(transaction);

            if (receipt == null) {
                ticketView.showError(
                    "Checkout could not generate a receipt."
                );
                return;
            }

            // Reserves selected seats after successful checkout
            for (int i = 0; i < selectedTickets.size(); i++) {

                Ticket ticket = selectedTickets.get(i);

                bookedSeatIDs.insertAtEnd(
                    ticket.getSeat().getSeatID()
                );
            }

            // Displays the receipt
            receiptView.displayReceipt(receipt);

        } catch (IllegalArgumentException | IllegalStateException e) {

            ticketView.showError(e.getMessage());
        }
    }


     public void addProductsToOrder(final Transaction transaction) {

        if (transaction == null || inventoryRepo == null
                || inventoryView == null) {

            return;
        }

        // Allows the customer or cashier to add multiple products
        while (true) {

            LinkedList<Product> products =
                inventoryRepo.viewProducts();

            if (products == null || products.size() == 0) {
                inventoryView.showMessage(
                    "No products available."
                );
                return;
            }

            // Displays only products available for purchase
            LinkedList<Product> availableProducts =
                new LinkedList<Product>();

            LinkedList<OrderProduct> orderedProducts =
                transaction.getProducts();

            for (int i = 0; i < products.size(); i++) {

                Product product = products.get(i);

                if (product == null || product.getProductID() == null) {
                    continue;
                }

                long orderedQuantity = 0;

                if (orderedProducts != null) {

                    for (int j = 0; j < orderedProducts.size(); j++) {

                        OrderProduct orderProduct = orderedProducts.get(j);

                        if (orderProduct != null
                                && orderProduct.getProduct() != null
                                && orderProduct.getProduct().getProductID() != null
                                && orderProduct.getProduct().getProductID()
                                    .equalsIgnoreCase(product.getProductID())) {

                            orderedQuantity += orderProduct.getQuantity();
                        }
                    }
                }

                // Only AVAILABLE and LOW_STOCK products can be purchased
                String status = product.getStatus();

                if (("AVAILABLE".equalsIgnoreCase(status)
                        || "LOW_STOCK".equalsIgnoreCase(status))
                        && product.getStockQuantity() > orderedQuantity) {

                    availableProducts.insertAtEnd(product);
                }
            }

            if (availableProducts.size() == 0) {
                inventoryView.showMessage(
                    "No more products are available for purchase."
                );
                return;
            }

            inventoryView.displayProducts(availableProducts);

            // Requests product ID.
            // Enter 0 to finish adding products.
            Product selectedProduct = LoopUtility.retryUntilValidResult(

                new Supplier<String>() {
                    @Override
                    public String get() {
                        return inventoryView.promptProductID();
                    }
                },

                new Function<String, Product>() {
                    @Override
                    public Product apply(String productID) {

                        if (!InputValidator.isNotBlank(productID)) {
                            throw new IllegalArgumentException(
                                "Product ID cannot be blank."
                            );
                        }

                        // Searches for the selected product
                        LinkedList<Product> currentProducts =
                            inventoryRepo.viewProducts();

                        if (currentProducts == null) {
                            throw new IllegalArgumentException(
                                "Unable to retrieve products."
                            );
                        }

                        Product foundProduct = null;

                        for (int i = 0; i < currentProducts.size(); i++) {

                            Product product = currentProducts.get(i);

                            if (product != null
                                    && product.getProductID() != null
                                    && product.getProductID()
                                        .equalsIgnoreCase(productID.trim())) {

                                foundProduct = product;
                                break;
                            }
                        }

                        if (foundProduct == null) {
                            throw new IllegalArgumentException(
                                "Product not found."
                            );
                        }

                        // Checks product status and stock
                        String status = foundProduct.getStatus();

                        if ((! "AVAILABLE".equalsIgnoreCase(status)
                                && ! "LOW_STOCK".equalsIgnoreCase(status))
                                || foundProduct.getStockQuantity() <= 0) {

                            throw new IllegalArgumentException(
                                "This product is not available for purchase."
                            );
                        }

                        // Checks how much of this product is already ordered
                        long orderedQuantity = 0;

                        LinkedList<OrderProduct> currentOrder =
                            transaction.getProducts();

                        if (currentOrder != null) {

                            for (int i = 0; i < currentOrder.size(); i++) {

                                OrderProduct orderProduct = currentOrder.get(i);

                                if (orderProduct != null
                                        && orderProduct.getProduct() != null
                                        && orderProduct.getProduct().getProductID() != null
                                        && orderProduct.getProduct().getProductID()
                                            .equalsIgnoreCase(
                                                foundProduct.getProductID())) {

                                    orderedQuantity += orderProduct.getQuantity();
                                }
                            }
                        }

                        if (foundProduct.getStockQuantity() <= orderedQuantity) {

                            throw new IllegalArgumentException(
                                "No remaining stock for this product. Please select another product."
                            );
                        }

                        return foundProduct;
                    }
                },

                new Consumer<String>() {
                    @Override
                    public void accept(String message) {
                        inventoryView.showError(message);
                    }
                },

                "0"
            );

            // Ends product ordering if customer or cashier enters 0
            if (selectedProduct == null) {
                break;
            }

            // Requests quantity.
            // Repeats the quantity field if invalid or beyond stock.
            Integer quantity = LoopUtility.retryUntilValidResult(

                new Supplier<String>() {
                    @Override
                    public String get() {
                        return inventoryView.readString(
                            "Enter quantity: "
                        );
                    }
                },

                new Function<String, Integer>() {
                    @Override
                    public Integer apply(String quantityInput) {

                        if (!InputValidator.isPositiveInt(quantityInput)) {
                            throw new IllegalArgumentException(
                                "Invalid quantity. Enter a positive whole number."
                            );
                        }

                        int enteredQuantity =
                            Integer.parseInt(quantityInput.trim());

                        long orderedQuantity = 0;

                        LinkedList<OrderProduct> currentOrder =
                            transaction.getProducts();

                        if (currentOrder != null) {

                            for (int i = 0; i < currentOrder.size(); i++) {

                                OrderProduct orderProduct =
                                    currentOrder.get(i);

                                if (orderProduct != null
                                        && orderProduct.getProduct() != null
                                        && orderProduct.getProduct().getProductID() != null
                                        && orderProduct.getProduct().getProductID()
                                            .equalsIgnoreCase(
                                                selectedProduct.getProductID())) {

                                    orderedQuantity += orderProduct.getQuantity();
                                }
                            }
                        }

                        if (orderedQuantity + enteredQuantity
                                > selectedProduct.getStockQuantity()) {

                            throw new IllegalArgumentException(
                                "Insufficient stock. Available quantity: "
                                + (selectedProduct.getStockQuantity()
                                - orderedQuantity)
                            );
                        }

                        return enteredQuantity;
                    }
                },

                new Consumer<String>() {
                    @Override
                    public void accept(String message) {
                        inventoryView.showError(message);
                    }
                },

                null
            );

            if (quantity == null) {
                return;
            }

            // Adds the product to the transaction
            try {

                transaction.addProduct(
                    selectedProduct,
                    quantity
                );

                inventoryView.showMessage(
                    "Product successfully added to your order."
                );

            } catch (IllegalArgumentException | IllegalStateException e) {

                inventoryView.showError(e.getMessage());
            }
        }
    }


    // Customer
        public void bookTickets(final Customer customer) {

        if (customer == null || cinemaRepo == null || history == null
                || scheduleView == null || seatView == null
                || ticketView == null) {

            return;
        }

        // Gets all available schedules
        LinkedList<Schedule> schedules =
            cinemaRepo.getAllSchedules();

        if (schedules == null || schedules.size() == 0) {
            ticketView.showMessage(
                "No available movie schedules."
            );
            return;
        }

        // Selects a valid schedule
        Schedule selectedSchedule;

        while (true) {

            selectedSchedule = scheduleView.selectSchedule(schedules);

            if (selectedSchedule == null) {
                return;
            }

            if (selectedSchedule.isActive()
                    && selectedSchedule.getShowDateTime() != null
                    && selectedSchedule.getShowDateTime().isAfter(LocalDateTime.now())) {

                break;
            }

            ticketView.showError(
                "Invalid or inactive movie schedule. Please select another schedule."
            );
        }

        final Schedule schedule = selectedSchedule;

        // Gets the cinema room
        CinemaRoom room = schedule.getRoom();

        if (room == null) {
            ticketView.showError(
                "The selected schedule has no assigned cinema room."
            );
            return;
        }

        // Gets the room seats and booked seat IDs
        final LinkedList<Seat> seats = room.getSeats();

        final LinkedList<String> bookedSeatIDs =
            schedule.getBookedSeatIDs();

        if (seats == null || seats.size() == 0
                || bookedSeatIDs == null) {

            ticketView.showError(
                "Cinema room seat information is unavailable."
            );
            return;
        }

        // Counts available seats
        int availableSeats = 0;

        for (int i = 0; i < seats.size(); i++) {

            Seat seat = seats.get(i);

            if (seat == null || seat.getSeatID() == null) {
                continue;
            }

            boolean isBooked = false;

            for (int j = 0; j < bookedSeatIDs.size(); j++) {

                String bookedID = bookedSeatIDs.get(j);

                if (bookedID != null
                        && bookedID.equalsIgnoreCase(seat.getSeatID())) {

                    isBooked = true;
                    break;
                }
            }

            if (!isBooked) {
                availableSeats++;
            }
        }

        if (availableSeats == 0) {
            ticketView.showMessage("No available seats.");
            return;
        }

        // Displays the seat layout
        seatView.displaySeatGrid(schedule);

        // Requests ticket quantity
        int ticketCount;

        while (true) {

            ticketCount = ticketView.promptTicketCount();

            if (ticketCount > 0 && ticketCount <= availableSeats) {
                break;
            }

            if (ticketCount <= 0) {
                ticketView.showError(
                    "Ticket quantity must be greater than zero."
                );
            } else {
                ticketView.showError(
                    "Only " + availableSeats + " seats are available."
                );
            }
        }

        // Generates transaction ID once
        String transactionID;

        try {

            transactionID = history.generateTransactionID();

        } catch (IllegalStateException e) {

            ticketView.showError(e.getMessage());
            return;
        }

        String bookingCode = "BK-" + transactionID;

        // Stores selected tickets
        final LinkedList<Ticket> selectedTickets =
            new LinkedList<Ticket>();

        int ticketNumber = 1;

        // Selects seats
        while (ticketNumber <= ticketCount) {

            // Repeats until a valid seat is selected.
            // Enter 0 to cancel booking.
            Seat selectedSeat = LoopUtility.retryUntilValidResult(

                new Supplier<String>() {
                    @Override
                    public String get() {
                        return seatView.promptSeatID();
                    }
                },

                new Function<String, Seat>() {
                    @Override
                    public Seat apply(String seatID) {

                        if (!InputValidator.isValidSeatID(seatID)) {
                            throw new IllegalArgumentException(
                                "Invalid seat ID."
                            );
                        }

                        String formattedSeatID = seatID.trim();

                        Seat foundSeat = null;

                        // Searches for the selected seat
                        for (int i = 0; i < seats.size(); i++) {

                            Seat seat = seats.get(i);

                            if (seat != null && seat.getSeatID() != null
                                    && seat.getSeatID()
                                        .equalsIgnoreCase(formattedSeatID)) {

                                foundSeat = seat;
                                break;
                            }
                        }

                        if (foundSeat == null) {
                            throw new IllegalArgumentException(
                                "Seat does not exist."
                            );
                        }

                        // Checks whether the seat is booked
                        for (int i = 0; i < bookedSeatIDs.size(); i++) {

                            String bookedID = bookedSeatIDs.get(i);

                            if (bookedID != null
                                    && bookedID.equalsIgnoreCase(
                                        foundSeat.getSeatID())) {

                                throw new IllegalArgumentException(
                                    "Seat is already booked."
                                );
                            }
                        }

                        // Checks for duplicate seat selection
                        for (int i = 0; i < selectedTickets.size(); i++) {

                            Ticket selectedTicket = selectedTickets.get(i);

                            if (selectedTicket != null
                                    && selectedTicket.getSeat() != null
                                    && selectedTicket.getSeat().getSeatID()
                                        .equalsIgnoreCase(
                                            foundSeat.getSeatID())) {

                                throw new IllegalArgumentException(
                                    "You already selected that seat."
                                );
                            }
                        }

                        return foundSeat;
                    }
                },

                new Consumer<String>() {
                    @Override
                    public void accept(String message) {
                        seatView.showError(message);
                    }
                },

                "0"
            );

            // Stops booking if customer enters 0
            if (selectedSeat == null) {
                ticketView.showMessage(
                    "Booking cancelled."
                );
                return;
            }

            // Calculates ticket price
            double ticketPrice =
                schedule.getPrice() + selectedSeat.getPriceModifier();

            // Creates the ticket
            try {

                String ticketID =
                    transactionID + "-T" + ticketNumber;

                Ticket ticket = new Ticket(
                    ticketID,
                    schedule,
                    selectedSeat,
                    ticketPrice,
                    "BOOKED"
                );

                selectedTickets.insertAtEnd(ticket);

                ticketNumber++;

            } catch (IllegalArgumentException e) {

                ticketView.showError(e.getMessage());
                return;
            }
        }

        // Creates the online transaction
        Transaction transaction;

        try {

            transaction = new Transaction(
                transactionID,
                "ONLINE",
                customer.getUserID(),
                null,
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
                bookingCode,
                null,
                null
            );

            // Allows customer to add food and drinks
            if (inventoryRepo != null && inventoryView != null) {
                addProductsToOrder(transaction);
            }

            // Stores the pending online booking
            history.addTransaction(transaction);

        } catch (IllegalArgumentException | IllegalStateException e) {

            ticketView.showError(e.getMessage());
            return;
        }

        // Reserves selected seats
        for (int i = 0; i < selectedTickets.size(); i++) {

            Ticket ticket = selectedTickets.get(i);

            bookedSeatIDs.insertAtEnd(
                ticket.getSeat().getSeatID()
            );
        }

        // Displays booking information
        ticketView.showMessage(
            "Booking successfully created."
        );

        ticketView.showMessage(
            "Booking Code: " + bookingCode
        );

        ticketView.displayTickets(selectedTickets);
    }

      public void cancelBooking(final Customer customer) {

        if (customer == null || history == null
                || ticketView == null || checkoutService == null) {

            return;
        }

        // Gets only the current customer's transactions
        final LinkedList<Transaction> customerTransactions =
            history.getByCustomer(customer.getUserID());

        if (customerTransactions == null
                || customerTransactions.size() == 0) {

            ticketView.showMessage(
                "You have no booking history."
            );
            return;
        }

        // Displays the customer's booking information
        ticketView.showMessage("Your Booking History:");

        for (int i = 0; i < customerTransactions.size(); i++) {

            Transaction current = customerTransactions.get(i);

            if (current != null) {

                ticketView.showMessage(
                    "Transaction ID: " + current.getTransactionID()
                    + " | Booking Code: " + current.getBookingCode()
                    + " | Status: " + current.getStatus()
                );
            }
        }

        // Requests booking code.
        // Enter 0 to go back.
        Transaction transaction = LoopUtility.retryUntilValidResult(

            new Supplier<String>() {
                @Override
                public String get() {
                    return ticketView.readString(
                        "Enter booking code (0 to go back): "
                    );
                }
            },

            new Function<String, Transaction>() {
                @Override
                public Transaction apply(String bookingCode) {

                    if (!InputValidator.isNotBlank(bookingCode)) {
                        throw new IllegalArgumentException(
                            "Booking code cannot be blank."
                        );
                    }

                    // Searches only the current customer's transactions
                    for (int i = 0; i < customerTransactions.size(); i++) {

                        Transaction current = customerTransactions.get(i);

                        if (current == null) {
                            continue;
                        }

                        if (current.getBookingCode() != null
                                && current.getBookingCode()
                                    .equalsIgnoreCase(bookingCode.trim())) {

                            // Checks transaction ownership
                            if (current.getCustomerID() == null
                                    || !current.getCustomerID()
                                        .equalsIgnoreCase(customer.getUserID())) {

                                throw new IllegalArgumentException(
                                    "You cannot cancel another customer's booking."
                                );
                            }

                            return current;
                        }
                    }

                    throw new IllegalArgumentException(
                        "Booking not found under this customer."
                    );
                }
            },

            new Consumer<String>() {
                @Override
                public void accept(String message) {
                    ticketView.showError(message);
                }
            },

            "0"
        );

        // Returns to the previous menu if customer enters 0
        if (transaction == null) {
            return;
        }

        // Requests cancellation confirmation
        if (!ticketView.confirm(
                "Are you sure you want to cancel this booking?")) {

            return;
        }

        // Cancels the booking
        try {

            boolean cancelled =
                checkoutService.cancelTransaction(transaction);

            if (cancelled) {

                ticketView.showMessage(
                    "Booking successfully cancelled."
                );

            } else {

                ticketView.showError(
                    "Unable to cancel booking."
                );
            }

        } catch (IllegalArgumentException | IllegalStateException e) {

            ticketView.showError(e.getMessage());
        }
    }
}
