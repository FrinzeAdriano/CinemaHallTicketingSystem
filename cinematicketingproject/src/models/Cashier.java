
package models;

import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import utility.*;
import service.*;

public class Cashier extends User{
    
    private String shift;
    
      public Cashier(String userID){
          super(userID);
       }
     
    @Override
      public String getRole(){
         return "Cashier";}
      
      
// Setters===================================
     public void setShift(String shift){
 
         if (!InputValidator.isNotBlank(shift)) {
             throw new IllegalArgumentException(
                 "Invalid input. No cashier shift was provided."
             );
         }
 
         this.shift = shift;
     }
     
//Getters====================================
     public String getShift(){
 
         if (!InputValidator.isNotBlank(shift)) {
             throw new IllegalStateException(
                 "Cashier shift has not been set."
             );
         }
 
         return shift;
     }  
}


