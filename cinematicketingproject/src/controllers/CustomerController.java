package controllers;
import datastructures.*;
import models.*;
import repositories.*;
import view.*;
import utility.*;
import service.*;
import java.time.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CustomerController {

    private UserRepository userRepo;
    private MovieRepository movieRepo;
    private CinemaRepository cinemaRepo;
    private TransactionHistory history;
    private VoucherRepository voucherRepo;
    private CheckoutService checkoutService;

    private UserView userView;
    private MovieRepositoryView movieListView;
    private ScheduleView scheduleView;
    private SeatView seatView;
    private TicketView ticketView;
    private TransactionHistoryView historyView;
    private ReceiptView receiptView;

    
    private InventoryRepository inventoryRepo; //I think need since may order prodcut din tayo sa online 
    private InventoryView inventoryView;
    // Customer Methods

    public void start(Customer customer) {

    }

    public void manageProfile(Customer customer) {

    }

    public void editProfile(Customer customer) {

    }

    public void changePassword(Customer customer) {

    }


    // Registration Method

    public void register() {

    }


    // Movie Browsing Method

    public void browseMovies() {

    }


     // Books tickets for an online customer
        public void bookTickets(Customer customer) {

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


        // Selects a schedule
        Schedule schedule = scheduleView.selectSchedule(schedules);

        if (schedule == null) {
            return;
        }


        // Checks the selected schedule
        if (!schedule.isActive()
                || schedule.getShowDateTime() == null
                || !schedule.getShowDateTime().isAfter(LocalDateTime.now())) {

            ticketView.showError(
                "Invalid or inactive movie schedule."
            );
            return;
        }


        // Gets the cinema room
        CinemaRoom room = schedule.getRoom();

        if (room == null) {
            ticketView.showError(
                "The selected schedule has no assigned cinema room."
            );
            return;
        }


        // Gets the room seats and booked seat IDs
        LinkedList<Seat> seats = room.getSeats();

        LinkedList<String> bookedSeatIDs =
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
        LinkedList<Ticket> selectedTickets =
            new LinkedList<Ticket>();

        int ticketNumber = 1;


        // Selects seats
        while (ticketNumber <= ticketCount) {

            final Seat[] selectedSeatHolder = new Seat[1];


            // Repeats until a valid seat is selected
            LoopUtility.retryUntilValid(

                new Supplier<String>() {
                    @Override
                    public String get() {
                        return seatView.promptSeatID();
                    }
                },

                new Consumer<String>() {
                    @Override
                    public void accept(String seatID) {

                        // Validates seat ID format
                        if (!InputValidator.isValidSeatID(seatID)) {
                            throw new IllegalArgumentException(
                                "Invalid seat ID."
                            );
                        }

                        String formattedSeatID = seatID.trim();

                        Seat selectedSeat = null;


                        // Searches for the selected seat
                        for (int i = 0; i < seats.size(); i++) {

                            Seat seat = seats.get(i);

                            if (seat != null && seat.getSeatID() != null
                                    && seat.getSeatID()
                                        .equalsIgnoreCase(formattedSeatID)) {

                                selectedSeat = seat;
                                break;
                            }
                        }

                        if (selectedSeat == null) {
                            throw new IllegalArgumentException(
                                "Seat does not exist."
                            );
                        }


                        // Checks whether the seat is booked
                        for (int i = 0; i < bookedSeatIDs.size(); i++) {

                            String bookedID = bookedSeatIDs.get(i);

                            if (bookedID != null
                                    && bookedID.equalsIgnoreCase(
                                        selectedSeat.getSeatID())) {

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
                                            selectedSeat.getSeatID())) {

                                throw new IllegalArgumentException(
                                    "You already selected that seat."
                                );
                            }
                        }


                        // Stores the valid seat
                        selectedSeatHolder[0] = selectedSeat;
                    }
                },

                new Consumer<String>() {
                    @Override
                    public void accept(String message) {
                        seatView.showError(message);
                    }
                }
            );


            Seat selectedSeat = selectedSeatHolder[0];


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


        // Creates the transaction
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

        // Adds products to an online customer's transaction wala toh pero I think need 
        public void addProductsToOrder(final Transaction transaction) {

        if (transaction == null || inventoryRepo == null
                || inventoryView == null) {

            return;
        }


        // Allows the customer to add multiple products
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


            final Product[] selectedProductHolder = new Product[1];
            final boolean[] finishOrdering = new boolean[1];


            // Repeats until a valid product ID is entered
            LoopUtility.retryUntilValid(

                new Supplier<String>() {
                    @Override
                    public String get() {
                        return inventoryView.readString(
                            "Enter product ID (0 to finish): "
                        );
                    }
                },

                new Consumer<String>() {
                    @Override
                    public void accept(String productID) {

                        if (!InputValidator.isNotBlank(productID)) {
                            throw new IllegalArgumentException(
                                "Product ID cannot be blank."
                            );
                        }


                        // Allows the customer to finish ordering
                        if (productID.trim().equals("0")) {
                            finishOrdering[0] = true;
                            return;
                        }


                        Product selectedProduct = null;

                        LinkedList<Product> currentProducts =
                            inventoryRepo.viewProducts();

                        if (currentProducts == null) {
                            throw new IllegalArgumentException(
                                "Unable to retrieve products."
                            );
                        }


                        // Searches for the selected product
                        for (int i = 0; i < currentProducts.size(); i++) {

                            Product product = currentProducts.get(i);

                            if (product != null
                                    && product.getProductID() != null
                                    && product.getProductID()
                                        .equalsIgnoreCase(productID.trim())) {

                                selectedProduct = product;
                                break;
                            }
                        }


                        if (selectedProduct == null) {
                            throw new IllegalArgumentException(
                                "Product not found."
                            );
                        }


                        // Checks product status and stock
                        String status = selectedProduct.getStatus();

                        if ((! "AVAILABLE".equalsIgnoreCase(status)
                                && ! "LOW_STOCK".equalsIgnoreCase(status))
                                || selectedProduct.getStockQuantity() <= 0) {

                            throw new IllegalArgumentException(
                                "This product is not available for purchase."
                            );
                        }


                        selectedProductHolder[0] = selectedProduct;
                    }
                },

                new Consumer<String>() {
                    @Override
                    public void accept(String message) {
                        inventoryView.showError(message);
                    }
                }
            );


            // Ends product ordering if customer enters 0
            if (finishOrdering[0]) {
                break;
            }


            final int[] quantityHolder = new int[1];


            // Repeats until quantity is valid and within stock
            LoopUtility.retryUntilValid(

                new Supplier<String>() {
                    @Override
                    public String get() {
                        return inventoryView.readString(
                            "Enter quantity: "
                        );
                    }
                },

                new Consumer<String>() {
                    @Override
                    public void accept(String quantityInput) {

                        if (!InputValidator.isPositiveInt(quantityInput)) {
                            throw new IllegalArgumentException(
                                "Invalid quantity. Enter a positive whole number."
                            );
                        }

                        int quantity =
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
                                                selectedProductHolder[0].getProductID())) {

                                    orderedQuantity += orderProduct.getQuantity();
                                }
                            }
                        }


                        if (orderedQuantity + quantity
                                > selectedProductHolder[0].getStockQuantity()) {

                            throw new IllegalArgumentException(
                                "Insufficient stock. Available quantity: "
                                + (selectedProductHolder[0].getStockQuantity()
                                - orderedQuantity)
                            );
                        }

                        quantityHolder[0] = quantity;
                    }
                },

                new Consumer<String>() {
                    @Override
                    public void accept(String message) {
                        inventoryView.showError(message);
                    }
                }
            );


            // Adds the product to the transaction
            try {

                transaction.addProduct(
                    selectedProductHolder[0],
                    quantityHolder[0]
                );

                inventoryView.showMessage(
                    "Product successfully added to your order."
                );

            } catch (IllegalArgumentException | IllegalStateException e) {

                inventoryView.showError(e.getMessage());
            }
        }
    }

        
        
    // Cancels a customer's booking
     public void cancelBooking(final Customer customer) {

        if (customer == null || history == null
                || historyView == null || checkoutService == null) {

            return;
        }


        // Gets only the current customer's transactions
        final LinkedList<Transaction> customerTransactions =
            history.getByCustomer(customer.getUserID());

        if (customerTransactions == null
                || customerTransactions.size() == 0) {

            historyView.showMessage(
                "You have no booking history."
            );
            return;
        }


        historyView.displayTransactions(customerTransactions);


        final Transaction[] transactionHolder =
            new Transaction[1];

        final boolean[] cancelInput = new boolean[1];


        // Repeats until a valid booking code is entered
        LoopUtility.retryUntilValid(

            new Supplier<String>() {
                @Override
                public String get() {
                    return historyView.readString(
                        "Enter booking code (0 to go back): "
                    );
                }
            },

            new Consumer<String>() {
                @Override
                public void accept(String bookingCode) {

                    if (!InputValidator.isNotBlank(bookingCode)) {
                        throw new IllegalArgumentException(
                            "Booking code cannot be blank."
                        );
                    }


                    // Allows the customer to go back
                    if (bookingCode.trim().equals("0")) {
                        cancelInput[0] = true;
                        return;
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

                            transactionHolder[0] = current;
                            return;
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
                    historyView.showError(message);
                }
            }
        );


        if (cancelInput[0]) {
            return;
        }


        Transaction transaction = transactionHolder[0];


        // Requests cancellation confirmation
        if (!historyView.confirm(
                "Are you sure you want to cancel this booking?")) {

            return;
        }


        // Cancels the booking
        try {

            boolean cancelled =
                checkoutService.cancelTransaction(transaction);

            if (cancelled) {

                historyView.showMessage(
                    "Booking successfully cancelled."
                );

            } else {

                historyView.showError(
                    "Unable to cancel booking."
                );
            }

        } catch (IllegalArgumentException | IllegalStateException e) {

            historyView.showError(e.getMessage());
        }
    }
     // Displays the customer's transaction history
      public void viewMyHistory(Customer customer) {

        if (customer == null || history == null
                || historyView == null) {

            return;
        }


        LinkedList<Transaction> transactions =
            history.getByCustomer(customer.getUserID());

        if (transactions == null || transactions.size() == 0) {
            historyView.showMessage(
                "You have no booking history."
            );
            return;
        }

        historyView.displayTransactions(transactions);
    }
}
