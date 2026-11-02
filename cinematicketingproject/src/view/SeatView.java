package view;

import datastructures.Node;
import models.CinemaRoom;
import models.Schedule;
import models.Seat;

public class SeatView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public SeatView() {
        super();
    }

    // Menus and prompts

    public String showSeatMenu() {
        System.out.println();
        System.out.println("=========== SEAT MANAGEMENT ============");
        System.out.println("1. View room layout");
        System.out.println("2. Add seat");
        System.out.println("3. Remove seat");
        System.out.println("4. Edit seat");
        System.out.println("5. Filter seats");
        System.out.println("0. Back");
        System.out.println("----------------------------------------");
        return readString("Choice (0 to go back): ");
    }

    public String showSeatFilterMenu() {
        System.out.println();
        System.out.println("============= FILTER SEATS =============");
        System.out.println("1. By row");
        System.out.println("2. By type");
        System.out.println("3. By status");
        System.out.println("0. Back");
        System.out.println("----------------------------------------");
        return readString("Choice (0 to go back): ");
    }

    public String showEditSeatMenu(Seat seat) {
        displaySeat(seat);
        System.out.println();
        System.out.println("============== EDIT SEAT ===============");
        System.out.println("1. Seat type");
        System.out.println("2. Seat status");
        System.out.println("3. Price modifier");
        System.out.println("0. Back");
        System.out.println("----------------------------------------");
        return readString("Choice (0 to go back): ");
    }

    public String promptSeatID() {
        return readString("Enter seat ID (e.g. A1): ");
    }

    public String promptSeatType() {
        return readString("Enter seat type (e.g. Regular, VIP): ");
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
            maxPerRow = Math.max(maxPerRow, inRow);
            longestCell = Math.max(longestCell, seat.getSeatID().length() + 2);   // + 2 for the brackets
            measure = measure.getNext();
        }

        String title = "SEATS FOR SCHEDULE " + schedule.getScheduleID();
        int labelWidth = "Row X".length() + 3;
        int cellWidth = Math.max(longestCell, ("Seat " + maxPerRow).length()) + 2;
        int width = Math.max(40, Math.max(labelWidth + maxPerRow * cellWidth, Math.max(title.length(), "SCREEN".length() + 4)));
        String equalsLine = "=".repeat(width);
        String dashLine = "-".repeat(width);
        String labelFormat = "%-" + labelWidth + "s";
        String cellFormat = "%-" + cellWidth + "s";
        int screenPadding = (width - "SCREEN".length()) / 2;

        System.out.println();
        System.out.println(title);
        System.out.println(equalsLine);
        System.out.print(String.format(labelFormat, ""));
        for (int n = 1; n <= maxPerRow; n++) {
            System.out.print(String.format(cellFormat, "Seat " + n));
        }
        System.out.println();
        Node<Seat> node = room.getSeats().getHead();
        char currentRow = 0;
        while (node != null) {
            Seat seat = node.getData();
            if (seat.getSeatRow() != currentRow) {
                if (currentRow != 0) {
                    System.out.println();
                }
                currentRow = seat.getSeatRow();
                System.out.print(String.format(labelFormat, "Row " + currentRow));
            }
            String cell;
            if (schedule.getBookedSeatIDs().contains(seat.getSeatID())) {
                cell = "[XX]";
            } else if (!seat.getStatus()) {
                cell = "[--]";
            } else {
                cell = "[" + seat.getSeatID() + "]";
            }
            System.out.print(String.format(cellFormat, cell));
            node = node.getNext();
        }
        System.out.println();
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
            maxPerRow = Math.max(maxPerRow, inRow);
            longestCell = Math.max(longestCell, seat.getSeatID().length() + 2);   // + 2 for the brackets
            measure = measure.getNext();
        }

        String title = "LAYOUT OF " + room.getRoomName() + " (" + room.getRoomID() + ")";
        int labelWidth = "Row X".length() + 3;
        int cellWidth = Math.max(longestCell, ("Seat " + maxPerRow).length()) + 2;
        int width = Math.max(40, Math.max(labelWidth + maxPerRow * cellWidth, Math.max(title.length(), "SCREEN".length() + 4)));
        String equalsLine = "=".repeat(width);
        String dashLine = "-".repeat(width);
        String labelFormat = "%-" + labelWidth + "s";
        String cellFormat = "%-" + cellWidth + "s";
        int screenPadding = (width - "SCREEN".length()) / 2;

        System.out.println();
        System.out.println(title);
        System.out.println(equalsLine);
        System.out.print(String.format(labelFormat, ""));
        for (int n = 1; n <= maxPerRow; n++) {
            System.out.print(String.format(cellFormat, "Seat " + n));
        }
        System.out.println();
        Node<Seat> node = room.getSeats().getHead();
        char currentRow = 0;
        while (node != null) {
            Seat seat = node.getData();
            if (seat.getSeatRow() != currentRow) {
                if (currentRow != 0) {
                    System.out.println();
                }
                currentRow = seat.getSeatRow();
                System.out.print(String.format(labelFormat, "Row " + currentRow));
            }
            String cell = seat.getStatus() ? "[" + seat.getSeatID() + "]" : "[--]";
            System.out.print(String.format(cellFormat, cell));
            node = node.getNext();
        }
        System.out.println();
        System.out.println(dashLine);
        System.out.println();
        System.out.println(equalsLine);
        System.out.println(" ".repeat(screenPadding) + "SCREEN");
        System.out.println(equalsLine);
        System.out.println();
        System.out.println("Legend:");
        System.out.println("  [A1]  Available seat");
        System.out.println("  [--]  Unavailable seat (cannot be booked)");
    }

    public void displaySeat(Seat seat) {
        System.out.println();
        System.out.println("============= SEAT DETAILS =============");
        System.out.println("ID             : " + seat.getSeatID());
        System.out.println("Row            : " + seat.getSeatRow());
        System.out.println("Number         : " + seat.getSeatNumber());
        System.out.println("Type           : " + seat.getSeatType());
        System.out.println("Status         : " + seat.getStatus());
        System.out.println("Price modifier : " + seat.getPriceModifier());
        System.out.println("----------------------------------------");
    }
}