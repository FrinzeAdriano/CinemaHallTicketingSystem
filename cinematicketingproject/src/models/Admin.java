
package models;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;

public class Admin extends User{
    
    public Admin(String userID){
       super(userID);
       }
    
    @Override
      public String getRole(){

         return "Admin";}
}


