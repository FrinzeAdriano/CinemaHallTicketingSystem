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

            String input = readString("Enter ticket quantity: ");

            if (InputValidator.isPositiveInt(input)) {
                return Integer.parseInt(input.trim());
            }

            showError("Please enter a valid positive whole number.");
        }
    }



    // Displays one ticket
    public void displayTicket(Ticket ticket) {

        if (ticket == null) {
            showError("Ticket not found.");
            return;
        }

        showMessage("Ticket ID: " + ticket.getTicketID());
        showMessage("Schedule: " + ticket.getSchedule().getScheduleID());
        showMessage("Seat: " + ticket.getSeat().getSeatID());
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
