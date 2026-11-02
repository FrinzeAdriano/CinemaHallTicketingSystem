package controllers;

import datastructures.LinkedList;
import datastructures.Node;
import models.User;
import repositories.UserRepository;
import view.UserView;

public class UserManagementController {
    private UserRepository userRepo;
    private UserView userView;

    public UserManagementController(UserRepository userRepo, UserView userView) {
        this.userRepo = userRepo;
        this.userView = userView;
    }

     public void manageUsers() {

        if (userRepo == null || userView == null) {
            throw new IllegalStateException(
                "User management dependencies cannot be null."
            );
        }

        boolean running = true;

        while (running) {

            int choice = userView.showUserManagementMenu();

            switch (choice) {

                case 1:
                    addUser();
                    break;

                case 2:
                    viewAllUsers();
                    break;

                case 3:
                    filterUsersByRole();
                    break;

                case 4:
                    deactivateUser();
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
     
      private void addUser() {

        String role = userView.promptRole();
        String userID = userRepo.generateUserID(role);

        try {

            User user = userView.promptNewUser(userID);

            // Check for duplicate usernames.
            while (isUsernameTaken(user.getUserName())) {

                userView.showError(
                    "Username already exists. Please enter another username."
                );

                user.setUserName(userView.promptUsername());
            }

            if (userRepo.addUser(user)) {

                userView.showMessage(
                    "User successfully added. User ID: " + userID
                );

            } else {

                userView.showError("Unable to add user.");
            }

        } catch (IllegalArgumentException e) {

            userView.showError(e.getMessage());
        }
    }


    // Checks whether a username is already being used

    private boolean isUsernameTaken(String username) {

        Node<User> node = userRepo.viewUsers().getHead();

        while (node != null) {

            if (node.getData().getUserName().equalsIgnoreCase(username)) {
                return true;
            }

            node = node.getNext();
        }

        return false;
    }


    // Displays all users

    private void viewAllUsers() {

        LinkedList<User> users = userRepo.viewUsers();

        if (users.size() == 0) {

            userView.showMessage("No users found.");
            return;
        }

        userView.displayUsers(users);
    }


    // Filters users by role

    private void filterUsersByRole() {

        String role = userView.promptRole();

        LinkedList<User> filteredUsers = userRepo.filterByRole(role);

        if (filteredUsers.size() == 0) {

            userView.showMessage(
                "No users found with the role " + role + "."
            );

            return;
        }

        userView.displayUsers(filteredUsers);
    }


    // Deactivates a user

    private void deactivateUser() {

        User user = selectUser();

        if (user == null) {
            return;
        }

        if (user.getStatus().equalsIgnoreCase("Inactive")) {

            userView.showMessage("This user is already inactive.");
            return;
        }

        if (!userView.confirm(
                "Are you sure you want to deactivate " + user.getUserName() + "?")) {
            return;
        }

        if (userRepo.deactivateUser(user.getUserID())) {

            userView.showMessage("User successfully deactivated.");

        } else {

            userView.showError("Unable to deactivate user.");
        }
    }


    // Selects a user using their ID.
    // Invalid IDs are re-prompted in the same field.

    private User selectUser() {

        LinkedList<User> users = userRepo.viewUsers();

        if (users.size() == 0) {

            userView.showMessage("No users available.");
            return null;
        }

        userView.displayUsers(users);

        while (true) {

            String userID = userView.promptUserID();

            if (userID.equals("0")) {
                return null;
            }

            User user = userRepo.findUser(userID);

            if (user != null) {
                return user;
            }

            userView.showError(
                "User ID was not found. Please try again."
            );
        }
    }
}
