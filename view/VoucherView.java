package view;

import datastructures.LinkedList;
import datastructures.Node;
import models.Voucher;
import java.util.ArrayList;

public class VoucherView extends BaseView {

    // Constructor (no ID needed, inherits the scanner from BaseView)
    public VoucherView() {
        super();
    }

    // Menus and prompts

    public int showVoucherMenu() {
        System.out.println();
        System.out.println("========== VOUCHER MANAGEMENT ==========");
        System.out.println("[1] Add voucher");
        System.out.println("[2] View all vouchers");
        System.out.println("[3] Deactivate voucher");
        System.out.println("[0] Back");
        System.out.println("----------------------------------------");
        return readInt("Choose an option (0 to go back): ", 0, 3);
    }

    public String promptVoucherCode() {
        return readString("Voucher code, 3-15 letters or digits (0 to go back): ").toUpperCase();
    }

    public String promptDescription() {
        return readString("Description (0 to go back): ");
    }

    public String promptDiscountMode() {
      System.out.println();
      System.out.println("============= VOUCHER TYPE =============");
      System.out.println("[1] Percent");
      System.out.println("[2] Fixed");
      System.out.println("[0] Back");
      System.out.println("----------------------------------------");
      switch (readInt("Choose a voucher type: ", 0, 2)) {
          case 1:
              return "PERCENT";
          case 2:
              return "FIXED";
          default:
              return "0";   // LoopUtility treats "0" as go back
      }
  }

    public String promptValue(String discountMode) {
        if (discountMode.equals("PERCENT")) {
            return readString("Discount percent, 1-100 (0 to go back): ");
        }
        return readString("Discount amount in Pesos - \"100.00 Php\" (0 to go back): ");
    }

    public String promptExpiryDate() {
        return readString("Expiry date, yyyy-MM-dd (0 to go back): ");
    }

    public String promptMaxUses() {
        return readString("Maximum number of uses, 1-100000 (0 to go back): ");
    }

    // Display methods

    public void displayVoucher(Voucher voucher) {
        System.out.println();
        System.out.println("----- VOUCHER DETAILS -----");
        System.out.println("Code        : " + voucher.getVoucherCode());
        System.out.println("Description : " + voucher.getDescription());
        System.out.println("Discount    : " + (voucher.getDiscountMode().equals("PERCENT")
            ? String.format("%.2f%%", voucher.getValue()) : String.format("%.2f Php", voucher.getValue())));
        System.out.println("Expires     : " + voucher.getExpiryDate());
        System.out.println("Uses        : " + voucher.getTimesUsed() + " / " + voucher.getMaxUses());
        System.out.println("Status      : " + (voucher.isActive() ? "Active" : "Inactive"));
    }

    public void displayVouchers(LinkedList<Voucher> list) {
        ArrayList<String[]> rows = new ArrayList<>();
        Node<Voucher> node = list.getHead();
        while (node != null) {
            Voucher v = node.getData();
            rows.add(new String[] {v.getVoucherCode(), v.getDescription(),
                    v.getDiscountMode().equals("PERCENT")
                            ? String.format("%.2f%%", v.getValue()) : String.format("%.2f Php off", v.getValue()),
                    v.getExpiryDate().toString(), v.getTimesUsed() + "/" + v.getMaxUses(),
                    v.isActive() ? "Active" : "Inactive"});
            node = node.getNext();
        }
        printTable("ALL VOUCHERS", new String[] {"Code", "Description", "Discount", "Expires", "Uses", "Status"}, rows);
    }
}