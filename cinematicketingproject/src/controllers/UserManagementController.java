package controllers;

import datastructures.*;
import models.*;
import repositories.*;
import utility.*;
import view.*;

public class UserManagementController {
    private UserRepository userRepo;
    private UserView userView;

    public UserManagementController(UserRepository userRepo, UserView userView) {
        this.userRepo = userRepo;
        this.userView = userView;
    }

    public void manageUsers() {
        int choice = -1;
        while (choice != 0) {
            choice = userView.showUserManagementMenu();
            try {
                // View, filter and deactivate need at least one user.
                if (userRepo.viewUsers().size() == 0 && choice >= 2 && choice <= 4) {
                    throw new IllegalArgumentException("Invalid input. There are no users yet.");
                }
                switch (choice) {
                    case 1: {
                        // Add user (role first, then each field is asked again until it is valid;
                        // 0 at any prompt goes back without adding)
                        String role = userView.promptRole();
                        if (role.equals("0")) {
                            break;
                        }
                        User user = createUser(role, userRepo.generateUserID(role));
                        if (LoopUtility.retryUntilValid(userView::promptUsername,
                                name -> {
                                    if (userRepo.isUsernameTaken(name)) {
                                        throw new IllegalArgumentException("Invalid input. Username " + name.trim() + " is already taken.");
                                    }
                                    user.setUserName(name);
                                }, userView::showError, "0")
                                && LoopUtility.retryUntilValid(userView::promptPassword,
                                password -> setNewPassword(user, password), userView::showError, "0")
                                && LoopUtility.retryUntilValid(userView::promptFullName, user::setFullName, userView::showError, "0")
                                && promptRoleDetails(user)) {
                            user.setStatus("Active");
                            userRepo.addUser(user);   // throws on a duplicate ID or username
                            userView.showMessage("User " + user.getUserID() + " was added.");
                        }
                        break;
                    }
                    case 2:
                        userView.displayUsers(userRepo.viewUsers());
                        break;
                    case 3: {
                        // Filter users by role (0 to go back)
                        String role = userView.promptRole();
                        if (!role.equals("0")) {
                            LinkedList<User> filtered = userRepo.filterByRole(role);
                            if (filtered.size() == 0) {
                                throw new IllegalArgumentException("Invalid input. No users found with the role " + role + ".");
                            }
                            userView.displayUsers(filtered);
                        }
                        break;
                    }
                    case 4: {
                        // Deactivate user (table first, then found by ID, 0 to go back). The ID and status
                        // are checked first, and the confirmation is only asked when the user can be deactivated.
                        userView.displayUsers(userRepo.viewUsers());
                        Boolean deactivated = LoopUtility.retryUntilValidResult(userView::promptUserID,
                                id -> {
                                    User found = userRepo.findUserByID(id);   // throws if not found
                                    if (found.getStatus().equalsIgnoreCase("Inactive")) {
                                        throw new IllegalArgumentException("Invalid input. User " + found.getUserID() + " is already inactive.");
                                    }
                                    return userView.confirm("Deactivate " + found.getUserName() + "?")
                                            && userRepo.deactivateUser(found.getUserID());
                                }, userView::showError, "0");
                        if (deactivated != null) {
                            userView.showMessage(deactivated ? "User was deactivated." : "Deactivation cancelled.");
                        }
                        break;
                    }
                    default:
                        break;   // 0 exits the loop
                }
            } catch (IllegalArgumentException e) {
                userView.showError(e.getMessage());
            }
        }
    }

    // Helpers

    // Builds the right kind of user for the chosen role.
    private User createUser(String role, String userID) {
        if (role.equals("Admin")) {
            return new Admin(userID);
        }
        if (role.equals("Cashier")) {
            return new Cashier(userID);
        }
        return new Customer(userID);
    }

    // Checks the password, asks the user to confirm it, then sets it.
    // First-time setup, so the old password is null. Throws IllegalArgumentException if it is rejected.
    private void setNewPassword(User user, String password) {
        if (!InputValidator.isValidPassword(password)) {
            throw new IllegalArgumentException("Invalid input. Password must be at least 6 characters and contain at least 1 digit.");
        }
        if (!password.equals(userView.promptConfirmPassword())) {
            throw new IllegalArgumentException("Invalid input. Passwords do not match.");
        }
        user.changePassword(null, password);
    }

    // Cashiers need a shift and customers need a type; admins need nothing extra.
    // Returns false when the user typed 0 to go back.
    private boolean promptRoleDetails(User user) {
        if (user instanceof Cashier) {
            return LoopUtility.retryUntilValid(userView::promptShift, ((Cashier) user)::setShift, userView::showError, "0");
        }
        if (user instanceof Customer) {
            return LoopUtility.retryUntilValid(userView::promptCustomerType, ((Customer) user)::setCustomerType, userView::showError, "0");
        }
        return true;
    }
}