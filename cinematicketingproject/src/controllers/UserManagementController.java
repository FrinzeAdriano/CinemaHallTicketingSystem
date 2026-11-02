package controllers;

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
    
    }
}
