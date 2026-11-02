package view;
import controllers.*;
import models.*;
import datastructures.*;
import repositories.*;
import utility.*;
import service.*;
public class CinemaTicketManagementSystemView extends BaseView{
    
     // Menu Methods

    // 1 = Login, 2 = Register as customer, 0 = Exit
    public int showMainMenu() {
        System.out.println("\n==========================================");
        System.out.println("      CINEMA TICKET MANAGEMENT SYSTEM");
        System.out.println("==========================================");
        System.out.println("1. Login");
        System.out.println("2. Register as a customer");
        System.out.println("0. Exit");
        return readInt("Choose an option: ", 0, 2);}

    // 1 My profile, 2 Users, 3 Movies, 4 Cinemas, 5 Rooms, 6 Seats, 7 Schedules,
    // 8 Inventory, 9 Vouchers, 10 Transaction history, 11 Sales reports, 0 Logout
    public int showAdminMenu() {
        System.out.println("\n===== ADMIN MENU =====");
        System.out.println(" 1. My profile");
        System.out.println(" 2. Manage users");
        System.out.println(" 3. Manage movies");
        System.out.println(" 4. Manage cinemas");
        System.out.println(" 5. Manage rooms");
        System.out.println(" 6. Manage seats");
        System.out.println(" 7. Manage schedules");
        System.out.println(" 8. Manage inventory");
        System.out.println(" 9. Manage vouchers");
        System.out.println("10. Transaction history");
        System.out.println("11. Sales reports");
        System.out.println(" 0. Logout");
        return readInt("Choose an option: ", 0, 11);}

    public int showCashierMenu() {
        System.out.println("\n===== CASHIER MENU =====");
        System.out.println("1. Sell tickets");
        System.out.println("2. Void a transaction");
        System.out.println("3. My transactions");
        System.out.println("4. Reprint receipt");
        System.out.println("5. View inventory");
        System.out.println("6. My profile");
        System.out.println("0. Logout");
        return readInt("Choose an option: ", 0, 6);}

    public int showCustomerMenu() {
        System.out.println("\n===== CUSTOMER MENU =====");
        System.out.println("1. Browse movies");
        System.out.println("2. Book tickets");
        System.out.println("3. Cancel a booking");
        System.out.println("4. My booking history");
        System.out.println("5. My profile");
        System.out.println("0. Logout");
        return readInt("Choose an option: ", 0, 5);}


    // Login Input Methods

    public String promptUsername() {
        return readString("Username: ");
    }

    public String promptPassword() {
        return readString("Password: ");
    }
}
