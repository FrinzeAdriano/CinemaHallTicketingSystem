package view;

import datastructures.LinkedList;
import datastructures.Node;
import models.CinemaRoom;
import models.Movie;
import models.Schedule;

public class ScheduleView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public ScheduleView() {
        super();
    }

    // Menus and prompts

    public int showScheduleMenu() {
        System.out.println();
        System.out.println("===== SCHEDULE MANAGEMENT =====");
        System.out.println("1. Add schedule");
        System.out.println("2. Remove schedule");
        System.out.println("3. Activate or deactivate schedule");
        System.out.println("4. Filter schedules");
        System.out.println("5. View all schedules");
        System.out.println("0. Back");
        return readInt("Choose an option: ", 0, 5);
    }

    // Reads each field and passes it to its setter. If the setter rejects the value,
    // the view only shows the setter's message and asks for that field again.
    public Schedule promptNewSchedule(String scheduleID, Movie movie, CinemaRoom room) {
        Schedule schedule = new Schedule(scheduleID);
        boolean valid = false;
        while (!valid) {
            try {
                schedule.setShowDateTime(readString("Show date and time (yyyy-MM-dd HH:mm): "));
                valid = true;
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        }
        valid = false;
        while (!valid) {
            try {
                schedule.setPrice(readString("Ticket price: "));
                valid = true;
            } catch (IllegalArgumentException e) {
                showError(e.getMessage());
            }
        }
        return schedule;
    }

    public String promptScheduleID() {
        return readString("Enter schedule ID: ");
    }

    // Prompts for the filter menu option (raw input only, the room validates it).
    public String promptFilterCategory() {
        return readString("Filter by (MOVIE, DATE, ACTIVE): ");
    }

    public String promptFilterValue() {
        return readString("Filter value: ");
    }

    // Prompt for the activate/deactivate option (true = active).
    public boolean promptActive() {
        return confirm("Set this schedule as active?");
    }

    // Shows the list numbered and returns the schedule the user picks.
    public Schedule selectSchedule(LinkedList<Schedule> list) {
        int count = list.size();
        if (count == 0) {
            throw new IllegalArgumentException("Invalid input. There are no schedules to select.");
        }
        displaySchedules(list);
        int choice = readInt("Select a schedule number: ", 1, count);
        return list.get(choice - 1);
    }

    // Display methods

    public void displaySchedules(LinkedList<Schedule> list) {
        System.out.println();
        System.out.println("===== SCHEDULES =====");
        System.out.printf("%-4s %-8s %-25s %-15s %-17s %-9s %-7s %-6s%n",
                "No.", "ID", "Movie", "Room", "Date & time", "Price", "Booked", "Active");
        Node<Schedule> node = list.getHead();
        int number = 1;
        while (node != null) {
            Schedule s = node.getData();
            System.out.printf("%-4d %-8s %-25s %-15s %-17s %-9.2f %-7d %-6s%n",
                    number, s.getScheduleID(), s.getMovie().getTitle(), s.getRoom().getRoomName(),
                    s.getShowDateTime().toString().replace('T', ' '), s.getPrice(),
                    s.getBookedSeatIDs().size(), s.isActive() ? "Yes" : "No");
            node = node.getNext();
            number++;
        }
    }
}