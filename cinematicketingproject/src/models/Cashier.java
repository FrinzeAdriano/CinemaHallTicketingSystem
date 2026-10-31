
package models;

import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;

public class Cashier extends User{
    
    private String shift;
    
      public Cashier(String userID, String userName,String password, String fullName, String status, String shift){
          super(userID, userName, password, fullName,status);
          this.shift = shift;
       }
     
    @Override
      public String getRole(){
         return "Cashier";}
      
      
// Setters===================================
     public void setShift(String shift){
         this.shift = shift;}
     
//Getters====================================
     public String getShift(){
         return shift;}
     
     
}
