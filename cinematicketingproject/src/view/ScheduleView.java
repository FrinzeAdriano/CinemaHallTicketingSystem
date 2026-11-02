package view;

import datastructures.LinkedList;
import datastructures.Node;
import datastructures.Queue;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import models.Schedule;
import utility.InputValidator;
public class ScheduleView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public ScheduleView() {
        super();
    }

    // Menus and prompts

    public String showScheduleMenu() {
        System.out.println();
        System.out.println("===== SCHEDULE MANAGEMENT =====");
        System.out.println("1. View schedules");
        System.out.println("2. Add schedule");
        System.out.println("3. Remove schedule");
        System.out.println("4. Edit schedule");
        System.out.println("5. Filter schedules");
        System.out.println("0. Back");
        return readString("Choose an option: ").trim();
    }

    public String showEditScheduleMenu(Schedule schedule) {
        System.out.println();
        System.out.println("===== EDIT SCHEDULE " + schedule.getScheduleID() + " =====");
        System.out.println("1. Price  (" + String.format("%.2f", schedule.getPrice()) + ")");
        System.out.println("2. Status (" + (schedule.isActive() ? "Open" : "Closed") + ", choose to switch)");
        System.out.println("0. Back");
        return readString("Choose a field to edit: ").trim();
    }

    public String showScheduleFilterMenu() {
        System.out.println();
        System.out.println("===== FILTER SCHEDULES =====");
        System.out.println("1. By movie title");
        System.out.println("2. By date (yyyy-MM-dd)");
        System.out.println("3. By status (true = open, false = closed)");
        System.out.println("0. Back");
        return readString("Choose a filter: ").trim();
    }
    public Schedule selectSchedule(LinkedList<Schedule> list) {

    if (list == null || list.size() == 0) {
        throw new IllegalArgumentException("No schedules available.");
    }

    while (true) {

       displayBookingSchedules(list);
        showMessage("----------------------------------------");

        String input = readString("Enter schedule ID (0 to go back): ");

        if (input.equals("0")) {
            return null;
        }

        if (!InputValidator.isNotBlank(input)) {
            showError("Schedule ID cannot be blank.");
            continue;
        }

        for (int i = 0; i < list.size(); i++) {

            Schedule schedule = list.get(i);

            if (schedule != null
                    && schedule.getScheduleID().equalsIgnoreCase(input.trim())) {
                return schedule;
            }
        }

        showError("Schedule ID not found. Please try again.");
    }
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
        System.out.println("\nLegend:");
    System.out.println("  Removable");
    System.out.println("    Yes    - No booked seats; schedule can be removed.");
    System.out.println("    No     - Has booked seats; schedule cannot be removed.");
    System.out.println("  Status");
    System.out.println("    Open   - Available for booking.");
    System.out.println("    Closed - Not available for booking.");
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
        System.out.println("\nLegend:");
System.out.println("  REMOVABLE");
System.out.println("    Yes - No booked seats; schedule can be removed.");
System.out.println("    No  - Has booked seats; schedule cannot be removed.");
System.out.println("  STATUS");
System.out.println("    Open   - Available for booking.");
System.out.println("    Closed - Not available for booking.");
    }
    
    public void displayBookingSchedules(LinkedList<Schedule> list) {
    if (list == null || list.size() == 0) {
        showMessage("No available schedules.");
        return;
    }

    System.out.println("\n======================================== SCHEDULES ========================================");
    System.out.println("+--------+------------+---------------+------------+--------+--------+--------+");
    System.out.println("| ID     | Date       | Time          | Movie      | Price  | Booked | Status |");
    System.out.println("+--------+------------+---------------+------------+--------+--------+--------+");

    for (int i = 0; i < list.size(); i++) {
        Schedule schedule = list.get(i);

        String date = schedule.getShowDateTime().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        );

        String startTime = schedule.getShowDateTime().format(
            java.time.format.DateTimeFormatter.ofPattern("HH:mm")
        );

        String endTime = schedule.getShowDateTime()
            .plusMinutes(schedule.getMovie().getDuration())
            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        String movieTitle = schedule.getMovie().getTitle();

        if (movieTitle.length() > 10) {
            movieTitle = movieTitle.substring(0, 10);
        }

        int booked = schedule.getBookedSeatIDs().size();
        String status = schedule.isActive() ? "Open" : "Closed";

        System.out.printf(
            "| %-6s | %-10s | %-13s | %-10s | %6.2f | %-6d | %-6s |%n",
            schedule.getScheduleID(),
            date,
            startTime + " - " + endTime,
            movieTitle,
            schedule.getPrice(),
            booked,
            status
        );
    }

    System.out.println("+--------+------------+---------------+------------+--------+--------+--------+");
    System.out.println("\nLegend:");
    System.out.println("  Open   - Available for booking.");
    System.out.println("  Closed - Not available for booking.");
}
}