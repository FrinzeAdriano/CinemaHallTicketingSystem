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
        System.out.println();
        System.out.println("========================================");
        System.out.println("      CINEMA TICKET MANAGEMENT SYSTEM");
        System.out.println("========================================");
        System.out.println("[1] Login");
        System.out.println("[2] Register as a Customer");
        System.out.println("[0] Exit");
        System.out.println("------------------------------------------");
        return readInt("Choose an option (0 to exit): ", 0, 2);
    }

    // The admin features are grouped into 5 categories; each one opens a submenu (showSubMenu).
    // 1 Account, 2 Movies and Showtimes, 3 Cinema Setup, 4 Food, Drinks and Promos,
    // 5 Sales and Records, 0 Logout
     public int showAdminMenu() {
        System.out.println();
        System.out.println("=============== ADMIN MENU ===============");
        System.out.println("[1] Account");
        System.out.println("[2] Movies and Showtimes");
        System.out.println("[3] Cinema Setup");
        System.out.println("[4] Food, Drinks and Promos");
        System.out.println("[5] Sales and Records");
        System.out.println("[0] Logout");
        System.out.println("------------------------------------------");
        return readInt("Choose an option (0 to logout): ", 0, 5);
    }

    // Submenu with 2 or 3 options. Pass null as the third option when there are only 2.
    // 0 = Back. readInt keeps asking until the number is in range.
   public int showSubMenu(String title, String first, String second, String third) {
        int fill = 42 - title.length() - 2;
        int left = fill / 2;
        int right = fill - left;

        System.out.println();
        System.out.println("=".repeat(left) + " " + title + " " + "=".repeat(right));
        System.out.println("[1] " + first);
        System.out.println("[2] " + second);
        int max = 2;
        if (third != null) {
            System.out.println("[3] " + third);
            max = 3;
        }
        System.out.println("[0] Back");
        System.out.println("------------------------------------------");
        return readInt("Choose an option (0 to go back): ", 0, max);
    }

    public int showCashierMenu() {
        System.out.println();
        System.out.println("============== CASHIER MENU ==============");
        System.out.println("[1] Browse movies");
        System.out.println("[2] Sell tickets");
        System.out.println("[3] Void a transaction");
        System.out.println("[4] My transactions");
        System.out.println("[5] Reprint receipt");
        System.out.println("[6] View inventory");
        System.out.println("[7] My profile");
        System.out.println("[0] Logout");
        System.out.println("------------------------------------------");
        return readInt("Choose an option (0 to logout): ", 0, 7);
    }

        public int showCustomerMenu() {
        System.out.println();
        System.out.println("============= CUSTOMER MENU ==============");
        System.out.println("[1] Browse movies");
        System.out.println("[2] Book tickets");
        System.out.println("[3] Cancel a booking");
        System.out.println("[4] My booking history");
        System.out.println("[5] My profile");
        System.out.println("[0] Logout");
        System.out.println("------------------------------------------");
        return readInt("Choose an option (0 to logout): ", 0, 5);
    }


    // Login Input Methods (0 goes back to the main menu)

    public String promptUsername() {
        return readString("Username (0 to go back): ");
    }

    public String promptPassword() {
        return readString("Password (0 to go back): ");
    }
}