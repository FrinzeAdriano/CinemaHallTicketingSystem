package controllers;

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
    }

    public void editProfile(User user) {
    }

    public void changePassword(User user) {
    }
}
