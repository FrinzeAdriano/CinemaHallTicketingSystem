package repositories;

import datastructures.LinkedList;
import datastructures.Node;
import models.User;

public class UserRepository {
    private LinkedList<User> users = new LinkedList<User>();

    // User management methods

    // Rules: the user must not be null, and the user ID and username must not already exist.
    // Returns true when added; throws IllegalArgumentException otherwise.
    public boolean addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Invalid input. No user was provided.");
        }
        if (findUser(user.getUserID()) != null) {
            throw new IllegalArgumentException("Invalid input. User ID " + user.getUserID() + " already exists.");
        }
        if (isUsernameTaken(user.getUserName())) {
            throw new IllegalArgumentException("Invalid input. Username " + user.getUserName() + " is already taken.");
        }
        users.insertAtEnd(user);
        return true;
    }

    public LinkedList<User> viewUsers() {
        return users;
    }

    // Returns null when the ID is not found.
    public User findUser(String userID) {
        if (userID == null) {
            return null;
        }
        Node<User> n = users.getHead();
        while (n != null) {
            if (n.getData().getUserID().equalsIgnoreCase(userID.trim())) {
                return n.getData();
            }
            n = n.getNext();
        }
        return null;
    }

    // Same as findUser, but throws IllegalArgumentException when the ID is not found
    // (so it can be used with LoopUtility.retryUntilValidResult).
    public User findUserByID(String userID) {
        User user = findUser(userID);
        if (user == null) {
            throw new IllegalArgumentException("Invalid input. User " + (userID == null ? "" : userID.trim()) + " was not found.");
        }
        return user;
    }

    // Case-insensitive check used before a username is accepted.
    public boolean isUsernameTaken(String username) {
        if (username == null) {
            return false;
        }
        Node<User> n = users.getHead();
        while (n != null) {
            if (n.getData().getUserName().equalsIgnoreCase(username.trim())) {
                return true;
            }
            n = n.getNext();
        }
        return false;
    }

    // Returns a new list with only the users of that role (empty when there are none).
    public LinkedList<User> filterByRole(String role) {
        LinkedList<User> result = new LinkedList<User>();
        if (role == null) {
            return result;
        }
        Node<User> n = users.getHead();
        while (n != null) {
            if (n.getData().getRole().equalsIgnoreCase(role.trim())) {
                result.insertAtEnd(n.getData());
            }
            n = n.getNext();
        }
        return result;
    }

    // Rules: the user must exist and must still be Active.
    // Returns true when deactivated; throws IllegalArgumentException otherwise.
    public boolean deactivateUser(String userID) {
        User user = findUserByID(userID);
        if (user.getStatus().equalsIgnoreCase("Inactive")) {
            throw new IllegalArgumentException("Invalid input. User " + user.getUserID() + " is already inactive.");
        }
        user.setStatus("Inactive");
        return true;
    }

    // ADM001, CSH001, CUS001, ... (next free number for that role's prefix)
    public String generateUserID(String role) {
        String prefix = prefixFor(role);
        int max = 0;
        Node<User> n = users.getHead();
        while (n != null) {
            String id = n.getData().getUserID();
            if (id.startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(id.substring(prefix.length()));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException e) {
                    // not one of our generated IDs, ignore it
                }
            }
            n = n.getNext();
        }
        return prefix + String.format("%03d", max + 1);
    }

    private String prefixFor(String role) {
        if (role == null) {
            return "USR";
        }
        if (role.equalsIgnoreCase("Admin")) {
            return "ADM";
        }
        if (role.equalsIgnoreCase("Cashier")) {
            return "CSH";
        }
        if (role.equalsIgnoreCase("Customer")) {
            return "CUS";
        }
        return "USR";
    }
}