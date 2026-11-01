package view;

import datastructures.LinkedList;
import datastructures.Node;
import models.CinemaRoom;

public class CinemaRoomListView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public CinemaRoomListView() {
        super();
    }

    // Menus and prompts

    public int showRoomMenu() {
        System.out.println();
        System.out.println("===== ROOM MANAGEMENT =====");
        System.out.println("1. Add room");
        System.out.println("2. Edit room");
        System.out.println("3. Remove room");
        System.out.println("4. Generate seat layout");
        System.out.println("5. View all rooms");
        System.out.println("0. Back");
        return readInt("Choose an option: ", 0, 5);
    }

    // Reads the raw fields and passes them to the setters. A setter that rejects a value
    // throws IllegalArgumentException; the controller catches it and shows e.getMessage().
    public CinemaRoom promptNewRoom(String roomID) {
        CinemaRoom room = new CinemaRoom(roomID);
        room.setRoomName(readString("Room name: "));
        room.setRows(readString("Number of rows (1-26): "));
        room.setSeatsPerRow(readString("Seats per row: "));
        room.setStatus(confirm("Open the room for use now?"));
        return room;
    }

    public String promptRoomID() {
        return readString("Enter room ID: ");
    }

    public void displayRooms(LinkedList<CinemaRoom> rooms) {
        System.out.println();
        System.out.println("===== ROOMS =====");
        System.out.printf("%-8s %-20s %-6s %-10s %-8s %-7s %-10s%n",
                "ID", "Name", "Rows", "Seats/Row", "Status", "Seats", "Schedules");
        Node<CinemaRoom> node = rooms.getHead();
        while (node != null) {
            CinemaRoom r = node.getData();
            System.out.printf("%-8s %-20s %-6d %-10d %-8s %-7d %-10d%n",
                    r.getRoomID(), r.getRoomName(), r.getRows(), r.getSeatsPerRow(),
                    r.getStatus() ? "Open" : "Closed", r.getSeats().size(), r.getSchedules().size());
            node = node.getNext();
        }
    }
}