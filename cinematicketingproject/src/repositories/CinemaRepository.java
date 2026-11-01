package repositories;

import datastructures.LinkedList;
import datastructures.Node;
import models.Cinema;
import models.CinemaRoom;
import models.Schedule;
import utility.InputValidator;

public class CinemaRepository {
    private static final String ID_PREFIX = "CIN";

    private LinkedList<Cinema> cinemas;

    // Constructor (no ID needed, the repository starts with an empty list)
    public CinemaRepository() {
        this.cinemas = new LinkedList<Cinema>();
    }

    // Rules: the cinema must not be null and its ID must not already exist (case-insensitive).
    // An empty list is allowed here because this is how the first cinema gets added.
    // Returns true when added; throws IllegalArgumentException otherwise.
    public boolean addCinema(Cinema cinema) {
        if (cinema == null) {
            throw new IllegalArgumentException("Invalid input. Cinema cannot be null.");
        }
        Node<Cinema> node = cinemas.getHead();
        while (node != null) {
            if (node.getData().getCinemaID().equalsIgnoreCase(cinema.getCinemaID())) {
                throw new IllegalArgumentException("Invalid input. Cinema ID " + cinema.getCinemaID() + " already exists.");
            }
            node = node.getNext();
        }
        cinemas.insertAtEnd(cinema);
        return true;
    }

    // Rules: there must be at least one cinema.
    // Returns the cinema list; throws IllegalArgumentException when it is empty.
    public LinkedList<Cinema> viewCinemas() {
        if (cinemas.size() == 0) {
            throw new IllegalArgumentException("Invalid input. There are no cinemas to view.");
        }
        return cinemas;
    }

    // Rules: the name must not be blank. Exact name match, case-insensitive.
    // Returns the first match, or null if no cinema has that name (this includes an empty
    // list, so the controller can still use it as a duplicate-name check before adding the
    // first cinema). Throws IllegalArgumentException for a blank name.
    public Cinema findCinema(String cinemaName) {
        if (!InputValidator.isNotBlank(cinemaName)) {
            throw new IllegalArgumentException("Invalid input. Cinema name cannot be blank.");
        }
        String name = cinemaName.trim();
        Node<Cinema> node = cinemas.getHead();
        while (node != null) {
            if (node.getData().getName().equalsIgnoreCase(name)) {
                return node.getData();
            }
            node = node.getNext();
        }
        return null;
    }

    // Rules: there must be at least one cinema, the ID must not be blank, and it must
    // belong to an existing cinema (case-insensitive).
    // Returns true when removed; throws IllegalArgumentException otherwise.
    public boolean removeCinema(String id) {
        if (cinemas.size() == 0) {
            throw new IllegalArgumentException("Invalid input. There are no cinemas to remove.");
        }
        if (!InputValidator.isNotBlank(id)) {
            throw new IllegalArgumentException("Invalid input. Cinema ID cannot be blank.");
        }
        String key = id.trim();
        Node<Cinema> node = cinemas.getHead();
        while (node != null) {
            if (node.getData().getCinemaID().equalsIgnoreCase(key)) {
                return cinemas.remove(node.getData());
            }
            node = node.getNext();
        }
        throw new IllegalArgumentException("Invalid input. Cinema " + key + " was not found.");
    }

    // Rules: there must be at least one cinema.
    // Collects the schedules of every room in every cinema; throws IllegalArgumentException
    // when there are no cinemas.
    public LinkedList<Schedule> getAllSchedules() {
        if (cinemas.size() == 0) {
            throw new IllegalArgumentException("Invalid input. There are no cinemas, so there are no schedules.");
        }
        LinkedList<Schedule> result = new LinkedList<Schedule>();
        Node<Cinema> cinemaNode = cinemas.getHead();
        while (cinemaNode != null) {
            Node<CinemaRoom> roomNode = cinemaNode.getData().getCinemaRooms().getHead();
            while (roomNode != null) {
                Node<Schedule> scheduleNode = roomNode.getData().getSchedules().getFront();
                while (scheduleNode != null) {
                    result.insertAtEnd(scheduleNode.getData());
                    scheduleNode = scheduleNode.getNext();
                }
                roomNode = roomNode.getNext();
            }
            cinemaNode = cinemaNode.getNext();
        }
        return result;
    }

    // Rules: there must be at least one cinema, and category and value must not be blank.
    // Delegates to CinemaRoom.filterSchedule for every room and merges the results; any
    // error CinemaRoom throws for an unknown category or bad value passes through.
    public LinkedList<Schedule> filterSchedule(String category, String value) {
        if (cinemas.size() == 0) {
            throw new IllegalArgumentException("Invalid input. There are no cinemas to filter schedules from.");
        }
        if (!InputValidator.isNotBlank(category)) {
            throw new IllegalArgumentException("Invalid input. Filter category cannot be blank.");
        }
        if (!InputValidator.isNotBlank(value)) {
            throw new IllegalArgumentException("Invalid input. Filter value cannot be blank.");
        }
        LinkedList<Schedule> result = new LinkedList<Schedule>();
        Node<Cinema> cinemaNode = cinemas.getHead();
        while (cinemaNode != null) {
            Node<CinemaRoom> roomNode = cinemaNode.getData().getCinemaRooms().getHead();
            while (roomNode != null) {
                Node<Schedule> match = roomNode.getData().filterSchedule(category, value).getHead();
                while (match != null) {
                    result.insertAtEnd(match.getData());
                    match = match.getNext();
                }
                roomNode = roomNode.getNext();
            }
            cinemaNode = cinemaNode.getNext();
        }
        return result;
    }

    // Generates the next ID in the format CIN001, CIN002, ... (highest existing number + 1).
    // An empty list is valid and gives CIN001.
    public String generateCinemaID() {
        int max = 0;
        Node<Cinema> node = cinemas.getHead();
        while (node != null) {
            String id = node.getData().getCinemaID();
            if (id.startsWith(ID_PREFIX) && InputValidator.isPositiveInt(id.substring(ID_PREFIX.length()))) {
                int number = Integer.parseInt(id.substring(ID_PREFIX.length()));
                if (number > max) {
                    max = number;
                }
            }
            node = node.getNext();
        }
        return String.format("%s%03d", ID_PREFIX, max + 1);
    }
}