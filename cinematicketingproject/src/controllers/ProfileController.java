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

    // Shared by Admin, Cashier and Customer. The user is passed in, not stored.
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

                    // Edit full name
                    while (true) {

                        try {

                            String fullName = userView.promptFullName();

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

                    // Edit username
                    while (true) {

                        String username = userView.promptUsername();

                        // Check whether another user already has this username.
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

       //pina add para no duplication of username
       private boolean isUsernameTaken(String username, User currentUser) {

        LinkedList<User> users = userRepo.viewUsers();

        Node<User> node = users.getHead();

        while (node != null) {

            User existingUser = node.getData();

            // Do not compare the current user's username against itself.
            if (!existingUser.getUserID().equalsIgnoreCase(
                    currentUser.getUserID())) {

                if (existingUser.getUserName().equalsIgnoreCase(username)) {
                    return true;
                }
            }

            node = node.getNext();
        }

        return false;
    }
       
       
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

            // Ask for a new password only after current password verification.
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
    
    
      //needed for the change password
      private boolean hasExistingPassword(User user) {

        try {

            user.getPassword();
            return true;

        } catch (IllegalStateException e) {

            return false;
        }
    }
}
