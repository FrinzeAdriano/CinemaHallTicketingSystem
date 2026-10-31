
package view;

import datastructures.*;
import models.*;
import repositories.*;
import utility.*;
import service.*;

public class UserView extends BaseView {


    // Menu Methods

    public int showProfileMenu() {
         return 0;}

    public int showEditProfileMenu(User user) {
         return 0;}

    public int showUserManagementMenu() {
         return 0;}


    // User Input Methods

    public User promptNewUser(String userID) {
         return null;}

    public String promptUserID() {
         return " ";}

    public String promptFullName() {
         return " ";}

    public String promptUsername() {
         return " ";}

    public String promptOldPassword() {
         return " ";}

    public String promptNewPassword() {
         return " ";}


    // Display Methods

    public void displayProfile(User user) {

    }

    public void displayUsers(LinkedList users) {

    }

}
