
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;

public abstract class User {

   private final String userID;
   private String userName;
   private String password;
   private String fullName;
   private String status;

   
   public User(String userID){
       this.userID = userID;
       }
   
   public abstract String getRole();
   
   // Returns false if the old password is wrong or the new one is invalid.
   // First-time setup: while no password is set yet, pass null as oldPass.
   public boolean changePassword(String oldPass, String newPass){
       if (password != null && !password.equals(oldPass)) {
           return false;
       }
       if (!InputValidator.isValidPassword(newPass)) {
           return false;
       }
       this.password = newPass;
       return true;}
 
 
// Setters =====================================   
   public void setUserName(String userName){
 
       if (!InputValidator.isValidUsername(userName)) {
           throw new IllegalArgumentException(
               "Invalid input. Username must be 4-20 letters or digits."
           );
       }
 
       this.userName = userName;
   }
 
   public void setFullName(String fullName){
 
       if (!InputValidator.isNotBlank(fullName)) {
           throw new IllegalArgumentException(
               "Invalid input. No full name was provided."
           );
       }
 
       this.fullName = fullName;
   }
 
   public void setStatus(String status){
 
       if (!InputValidator.isNotBlank(status)) {
           throw new IllegalArgumentException(
               "Invalid input. No user status was provided."
           );
       }
 
       this.status = status;
   }
   
 
 // Getters =====================================  
   public String getUserID(){
       return userID;
   }
 
   public String getUserName(){
 
       if (!InputValidator.isNotBlank(userName)) {
           throw new IllegalStateException(
               "Username has not been set."
           );
       }
 
       return userName;
   }
 
   public String getPassword(){
 
       if (!InputValidator.isNotBlank(password)) {
           throw new IllegalStateException(
               "Password has not been set."
           );
       }
 
       return password;
   }
 
   public String getFullName(){
 
       if (!InputValidator.isNotBlank(fullName)) {
           throw new IllegalStateException(
               "Full name has not been set."
           );
       }
 
       return fullName;
   }
 
   public String getStatus(){
 
       if (!InputValidator.isNotBlank(status)) {
           throw new IllegalStateException(
               "User status has not been set."
           );
       }
 
       return status;
   }
}

