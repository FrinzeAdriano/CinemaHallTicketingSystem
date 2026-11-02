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
        return readString("Enter seat ID (e.g. A1) (0 to go back): ");
    }
 
    public String promptSeatType() {
        return readString("Enter seat type (Regular, Premium, VIP, Wheelchair): ");
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
        // maxPerRow is the widest span: the highest seat number in any row.
        // A missing number inside a row is an aisle and is printed as blank space.
        int maxPerRow = 0;
        int longestCell = 0;
        Node<Seat> measure = room.getSeats().getHead();
        while (measure != null) {
            Seat seat = measure.getData();
            maxPerRow = Math.max(maxPerRow, seat.getSeatNumber());
            longestCell = Math.max(longestCell, seat.getSeatID().length() + 2);   // + 2 for the brackets
            measure = measure.getNext();
        }
 
        String title = "SEATS FOR SCHEDULE " + schedule.getScheduleID();
        int labelWidth = "Row X".length() + 3;
        int cellWidth = Math.max(longestCell, String.valueOf(maxPerRow).length());   // cells touch: no gap between them
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
        // Seat number header, one number over each column.
        StringBuilder header = new StringBuilder(String.format(labelFormat, "Seat"));
        for (int n = 1; n <= maxPerRow; n++) {
            String num = String.valueOf(n);
            int left = (cellWidth - num.length()) / 2;
            header.append(" ".repeat(left)).append(num).append(" ".repeat(cellWidth - num.length() - left));
        }
        System.out.println(header);
        Node<Seat> node = room.getSeats().getHead();
        while (node != null) {
            // Collect one row, then center it under the widest row.
            char rowLetter = node.getData().getSeatRow();
            StringBuilder cells = new StringBuilder();
            int next = 1;   // the seat number expected in the next column
            while (node != null && node.getData().getSeatRow() == rowLetter) {
                Seat seat = node.getData();
                while (next < seat.getSeatNumber()) {
                    cells.append(" ".repeat(cellWidth));   // aisle: a missing seat number
                    next++;
                }
                String cell;
                if (schedule.getBookedSeatIDs().contains(seat.getSeatID())) {
                    cell = "[XX]";
                } else if (!seat.getStatus()) {
                    cell = "[--]";
                } else {
                    cell = "[" + seat.getSeatID() + "]";
                }
                cells.append(String.format(cellFormat, cell));
                next++;
                node = node.getNext();
            }
            int indent = (maxPerRow - (next - 1)) * cellWidth / 2;
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
    }
 
    // Shows every seat of the room. All widths are worked out from the seats themselves.
    public void displayRoomLayout(CinemaRoom room) {
        // Measure the room: longest row and longest seat label
        // maxPerRow is the widest span: the highest seat number in any row.
        // A missing number inside a row is an aisle and is printed as blank space.
        int maxPerRow = 0;
        int longestCell = 0;
        Node<Seat> measure = room.getSeats().getHead();
        while (measure != null) {
            Seat seat = measure.getData();
            maxPerRow = Math.max(maxPerRow, seat.getSeatNumber());
            longestCell = Math.max(longestCell, seat.getSeatID().length() + 2);   // + 2 for the brackets
            measure = measure.getNext();
        }
 
        String title = "LAYOUT OF " + room.getRoomName() + " (" + room.getRoomID() + ")";
        int labelWidth = "Row X".length() + 3;
        int cellWidth = Math.max(longestCell, String.valueOf(maxPerRow).length());   // cells touch: no gap between them
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
        // Seat number header, one number over each column.
        StringBuilder header = new StringBuilder(String.format(labelFormat, "Seat"));
        for (int n = 1; n <= maxPerRow; n++) {
            String num = String.valueOf(n);
            int left = (cellWidth - num.length()) / 2;
            header.append(" ".repeat(left)).append(num).append(" ".repeat(cellWidth - num.length() - left));
        }
        System.out.println(header);
        Node<Seat> node = room.getSeats().getHead();
        while (node != null) {
            // Collect one row, then center it under the widest row.
            char rowLetter = node.getData().getSeatRow();
            StringBuilder cells = new StringBuilder();
            int next = 1;   // the seat number expected in the next column
            while (node != null && node.getData().getSeatRow() == rowLetter) {
                Seat seat = node.getData();
                while (next < seat.getSeatNumber()) {
                    cells.append(" ".repeat(cellWidth));   // aisle: a missing seat number
                    next++;
                }
                String cell = seat.getStatus() ? "[" + seat.getSeatID() + "]" : "[--]";
                cells.append(String.format(cellFormat, cell));
                next++;
                node = node.getNext();
            }
            int indent = (maxPerRow - (next - 1)) * cellWidth / 2;
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