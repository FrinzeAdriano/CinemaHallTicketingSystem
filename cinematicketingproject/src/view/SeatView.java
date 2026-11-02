package view;

import datastructures.LinkedList;
import datastructures.Node;
import java.util.ArrayList;
import models.CinemaRoom;
import models.Schedule;
import models.Seat;

public class SeatView extends BaseView {

    private static final int AISLE_CELLS = 1;   // width of an aisle, counted in seat cells (1 = same width as [A1])

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public SeatView() {
        super();
    }

    // Menus and prompts

    public String showSeatMenu() {
        System.out.println();
        System.out.println("=========== SEAT MANAGEMENT ============");
        System.out.println("[1] View room layout");
        System.out.println("[2] Add seat");
        System.out.println("[3] Remove seat");
        System.out.println("[4] Edit seat");
        System.out.println("[5] Filter seats");
        System.out.println("[6] Add or remove aisle");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readString("Choice (0 to go back): ");
    }

    public String showSeatFilterMenu() {
        System.out.println();
        System.out.println("============= FILTER SEATS =============");
        System.out.println("[1] By row (e.g. A)");
        System.out.println("[2] By type (Regular, Premium, VIP, Wheelchair)");
        System.out.println("[3] By status (Available, Unavailable)");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readString("Choice (0 to go back): ");
    }

    public String showEditSeatMenu(Seat seat) {
        displaySeat(seat);
        System.out.println();
        System.out.println("============== EDIT SEAT ===============");
        System.out.println("[1] Seat type");
        System.out.println("[2] Seat status");
        System.out.println("[3] Price modifier");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readString("Choice (0 to go back): ");
    }

    public String promptSeatID() {
        return readString("Enter seat ID (e.g. A1) (0 to go back): ");
    }

    public String promptSeatType() {
        return readString("Enter seat type (Regular, Premium, VIP, Wheelchair) (0 to go back): ");
    }

    public String promptSearchValue() {
        return readString("Enter search value (0 to go back): ");
    }

    public String promptPriceModifier() {
        return readString("Enter price modifier (0 to go back): ");
    }

    // Display methods


    // Shows the room's seats for one schedule. All widths are worked out from the seats themselves.
    public void displaySeatGrid(Schedule schedule) {
        CinemaRoom room = schedule.getRoom();

        // Measure the room: longest row and longest seat label
        int maxPerRow = 0;
        int inRow = 0;
        int longestCell = 0;
        char previousRow = 0;
        Node<Seat> measure = room.getSeats().getHead();
        while (measure != null) {
            Seat seat = measure.getData();
            inRow = (seat.getSeatRow() == previousRow) ? inRow + 1 : 1;
            previousRow = seat.getSeatRow();
            // An aisle only takes space when another seat of the same row follows it.
            if (seat.hasAisleAfter() && measure.getNext() != null
                    && measure.getNext().getData().getSeatRow() == seat.getSeatRow()) {
                inRow += AISLE_CELLS;
            }
            maxPerRow = Math.max(maxPerRow, inRow);
            longestCell = Math.max(longestCell, seat.getSeatID().length() + 2);   // + 2 for the brackets
            measure = measure.getNext();
        }

        String title = "SEATS FOR SCHEDULE " + schedule.getScheduleID();
        int labelWidth = "Row X".length() + 3;
        int cellWidth = longestCell;   // cells touch: no gap between them
        int width = Math.max(labelWidth + maxPerRow * cellWidth, Math.max(title.length(), "SCREEN".length() + 4));
        String equalsLine = "=".repeat(width);
        String dashLine = "-".repeat(width);
        String labelFormat = "%-" + labelWidth + "s";
        String cellFormat = "%-" + cellWidth + "s";
        // The seats sit to the right of the row label; the screen is centered over them.
        int screenPadding = (width - "SCREEN".length()) / 2;

        System.out.println();
        System.out.println(title);
        System.out.println(equalsLine);
        Node<Seat> node = room.getSeats().getHead();
        while (node != null) {
            // Collect one row, then center it under the widest row.
            char rowLetter = node.getData().getSeatRow();
            StringBuilder cells = new StringBuilder();
            int count = 0;
            while (node != null && node.getData().getSeatRow() == rowLetter) {
                Seat seat = node.getData();
                String cell;
                if (schedule.getBookedSeatIDs().contains(seat.getSeatID())) {
                    cell = "[XX]";
                } else if (!seat.getStatus()) {
                    cell = "[--]";
                } else {
                    cell = "[" + seat.getSeatID() + "]";
                }
                cells.append(String.format(cellFormat, cell));
                count++;
                if (seat.hasAisleAfter() && node.getNext() != null && node.getNext().getData().getSeatRow() == rowLetter) {
                    cells.append(" ".repeat(cellWidth * AISLE_CELLS));   // aisle: blank space before the next seat
                    count += AISLE_CELLS;
                }
                node = node.getNext();
            }
            int indent = (maxPerRow - count) * cellWidth / 2;
            System.out.println(String.format(labelFormat, "Row " + rowLetter) + " ".repeat(indent) + cells);
        }
        System.out.println(dashLine);
        System.out.println();
        System.out.println(equalsLine);
        System.out.println(" ".repeat(screenPadding) + "SCREEN");
        System.out.println(equalsLine);
        System.out.println();
        System.out.println("Legend:");
        System.out.println("  [A1]  Available seat");
        System.out.println("  [XX]  Already booked for this schedule");
        System.out.println("  [--]  Unavailable seat (cannot be booked)");
        System.out.println("  (blank column)  Aisle");
    }

    // Shows every seat of the room. All widths are worked out from the seats themselves.
    public void displayRoomLayout(CinemaRoom room) {
        // Measure the room: longest row and longest seat label
        int maxPerRow = 0;
        int inRow = 0;
        int longestCell = 0;
        char previousRow = 0;
        Node<Seat> measure = room.getSeats().getHead();
        while (measure != null) {
            Seat seat = measure.getData();
            inRow = (seat.getSeatRow() == previousRow) ? inRow + 1 : 1;
            previousRow = seat.getSeatRow();
            // An aisle only takes space when another seat of the same row follows it.
            if (seat.hasAisleAfter() && measure.getNext() != null
                    && measure.getNext().getData().getSeatRow() == seat.getSeatRow()) {
                inRow += AISLE_CELLS;
            }
            maxPerRow = Math.max(maxPerRow, inRow);
            longestCell = Math.max(longestCell, seat.getSeatID().length() + 2);   // + 2 for the brackets
            measure = measure.getNext();
        }

        String title = "LAYOUT OF " + room.getRoomName() + " (" + room.getRoomID() + ")";
        int labelWidth = "Row X".length() + 3;
        int cellWidth = longestCell;   // cells touch: no gap between them
        int width = Math.max(labelWidth + maxPerRow * cellWidth, Math.max(title.length(), "SCREEN".length() + 4));
        String equalsLine = "=".repeat(width);
        String dashLine = "-".repeat(width);
        String labelFormat = "%-" + labelWidth + "s";
        String cellFormat = "%-" + cellWidth + "s";
        // The seats sit to the right of the row label; the screen is centered over them.
        int screenPadding = labelWidth + (maxPerRow * cellWidth - "SCREEN".length()) / 2;

        System.out.println();
        System.out.println(title);
        System.out.println(equalsLine);
        Node<Seat> node = room.getSeats().getHead();
        while (node != null) {
            // Collect one row, then center it under the widest row.
            char rowLetter = node.getData().getSeatRow();
            StringBuilder cells = new StringBuilder();
            int count = 0;
            while (node != null && node.getData().getSeatRow() == rowLetter) {
                Seat seat = node.getData();
                String cell = seat.getStatus() ? "[" + seat.getSeatID() + "]" : "[--]";
                cells.append(String.format(cellFormat, cell));
                count++;
                if (seat.hasAisleAfter() && node.getNext() != null && node.getNext().getData().getSeatRow() == rowLetter) {
                    cells.append(" ".repeat(cellWidth * AISLE_CELLS));   // aisle: blank space before the next seat
                    count += AISLE_CELLS;
                }
                node = node.getNext();
            }
            int indent = (maxPerRow - count) * cellWidth / 2;
            System.out.println(String.format(labelFormat, "Row " + rowLetter) + " ".repeat(indent) + cells);
        }
        System.out.println(dashLine);
        System.out.println();
        System.out.println(equalsLine);
        System.out.println(" ".repeat(screenPadding) + "SCREEN");
        System.out.println(equalsLine);
        System.out.println();
        System.out.println("Legend:");
        System.out.println("  [A1]  Available seat");
        System.out.println("  [--]  Unavailable seat (cannot be booked)");
        System.out.println("  (blank column)  Aisle");
    }

    // Table of seats (used for the filter results).
    public void displaySeats(LinkedList<Seat> seats) {
        ArrayList<String[]> rows = new ArrayList<>();
        Node<Seat> node = seats.getHead();
        while (node != null) {
            Seat seat = node.getData();
            rows.add(new String[] {seat.getSeatID(), String.valueOf(seat.getSeatRow()), String.valueOf(seat.getSeatNumber()),
                    seat.getSeatType(), seat.getStatus() ? "Available" : "Unavailable",
                    String.valueOf(seat.getPriceModifier()), seat.hasAisleAfter() ? "Yes" : "No"});
            node = node.getNext();
        }
        printTable("SEATS", new String[] {"ID", "Row", "Number", "Type", "Status", "Price modifier", "Aisle after"}, rows);
    }

    // One seat shown as a one-row table (used by the edit seat menu).
    public void displaySeat(Seat seat) {
        LinkedList<Seat> single = new LinkedList<Seat>();
        single.insertAtEnd(seat);
        displaySeats(single);
    }
}