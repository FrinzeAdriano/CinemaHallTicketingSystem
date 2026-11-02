package view;
import controllers.*;
import models.*;
import datastructures.*;
import repositories.*;
import utility.*;
import service.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
public class TicketView extends BaseView {


    // Requests the number of tickets
// Table of the customer's bookings: the customer picks a booking code from this
public void displayBookingTable(LinkedList<Transaction> transactions) {
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String[] headers = {"Booking Code", "Date", "Tickets", "Total", "Status"};
    ArrayList<String[]> rows = new ArrayList<String[]>();

    for (int i = 0; i < transactions.size(); i++) {
        Transaction t = transactions.get(i);

        rows.add(new String[] {
            t.getBookingCode() == null ? "N/A" : t.getBookingCode(),
            t.getDateTime().format(format),
            String.valueOf(t.getTickets().size()),
            String.format("%.2f", t.getTotalAmount()),
            t.getStatus()
        });
    }

    printTable("MY BOOKINGS", headers, rows);
}
     public int promptTicketCount() {

        while (true) {

            showMessage("----------------------------------------");

            String input = readString(
                "Enter ticket quantity (0 to go back): "
            );

            if (input.equals("0")) {
                return 0;
            }

            if (InputValidator.isPositiveInt(input)) {
                return Integer.parseInt(input.trim());
            }

            showError(
                "Please enter a valid positive whole number."
            );
        }
    }
 public String promptPaymentProvider(String paymentMethod) {

        String[] providers;

        System.out.println();

        if (paymentMethod.equalsIgnoreCase("E-wallet")) {
            System.out.println("=========== E-WALLET PROVIDER ============");
            providers = new String[] {"GCash", "Maya", "GrabPay"};
        } else {
            System.out.println("============= CARD PROVIDER ==============");
            providers = new String[] {"BDO Unibank", "BPI", "Metrobank",
                "UnionBank", "Security Bank", "LandBank"};
        }

        for (int i = 0; i < providers.length; i++) {
            System.out.println("[" + (i + 1) + "] " + providers[i]);
        }

        System.out.println("[0] Go back");
        System.out.println("------------------------------------------");

        int choice = readInt("Choose a provider (0 to go back): ", 0, providers.length);

        if (choice == 0) {
            return null;
        }

        return providers[choice - 1];
    }
    public int promptWalkInDiscount(Ticket ticket) {
    showMessage("\nTicket ID: " + ticket.getTicketID());
    showMessage("Seat: " + ticket.getSeat().getSeatID());
    showMessage("Ticket Price: " + ticket.getPrice());

    showMessage("[1] Senior Citizen");
    showMessage("[2] PWD");
    showMessage("[0] No discount");

    return readInt("Discount: ", 0, 2);
}
    // Displays one ticket

    public void displayTicket(Ticket ticket) {

        if (ticket == null) {
            showError("Ticket not found.");
            return;
        }

        showMessage("Ticket ID: " + ticket.getTicketID());

        if (ticket.getSchedule() != null) {
            showMessage(
                "Schedule: " + ticket.getSchedule().getScheduleID()
            );
        } else {
            showMessage("Schedule: Not available.");
        }

        if (ticket.getSeat() != null) {
            showMessage(
                "Seat: " + ticket.getSeat().getSeatID()
            );
        } else {
            showMessage("Seat: Not available.");
        }

        showMessage("Price: " + ticket.getPrice());
        showMessage("Status: " + ticket.getStatus());
    }


    // Displays multiple tickets

    public void displayTickets(LinkedList<Ticket> tickets) {

        if (tickets == null || tickets.size() == 0) {
            showMessage("No tickets found.");
            return;
        }

        for (int i = 0; i < tickets.size(); i++) {
            displayTicket(tickets.get(i));
        }
    }
}
