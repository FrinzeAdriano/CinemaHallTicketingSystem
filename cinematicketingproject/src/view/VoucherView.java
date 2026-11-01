
package view;

import controllers.*;
import models.*;
import datastructures.*;
import repositories.*;
import utility.*;
import service.*;
import java.time.LocalDate;

public class VoucherView extends BaseView {


    // Menu Methods
 
    public int showVoucherMenu() {
        System.out.println("\n===== VOUCHER MANAGEMENT =====");
        System.out.println("1. Add voucher");
        System.out.println("2. View all vouchers");
        System.out.println("3. Edit voucher");
        System.out.println("4. Deactivate voucher");
        System.out.println("5. Remove voucher");
        System.out.println("0. Back");
        return readInt("Choose an option: ", 0, 5);}
 
    // Voucher Input Methods
 
    public Voucher promptNewVoucher() {
        String code;
        while (true) {
            code = readString("Voucher code (3-15 letters or digits): ").toUpperCase();
            if (code.matches("[A-Z0-9]{3,15}")) {
                break;
            }
            showError("Code must be 3-15 letters or digits, no spaces or symbols.");
        }
 
        String description = readString("Description: ");
 
        System.out.println("Discount type:");
        System.out.println("1. Percent (e.g. 10 = 10% off)");
        System.out.println("2. Fixed amount off");
        String mode = (readInt("Choose a type: ", 1, 2) == 1) ? "PERCENT" : "FIXED";
 
        double value;
        while (true) {
            value = readDouble("Discount value: ");
            if (mode.equals("PERCENT") && value > 100) {
                showError("A percent discount cannot be more than 100.");
                continue;
            }
            break;
        }
 
        LocalDate expiry;
        while (true) {
            expiry = readDate("Expiry date (yyyy-MM-dd): ");
            if (!expiry.isBefore(LocalDate.now())) {
                break;
            }
            showError("The expiry date cannot be in the past.");
        }
 
        int maxUses = readInt("Maximum number of uses (1-100000): ", 1, 100000);
 
        Voucher voucher = new Voucher(code);
        voucher.setDescription(description);
        // voucher.setDiscountMode(mode);   // Voucher has no setDiscountMode yet
        voucher.setValue(value);
        voucher.setExpiryDate(expiry);
        voucher.setMaxUses(maxUses);
        voucher.setTimesUsed(0);
        voucher.setActive(true);
        return voucher;
    }
 
    public String promptVoucherCode() {
        return readString("Enter voucher code: ").toUpperCase();
    }
 
    // Shows the voucher and asks which field to change. 0 = cancel.
    // 1 = description, 2 = value, 3 = expiry date, 4 = maximum uses.
    public int showEditVoucherMenu(Voucher voucher) {
        displayVoucher(voucher);
        System.out.println("\nWhat do you want to edit?");
        System.out.println("1. Description");
        System.out.println("2. Discount value");
        System.out.println("3. Expiry date");
        System.out.println("4. Maximum uses");
        System.out.println("0. Cancel");
        return readInt("Choose an option: ", 0, 4);}
 
 
    // Display Methods
 
    public void displayVoucher(Voucher voucher) {
        System.out.println("\n===== VOUCHER " + voucher.getVoucherCode() + " =====");
        System.out.println("Description : " + voucher.getDescription());
        System.out.println("Discount    : " + voucher.getDiscountMode() + " " + voucher.getValue());
        System.out.println("Expires     : " + voucher.getExpiryDate());
        System.out.println("Uses        : " + voucher.getTimesUsed() + " / " + voucher.getMaxUses());
        System.out.println("Status      : " + (voucher.isActive() ? "Active" : "Inactive"));}
 
    public void displayVouchers(LinkedList list) {
        if (list == null || list.getHead() == null) {
            System.out.println("No vouchers found.");
            return;
        }
        String row = "%-16s %-12s %-12s %-9s %-10s %-30s%n";
        System.out.println();
        System.out.printf(row, "Code", "Discount", "Expires", "Uses", "Status", "Description");
        System.out.println("----------------------------------------------------------------------------------------");
        Node n = list.getHead();
        while (n != null) {
            Voucher v = (Voucher) n.getData();
            System.out.printf(row, v.getVoucherCode(), v.getDiscountMode() + " " + v.getValue(), v.getExpiryDate(),
                    v.getTimesUsed() + "/" + v.getMaxUses(), v.isActive() ? "Active" : "Inactive", v.getDescription());
            n = n.getNext();
        }}
 
}
