package view;

import datastructures.LinkedList;
import datastructures.Node;
import datastructures.Queue;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import models.Schedule;

public class ScheduleView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public ScheduleView() {
        super();
    }

    // Menus and prompts

    public String showScheduleMenu() {
        System.out.println();
        System.out.println("===== SCHEDULE MANAGEMENT =====");
        System.out.println("[1] View schedules");
        System.out.println("[2] Add schedule");
        System.out.println("[3] Remove schedule");
        System.out.println("[4] Edit schedule");
        System.out.println("[5] Filter schedules");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readString("Choose an option (0 to go back): ").trim();
    }

    public String showEditScheduleMenu(Schedule schedule) {
        System.out.println();
        System.out.println("===== EDIT SCHEDULE " + schedule.getScheduleID() + " =====");
        System.out.println("[1] Price  (" + String.format("%.2f", schedule.getPrice()) + ")");
        System.out.println("[2] Status (" + (schedule.isActive() ? "Open" : "Closed") + ", choose to switch)");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readString("Choose a field to edit (0 to go back): ").trim();
    }

    public String showScheduleFilterMenu() {
        System.out.println();
        System.out.println("===== FILTER SCHEDULES =====");
        System.out.println("[1] By movie title");
        System.out.println("[2] By date (yyyy-MM-dd)");
        System.out.println("[3] By status (true = open, false = closed)");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readString("Choose a filter (0 to go back): ").trim();
    }
    public Schedule selectSchedule(LinkedList<Schedule> list) {
        displaySchedules(list);
        int choice = readInt("Select a schedule number: ", 1, list.size());
        return list.get(choice - 1);
    }
    public String promptScheduleID() {
        return readString("Enter schedule ID (0 to go back): ");
    }

    public String promptMovieID() {
        return readString("Enter movie (0 to go back): ");
    }

    public String promptPrice() {
        return readString("Ticket price (0 to go back): ");
    }

    public String promptShowDateTime() {
        return readString("Show date and time, yyyy-MM-dd HH:mm (0 to go back): ");
    }

    public String promptSearchValue() {
        return readString("Search value (0 to go back): ");
    }

    // Display methods

    // Shows a room's schedules (a Queue) as a table, in queue order.
    public void displaySchedules(Queue<Schedule> queue) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        String[] headers = {"ID", "Date", "Time", "Movie", "Price", "Booked", "Status", "Removable"};
        ArrayList<String[]> rows = new ArrayList<>();
        Node<Schedule> node = queue.getFront();
        while (node != null) {
            Schedule s = node.getData();
            int booked = s.getBookedSeatIDs().size();
            rows.add(new String[] {
                    s.getScheduleID(),
                    s.getShowDateTime().toLocalDate().toString(),
                    s.getShowDateTime().format(timeFormat) + " - "
                            + s.getShowDateTime().plusMinutes(s.getMovie().getDuration()).format(timeFormat),
                    s.getMovie().getTitle(),
                    String.format("%.2f", s.getPrice()),
                    String.valueOf(booked),
                    s.isActive() ? "Open" : "Closed",
                    booked == 0 ? "Yes" : "No"
            });
            node = node.getNext();
        }
        printTable("SCHEDULES", headers, rows);
        System.out.println("Legend:");
        System.out.println("  Removable Yes  No booked seats, the schedule can be removed");
        System.out.println("  Removable No   Has booked seats, the schedule cannot be removed");
        System.out.println("  Status Open    Open for booking");
        System.out.println("  Status Closed  Closed for booking");
    }

    // Shows a filtered list of schedules as the same table.
    public void displaySchedules(LinkedList<Schedule> list) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        String[] headers = {"ID", "Date", "Time", "Movie", "Price", "Booked", "Status", "Removable"};
        ArrayList<String[]> rows = new ArrayList<>();
        Node<Schedule> node = list.getHead();
        while (node != null) {
            Schedule s = node.getData();
            int booked = s.getBookedSeatIDs().size();
            rows.add(new String[] {
                    s.getScheduleID(),
                    s.getShowDateTime().toLocalDate().toString(),
                    s.getShowDateTime().format(timeFormat) + " - "
                            + s.getShowDateTime().plusMinutes(s.getMovie().getDuration()).format(timeFormat),
                    s.getMovie().getTitle(),
                    String.format("%.2f", s.getPrice()),
                    String.valueOf(booked),
                    s.isActive() ? "Open" : "Closed",
                    booked == 0 ? "Yes" : "No"
            });
            node = node.getNext();
        }
        printTable("SCHEDULES", headers, rows);
        System.out.println("Legend:");
        System.out.println("  Removable Yes  No booked seats, the schedule can be removed");
        System.out.println("  Removable No   Has booked seats, the schedule cannot be removed");
        System.out.println("  Status Open    Open for booking");
        System.out.println("  Status Closed  Closed for booking");
    }
}