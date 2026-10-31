
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

   
   public User(String userID, String userName,String password, String fullName,String status){
       this.userID = userID;
       this.userName = userName;
       this.password = password;
       this.fullName = fullName;
       this.status = status;
       }
   
   public abstract String getRole();
   
   // Returns false if the old password is wrong or the new one is invalid.
   public boolean changePassword(String oldPass, String newPass){
       if (oldPass == null || !password.equals(oldPass)) {
           return false;
       }
       if (!InputValidator.isValidPassword(newPass)) {
           return false;
       }
       this.password = newPass;
       return true;}


// Setters =====================================   
   public void setUserName(String userName){
       this.userName = userName;}
   
   public void setFullName(String fullName){
       this.fullName = fullName;}
     
   public void setStatus(String status){
       this.status = status;}
   

 // Getters =====================================  
   public String getUserID(){
       return userID;}
   
   public String getUserName(){
       return userName;}
   
   public String getPassword(){
       return password;}
   
   public String getFullName(){
       return fullName;}
   
   public String getStatus(){
       return status;}
}
