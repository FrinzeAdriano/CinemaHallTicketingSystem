package models;

import utility.*;

public class Customer extends User{
    
    private String customerType;
    private String dateRegistered;
      
    public Customer(String userID){
        super(userID);   
        this.customerType = customerType;
        this.dateRegistered = dateRegistered;}
    
    @Override
      public String getRole(){
         return "Customer";}
      
 //Setters====================================
      public void setCustomerType(String customerType){
 
         if (!InputValidator.isNotBlank(customerType)) {
             throw new IllegalArgumentException(
                 "Invalid input. No customer type was provided."
             );
         }
 
         this.customerType = customerType;
      }
 
 //Getters====================================
      public String getCustomerType(){
 
         if (!InputValidator.isNotBlank(customerType)) {
             throw new IllegalStateException(
                 "Customer type has not been set."
             );
         }
 
         return customerType;
      }
 
      public String getDateRegistered(){
 
         if (!InputValidator.isNotBlank(dateRegistered)) {
             throw new IllegalStateException(
                 "Customer registration date has not been set."
             );
         }
 
         return dateRegistered;
      }
}
