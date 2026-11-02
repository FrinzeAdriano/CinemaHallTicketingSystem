
package controllers;

import datastructures.LinkedList;
import datastructures.Node;
import models.User;
import repositories.UserRepository;
import view.UserView;

public class ProfileController {

    private UserRepository userRepo;
    private UserView userView;

    public ProfileController(UserRepository userRepo, UserView userView) {
        this.userRepo = userRepo;
        this.userView = userView;
    }

    // Manages the profile of the currently logged-in user.
    public void manageProfile(User user) {

        if (user == null || userRepo == null || userView == null) {
            if (userView != null) {
                userView.showError("Unable to access user profile.");
            }
            return;
        }
        boolean running = true;

        while (running) {

            userView.displayProfile(user);

            int choice = userView.showProfileMenu();

            switch (choice) {
                case 1:
                    editProfile(user);
                    break;

                case 2:
                    changePassword(user);
                    break;

                case 0:
                    running = false;
                    break;

                default:
                    userView.showError("Invalid option.");
                    break;
            }
        }
    }
    // Allows the user to edit their full name or username.
    public void editProfile(User user) {

        if (user == null) {
            userView.showError("User not found.");
            return;
        }
        boolean editing = true;

        while (editing) {
            int choice = userView.showEditProfileMenu(user);
            switch (choice) {

                case 1:
                    // Edit full name.
                    while (true) {

                        String fullName = userView.promptFullName();

                        // Return to the edit profile menu without making changes.
                        if (fullName != null && fullName.trim().equals("0")) {
                            break;
                        }
                        try {
                            user.setFullName(fullName);
                            userView.showMessage(
                                "Full name successfully updated."
                            );
                            break;
                        } catch (IllegalArgumentException e) {
                            userView.showError(e.getMessage());
                        }
                    }
                    break;

                case 2:
                    // Edit username.
                    while (true) {
                        String username = userView.promptUsername();
                        // Return to the edit profile menu without making changes.
                        if (username != null && username.trim().equals("0")) {
                            break;
                        }
                        // Validate username format.
                        if (username == null
                                || !username.matches("[a-zA-Z0-9]{4,20}")) {
                            userView.showError(
                                "Invalid input. Username must be 4-20 letters or digits."
                            );
                            continue;
                        }

                        // Check for duplicate usernames before updating.
                        if (isUsernameTaken(username, user)) {
                            userView.showError(
                                "Username already exists. Please enter another username."
                            );
                            continue;
                        }
                        try {
                            user.setUserName(username);
                            userView.showMessage(
                                "Username successfully updated."
                            );
                            break;
                        } catch (IllegalArgumentException e) {
                            userView.showError(e.getMessage());
                        }
                    }
                    break;

                case 0:
                    editing = false;
                    break;

                default:
                    userView.showError("Invalid option.");
                    break;
            }
        }
    }

    // Checks whether another user already uses the specified username.
    private boolean isUsernameTaken(String username, User currentUser) {

        LinkedList<User> users = userRepo.viewUsers();

        if (users == null) {
            return false;
        }
        Node<User> node = users.getHead();
        while (node != null) {
            User existingUser = node.getData();
            if (existingUser != null
                    && !existingUser.getUserID().equalsIgnoreCase(
                        currentUser.getUserID())) {
                if (existingUser.getUserName() != null
                        && existingUser.getUserName().equalsIgnoreCase(
                            username)) {
                    return true;
                }
            }
            node = node.getNext();
        }
        return false;
    }

    // Allows the user to change their password.
    public void changePassword(User user) {

        if (user == null) {
            userView.showError("User not found.");
            return;
        }
        String oldPassword = null;
        // Check whether the user already has a password.
        try {
            user.getPassword();
        } catch (IllegalStateException e) {
            // First-time password setup.
            oldPassword = null;
        }
        while (true) {
            // Verify the current password before asking for a new one.
            if (oldPassword != null || hasExistingPassword(user)) {
                oldPassword = userView.promptOldPassword();
                if (!oldPassword.equals(user.getPassword())) {
                    userView.showError(
                        "Incorrect current password. Please try again."
                    );
                    continue;
                }
            }

            // Ask for a new password after current password verification.
            String newPassword = userView.promptNewPassword();
            if (user.changePassword(oldPassword, newPassword)) {
                userView.showMessage(
                    "Password successfully changed."
                );
                return;
            } else {
                userView.showError(
                    "Unable to change password. Please try again."
                );
            }
        }
    }
    // Checks whether the user already has a password.
    private boolean hasExistingPassword(User user) {
        try {
            user.getPassword();
            return true;
        } catch (IllegalStateException e) {

            return false;
        }
    }
}
