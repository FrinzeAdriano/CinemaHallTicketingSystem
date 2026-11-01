
package view;

import datastructures.*;
import models.*;
import repositories.*;
import utility.*;
import service.*;
import java.time.LocalDate;

public class UserView extends BaseView {


    // Menu Methods

    public int showProfileMenu() {
        System.out.println("\n===== MY PROFILE =====");
        System.out.println("1. Edit profile");
        System.out.println("2. Change password");
        System.out.println("0. Back");
        return readInt("Choose an option: ", 0, 2);}

    public int showEditProfileMenu(User user) {
        System.out.println("\n===== EDIT PROFILE (" + user.getUserID() + ") =====");
        System.out.println("1. Full name (" + user.getFullName() + ")");
        System.out.println("2. Username (" + user.getUserName() + ")");
        System.out.println("0. Back");
        return readInt("Choose an option: ", 0, 2);}

    public int showUserManagementMenu() {
        System.out.println("\n===== USER MANAGEMENT =====");
        System.out.println("1. Add user");
        System.out.println("2. View all users");
        System.out.println("3. Filter users by role");
        System.out.println("4. Deactivate user");
        System.out.println("5. Reactivate user");
        System.out.println("6. Delete user");
        System.out.println("0. Back");
        return readInt("Choose an option: ", 0, 6);}


    // User Input Methods

    // The role is read from the ID prefix (ADM / CSH / CUS) that
    // UserRepository.generateUserID produced. Unknown prefix: ask for the role.
    public User promptNewUser(String userID) {
        String role = roleFromID(userID);
        if (role == null) {
            role = promptRole();
        }
 
        String username = promptUsername();
        String password = promptNewPassword();
        String fullName = promptFullName();
 
        if (role.equals("Admin")) {
            return new Admin(userID);
        }
        if (role.equals("Cashier")) {
            return new Cashier(userID);
        }
        return new Customer(userID, username, password, fullName, "Active",
                promptCustomerType(), LocalDate.now().toString());}
    
        // Asks which kind of account to create. Returns Admin, Cashier or Customer.
    private String promptRole() {
        System.out.println("Role:");
        System.out.println("1. Admin");
        System.out.println("2. Cashier");
        System.out.println("3. Customer");
        int choice = readInt("Choose a role: ", 1, 3);
        if (choice == 1) {
            return "Admin";
        }
        if (choice == 2) {
            return "Cashier";
        }
        return "Customer";}

    public String promptUserID() {
        return readString("Enter user ID: ");}

    public String promptFullName() {
        return readString("Full name: ");}

    public String promptUsername() {
        while (true) {
            String username = readString("Username (4-20 letters or digits): ");
            if (InputValidator.isValidUsername(username)) {
                return username;
            }
            showError("Username must be 4-20 letters or digits, no spaces or symbols.");
        }}

    public String promptOldPassword() {
        return readString("Current password: ");}

    public String promptNewPassword() {
        while (true) {
            String password = readString("New password (at least 6 characters): ");
            if (!InputValidator.isValidPassword(password)) {
                showError("Password must be at least 6 characters.");
                continue;
            }
            String confirmation = readString("Confirm password: ");
            if (password.equals(confirmation)) {
                return password;
            }
            showError("Passwords do not match. Try again.");
        }}


    // Display Methods

    // Display Methods
 
    public void displayProfile(User user) {
        System.out.println("\n===== PROFILE =====");
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
        }}
 
    public void displayUsers(LinkedList users) {
        if (users == null || users.getHead() == null) {
            System.out.println("No users found.");
            return;
        }
        String row = "%-8s %-9s %-20s %-25s %-9s%n";
        System.out.println();
        System.out.printf(row, "ID", "Role", "Username", "Full name", "Status");
        System.out.println("---------------------------------------------------------------------------");
        Node n = users.getHead();
        while (n != null) {
            User user = (User) n.getData();
            System.out.printf(row, user.getUserID(), user.getRole(), user.getUserName(),
                    user.getFullName(), user.getStatus());
            n = n.getNext();
        }}
    
// EXTRA, CAN REMOVE ROLEFROMID PERO NEED TALAGA YUNG    promptShift()  promptCustomerType()
    
    private String roleFromID(String userID) {
        if (userID == null) {
            return null;
        }
        if (userID.startsWith("ADM")) {
            return "Admin";
        }
        if (userID.startsWith("CSH")) {
            return "Cashier";
        }
        if (userID.startsWith("CUS")) {
            return "Customer";
        }
        return null;
    }
 
    private String promptShift() {
        System.out.println("Shift:");
        System.out.println("1. Morning");
        System.out.println("2. Afternoon");
        System.out.println("3. Night");
        int choice = readInt("Choose a shift: ", 1, 3);
        if (choice == 1) {
            return "Morning";
        }
        if (choice == 2) {
            return "Afternoon";
        }
        return "Night";
    }
 
    private String promptCustomerType() {
        System.out.println("Customer type:");
        System.out.println("1. Regular");
        System.out.println("2. Student");
        System.out.println("3. Senior");
        int choice = readInt("Choose a type: ", 1, 3);
        if (choice == 1) {
            return "Regular";
        }
        if (choice == 2) {
            return "Student";
        }
        return "Senior";
    }

}
