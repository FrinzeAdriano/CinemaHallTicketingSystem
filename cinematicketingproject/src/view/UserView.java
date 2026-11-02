package view;

import datastructures.LinkedList;
import datastructures.Node;
import models.Cashier;
import models.Customer;
import models.User;
import utility.InputValidator;
import java.util.ArrayList;

public class UserView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public UserView() {
        super();
    }

    // Menus and prompts

    public int showProfileMenu() {
        System.out.println();
        System.out.println("===== MY PROFILE =====");
        System.out.println("[1] Edit profile");
        System.out.println("[2] Change password");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readInt("Choose an option (0 to go back): ", 0, 2);
    }

    public int showEditProfileMenu(User user) {
        System.out.println();
        System.out.println("===== EDIT PROFILE (" + user.getUserID() + ") =====");
        System.out.println("[1] Full name (" + user.getFullName() + ")");
        System.out.println("[2] Username  (" + user.getUserName() + ")");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readInt("Choose an option (0 to go back): ", 0, 2);
    }

    public int showUserManagementMenu() {
        System.out.println();
        System.out.println("===== USER MANAGEMENT =====");
        System.out.println("[1] Add user");
        System.out.println("[2] View all users");
        System.out.println("[3] Filter users by role");
        System.out.println("[4] Deactivate user");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readInt("Choose an option (0 to go back): ", 0, 4);
    }

    // Returns "Admin", "Cashier" or "Customer", or "0" when the user chooses to go back.
    public String promptRole() {
        System.out.println();
        System.out.println("===== SELECT USER ROLE =====");
        System.out.println("[1] Admin");
        System.out.println("[2] Cashier");
        System.out.println("[3] Customer");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        int choice = readInt("Choose a role (0 to go back): ", 0, 3);
        if (choice == 1) {
            return "Admin";
        }
        if (choice == 2) {
            return "Cashier";
        }
        if (choice == 3) {
            return "Customer";
        }
        return "0";
    }

    // Returns "Morning", "Afternoon" or "Evening", or "0" when the user chooses to go back.
    public String promptShift() {
        System.out.println();
        System.out.println("===== SELECT CASHIER SHIFT =====");
        System.out.println("[1] Morning");
        System.out.println("[2] Afternoon");
        System.out.println("[3] Evening");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        int choice = readInt("Choose a shift (0 to go back): ", 0, 3);
        if (choice == 1) {
            return "Morning";
        }
        if (choice == 2) {
            return "Afternoon";
        }
        if (choice == 3) {
            return "Evening";
        }
        return "0";
    }

    // Returns "Walk-in" or "Online", or "0" when the user chooses to go back.
    public String promptCustomerType() {
        System.out.println();
        System.out.println("===== SELECT CUSTOMER TYPE =====");
        System.out.println("[1] Walk-in");
        System.out.println("[2] Online");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        int choice = readInt("Choose a type (0 to go back): ", 0, 2);
        if (choice == 1) {
            return "Walk-in";
        }
        if (choice == 2) {
            return "Online";
        }
        return "0";
    }

    // The field prompts only read the raw text. The setters check it, and the controller
    // asks again (LoopUtility) when a setter rejects it.

    public String promptUserID() {
        return readString("Enter user ID (0 to go back): ");
    }

    public String promptUsername() {
        return readString("Username, 4-20 letters or digits (0 to go back): ");
    }

    public String promptPassword() {
        return readString("Password, at least 6 characters with 1 digit (0 to go back): ");
    }

    public String promptConfirmPassword() {
        return readString("Confirm password: ");
    }

    public String promptFullName() {
        return readString("Full name (0 to go back): ");
    }

    public String promptOldPassword() {
        return readString("Current password: ");
    }

    // Used by the change password screen: keeps asking until the password is valid and confirmed.
    public String promptNewPassword() {
        while (true) {
            String password = readString("New password (at least 6 characters, 1 digit): ");
            if (!InputValidator.isValidPassword(password)) {
                showError("Invalid input. Password must be at least 6 characters and contain at least 1 digit.");
                continue;
            }
            String confirmation = readString("Confirm password: ");
            if (password.equals(confirmation)) {
                return password;
            }
            showError("Invalid input. Passwords do not match.");
        }
    }

    // Display methods

    public void displayProfile(User user) {
        System.out.println();
        System.out.println("----- PROFILE -----");
        System.out.println("User ID    : " + user.getUserID());
        System.out.println("Role       : " + user.getRole());
        System.out.println("Username   : " + user.getUserName());
        System.out.println("Full name  : " + user.getFullName());
        System.out.println("Status     : " + user.getStatus());
        if (user instanceof Cashier) {
            System.out.println("Shift      : " + ((Cashier) user).getShift());
        } else if (user instanceof Customer) {
            Customer customer = (Customer) user;
            System.out.println("Type       : " + customer.getCustomerType());
            System.out.println("Registered : " + customer.getDateRegistered());
        }
    }

    public void displayUsers(LinkedList<User> users) {
        ArrayList<String[]> rows = new ArrayList<>();
        Node<User> node = users.getHead();
        while (node != null) {
            User u = node.getData();
            rows.add(new String[] {u.getUserID(), u.getRole(), u.getUserName(), u.getFullName(), u.getStatus()});
            node = node.getNext();
        }
        printTable("ALL USERS", new String[] {"ID", "Role", "Username", "Full name", "Status"}, rows);
    }
}