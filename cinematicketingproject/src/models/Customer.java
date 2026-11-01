
package models;


public class Customer extends User{
    
    private String customerType;
    private String dateRegistered;
      
    Customer(String userID, String userName,String password, String fullName,String status, String customeType, String dateRegistered){
        super(userID, userName, password, fullName, status);   
        this.customerType = customerType;
        this.dateRegistered = dateRegistered;}
    
    @Override
      public String getRole(){
         return " ";}
      
 //Setters====================================
      public void setCustomerType(String customerType){
         this.customerType = customerType;}
      
 //Getters====================================
      public String getCustomerType(){
         return customerType;}
      
      public String getDateRegistered(){
         return dateRegistered;}
}