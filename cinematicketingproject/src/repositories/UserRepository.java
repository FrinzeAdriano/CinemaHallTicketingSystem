package repositories;
import controllers.*;
import models.*;
import datastructures.*;
import view.*;
import utility.*;
import service.*;

public class UserRepository {

    private LinkedList users = new LinkedList();


    // User Management Methods

    // Fails on null, duplicate user ID or duplicate username.
    public boolean addUser(User user) {
        if (user == null) {
            return false;
        }
        if (findUser(user.getUserID()) != null) {
            return false;
        }
        Node<User> n = users.getHead();
        while (n != null) {
            if (n.getData().getUserName().equalsIgnoreCase(user.getUserName())) {
                return false;
            }
            n = n.getNext();
        }
        users.insertAtEnd(user);
        return true;}
 
    public LinkedList viewUsers() {
        return users;
    }
 
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
 
    public LinkedList filterByRole(String role) {
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
 
    // Returns true only if the user exists and was Active.
    public boolean deactivateUser(String userID) {
        User user = findUser(userID);
        if (user == null || user.getStatus().equalsIgnoreCase("Inactive")) {
            return false;
        }
        user.setStatus("Inactive");
        return true;
    }
 
    // Returns true only if the user exists and was Inactive.
    public boolean reactivateUser(String userID) {
        User user = findUser(userID);
        if (user == null || user.getStatus().equalsIgnoreCase("Active")) {
            return false;
        }
        user.setStatus("Active");
        return true;
    }
 
    // The controller should check history.hasTransactionsBy(userID) before calling this.
    public boolean deleteUser(String userID) {
        User user = findUser(userID);
        if (user == null) {
            return false;
        }
        return users.remove(user);}
 
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
        return prefix + String.format("%03d", max + 1);}
 
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
