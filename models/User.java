package models;

import utility.InputValidator;

public abstract class User {
    private final String userID;
    private String userName;
    private String password;
    private String fullName;
    private String status;

    // Constructor (ID is required because the table has no setter for it)
    public User(String userID) {
        this.userID = userID;
    }

    // Behavior methods

    public abstract String getRole();

    // Rules: the old password must match (skipped while no password is set yet, so pass null
    // for first-time setup) and the new password must be valid (6+ characters, at least 1 digit).
    // Returns true when the password was changed, false otherwise.
    public boolean changePassword(String oldPass, String newPass) {
        if (password != null && !password.equals(oldPass)) {
            return false;
        }
        if (!InputValidator.isValidPassword(newPass)) {
            return false;
        }
        this.password = newPass;
        return true;
    }

    // Setters (business rules live here).
    // An invalid value throws IllegalArgumentException with the error message;
    // the controller catches it and passes e.getMessage() to the view.

    // Username: 4 to 20 letters or digits, no spaces.
    public void setUserName(String userName) {
        if (!InputValidator.isValidUsername(userName)) {
            throw new IllegalArgumentException("Invalid input. Username must be 4-20 letters or digits.");
        }
        this.userName = userName;
    }

    // Full name: not blank, starts with a letter, then only letters, spaces and . ' & -
    public void setFullName(String fullName) {
        if (!InputValidator.isNotBlank(fullName)) {
            throw new IllegalArgumentException("Invalid input. No full name was provided.");
        }
        String cleaned = fullName.trim();
        if (!cleaned.matches("\\p{L}[\\p{L} .'&-]*")) {
            throw new IllegalArgumentException(
                    "Invalid input. Full name cannot contain numbers or symbols. Use letters, spaces, and . ' & - only.");
        }
        this.fullName = cleaned;
    }

    // Status: Active or Inactive (case-insensitive, stored in that form).
    public void setStatus(String status) {
        if (!InputValidator.isNotBlank(status)) {
            throw new IllegalArgumentException("Invalid input. No user status was provided.");
        }
        String formatted = status.trim();
        if (formatted.equalsIgnoreCase("Active")) {
            this.status = "Active";
        } else if (formatted.equalsIgnoreCase("Inactive")) {
            this.status = "Inactive";
        } else {
            throw new IllegalArgumentException("Invalid input. User status must be Active or Inactive.");
        }
    }

    // Getters (the ones that need a value throw IllegalStateException if it was never set)
    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        if (!InputValidator.isNotBlank(userName)) {
            throw new IllegalStateException("Username has not been set.");
        }
        return userName;
    }

    public String getPassword() {
        if (!InputValidator.isNotBlank(password)) {
            throw new IllegalStateException("Password has not been set.");
        }
        return password;
    }

    public String getFullName() {
        if (!InputValidator.isNotBlank(fullName)) {
            throw new IllegalStateException("Full name has not been set.");
        }
        return fullName;
    }

    public String getStatus() {
        if (!InputValidator.isNotBlank(status)) {
            throw new IllegalStateException("User status has not been set.");
        }
        return status;
    }
}