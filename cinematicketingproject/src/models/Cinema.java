package models;

import datastructures.LinkedList;
import datastructures.Node;
import utility.InputValidator;

public class Cinema {
    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MIN_LOCATION_LENGTH = 5;
    private static final int MAX_LOCATION_LENGTH = 100;

    private final String cinemaID;
    private String name;
    private String location;
    private LinkedList<CinemaRoom> cinemaRooms;

    // Constructor (ID is required because the table has no setter for it)
    public Cinema(String cinemaID) {
        this.cinemaID = cinemaID;
        this.name = "TBA";
        this.location = "TBA";
        this.cinemaRooms = new LinkedList<CinemaRoom>();
    }

    // Behavior methods

    // Rules: room must not be null, must not belong to another cinema,
    // and its ID and name must not already be used by a room in this cinema.
    // Returns true when added; throws IllegalArgumentException with the error message otherwise.
    public boolean addRoom(CinemaRoom room) {
        if (room == null) {
            throw new IllegalArgumentException("Invalid input. No room was provided.");
        }
        if (room.getCinema() != null && room.getCinema() != this) {
            throw new IllegalArgumentException("Invalid input. Room " + room.getRoomID() + " already belongs to another cinema.");
        }
        Node<CinemaRoom> node = cinemaRooms.getHead();
        while (node != null) {
            CinemaRoom existing = node.getData();
            if (existing.getRoomID().equals(room.getRoomID())) {
                throw new IllegalArgumentException("Invalid input. A room with ID " + room.getRoomID() + " already exists in this cinema.");
            }
            if (existing.getRoomName().equalsIgnoreCase(room.getRoomName())) {
                throw new IllegalArgumentException("Invalid input. A room named " + room.getRoomName() + " already exists in this cinema.");
            }
            node = node.getNext();
        }
        room.setCinema(this);
        cinemaRooms.insertAtEnd(room);
        return true;
    }

    // Rules: there must be rooms, the room ID must not be blank, the room must exist,
    // and it must have no schedules queued.
    // Returns true when removed; throws IllegalArgumentException with the error message otherwise.
    public boolean removeRoom(String roomID) {
        if (cinemaRooms.size() == 0) {
            throw new IllegalArgumentException("Invalid input. This cinema has no rooms to remove.");
        }
        CinemaRoom room = findRoom(roomID);   // throws if the ID is blank
        if (room == null) {
            throw new IllegalArgumentException("Invalid input. Room " + roomID.trim() + " was not found in this cinema.");
        }
        if (!room.getSchedules().isEmpty()) {
            throw new IllegalArgumentException("Invalid input. Room " + room.getRoomID() + " still has schedules and cannot be removed.");
        }
        cinemaRooms.remove(room);
        room.setCinema(null);
        return true;
    }

    // Rules: the cinema must have rooms, the room ID must not be blank, and the room must exist.
    // Returns the room; throws IllegalArgumentException otherwise.
    public CinemaRoom findRoom(String roomID) {
        if (cinemaRooms.size() == 0) {
            throw new IllegalArgumentException("Invalid input. This cinema has no rooms yet.");
        }
        if (!InputValidator.isNotBlank(roomID)) {
            throw new IllegalArgumentException("Invalid input. Room ID cannot be blank.");
        }
        Node<CinemaRoom> node = cinemaRooms.getHead();
        while (node != null) {
            if (node.getData().getRoomID().equals(roomID.trim())) {
                return node.getData();
            }
            node = node.getNext();
        }
        throw new IllegalArgumentException("Invalid input. Room " + roomID.trim() + " was not found in this cinema.");
    }

    // Two cinemas are equal when they have the same cinemaID.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Cinema)) {
            return false;
        }
        return cinemaID.equals(((Cinema) obj).cinemaID);
    }

    // Setters (business rules live here).
    // An invalid value throws IllegalArgumentException with the error message;
    // the controller catches it and passes e.getMessage() to the view.

    // Name: 2 to 50 characters after trimming.
    public void setName(String name) {
        if (!InputValidator.isNotBlank(name)) {
            throw new IllegalArgumentException("Invalid input. Cinema name cannot be blank.");
        }
        String cleaned = name.trim();
        if (!InputValidator.inRange(cleaned.length(), MIN_NAME_LENGTH, MAX_NAME_LENGTH)) {
            throw new IllegalArgumentException(
                "Invalid input. Cinema name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }
        this.name = cleaned;
    }

    // Location: 5 to 100 characters after trimming.
    public void setLocation(String location) {
        if (!InputValidator.isNotBlank(location)) {
            throw new IllegalArgumentException("Invalid input. Location cannot be blank.");
        }
        String cleaned = location.trim();
        if (!InputValidator.inRange(cleaned.length(), MIN_LOCATION_LENGTH, MAX_LOCATION_LENGTH)) {
            throw new IllegalArgumentException(
                "Invalid input. Location must be between " + MIN_LOCATION_LENGTH + " and " + MAX_LOCATION_LENGTH + " characters.");
        }
        this.location = cleaned;
    }

    // Getters
    public String getCinemaID() {
        return cinemaID;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public LinkedList<CinemaRoom> getCinemaRooms() {
        return cinemaRooms;
    }
}