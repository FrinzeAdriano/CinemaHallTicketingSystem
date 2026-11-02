package view;

import datastructures.LinkedList;
import datastructures.Node;
import models.Cinema;
import java.util.ArrayList;

public class CinemaView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public CinemaView() {
        super();
    }

    // Menus and prompts

    // Reads the raw fields and passes them to the setters. A setter that rejects a value
    // throws IllegalArgumentException; the controller catches it and shows e.getMessage().
    public Cinema promptNewCinema(String cinemaID) {
        Cinema cinema = new Cinema(cinemaID);
        cinema.setName(readString("Cinema name: "));
        cinema.setLocation(readString("Location: "));
        return cinema;
    }

    public String promptCinemaID() {
        return readString("Enter cinema ID (0 to go back): ");
    }

    public String promptCinemaName() {
        return readString("Cinema name (0 to go back): ");
    }

    public String promptCinemaLocation() {
        return readString("Location (0 to go back): ");
    }

    public int showEditCinemaMenu(Cinema cinema) {
        System.out.println();
        System.out.println("===== EDIT CINEMA " + cinema.getCinemaID() + " =====");
        System.out.println("[1] Name     (" + cinema.getName() + ")");
        System.out.println("[2] Location (" + cinema.getLocation() + ")");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readInt("Choose a field to edit: ", 0, 2);
    }

    public void displayCinema(Cinema cinema) {
        System.out.println();
        System.out.println("----- CINEMA DETAILS -----");
        System.out.println("ID       : " + cinema.getCinemaID());
        System.out.println("Name     : " + cinema.getName());
        System.out.println("Location : " + cinema.getLocation());
        System.out.println("Rooms    : " + cinema.getCinemaRooms().size());
    }

    public void displayCinemas(LinkedList<Cinema> list) {
        ArrayList<String[]> rows = new ArrayList<>();
        Node<Cinema> node = list.getHead();
        while (node != null) {
            Cinema c = node.getData();
            rows.add(new String[] {c.getCinemaID(), c.getName(), c.getLocation(),
                    String.valueOf(c.getCinemaRooms().size())});
            node = node.getNext();
        }
        printTable("ALL CINEMAS", new String[] {"ID", "Name", "Location", "Rooms"}, rows);
    }
}