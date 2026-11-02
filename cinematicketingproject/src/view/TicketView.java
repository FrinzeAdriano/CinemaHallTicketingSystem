package view;
import controllers.*;
import models.*;
import datastructures.*;
import repositories.*;
import utility.*;
import service.*;

public class TicketView extends BaseView {


    // Requests the number of tickets

     public int promptTicketCount() {

        while (true) {

            showMessage("----------------------------------------");

            String input = readString(
                "Enter seat quantity (0 to go back): "
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
