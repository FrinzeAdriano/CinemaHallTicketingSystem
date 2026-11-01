package view;

import datastructures.LinkedList;
import datastructures.Node;
import models.Cinema;

public class CinemaView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public CinemaView() {
        super();
    }

    // Menus and prompts

    public int showCinemaMenu() {
        System.out.println();
        System.out.println("===== CINEMA MANAGEMENT =====");
        System.out.println("1. Add cinema");
        System.out.println("2. Edit cinema");
        System.out.println("3. Remove cinema");
        System.out.println("4. Find cinema by name");
        System.out.println("5. View all cinemas");
        System.out.println("0. Back");
        return readInt("Choose an option: ", 0, 5);
    }

    // Reads the raw fields and passes them to the setters. A setter that rejects a value
    // throws IllegalArgumentException; the controller catches it and shows e.getMessage().
    public Cinema promptNewCinema(String cinemaID) {
        Cinema cinema = new Cinema(cinemaID);
        cinema.setName(readString("Cinema name: "));
        cinema.setLocation(readString("Location: "));
        return cinema;
    }

    public String promptCinemaID() {
        return readString("Enter cinema ID: ");
    }

    public int showEditCinemaMenu(Cinema cinema) {
        System.out.println();
        System.out.println("===== EDIT CINEMA " + cinema.getCinemaID() + " =====");
        System.out.println("1. Name     (" + cinema.getName() + ")");
        System.out.println("2. Location (" + cinema.getLocation() + ")");
        System.out.println("0. Back");
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
        System.out.println();
        System.out.println("===== ALL CINEMAS =====");
        System.out.printf("%-8s %-25s %-40s %-6s%n", "ID", "Name", "Location", "Rooms");
        Node<Cinema> node = list.getHead();
        while (node != null) {
            Cinema c = node.getData();
            System.out.printf("%-8s %-25s %-40s %-6d%n", c.getCinemaID(), c.getName(), c.getLocation(),
                    c.getCinemaRooms().size());
            node = node.getNext();
        }
    }
}