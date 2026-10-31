
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;

public class Admin extends User{
    
    public Admin(String userID, String userName,String password, String fullName, String status){
       super(userID, userName, password, fullName, status);
       }
    
    @Override
      public String getRole(){
         return "Admin";}
}
