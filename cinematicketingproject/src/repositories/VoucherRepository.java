package repositories;

import datastructures.LinkedList;
import datastructures.Node;
import models.Voucher;
import utility.InputValidator;

public class VoucherRepository {

    private static final String ID_PREFIX = "VCH";
    private LinkedList<Voucher> vouchers = new LinkedList<Voucher>();

    // Voucher Management Methods

    // Fails on null or if the code already exists.
    public boolean addVoucher(Voucher v) {
        if (v == null || findByCode(v.getVoucherCode()) != null) {
            return false;
        }
        vouchers.insertAtEnd(v);
        return true;
    }

    public LinkedList<Voucher> viewVouchers() {
        return vouchers;
    }

    // Codes are case-insensitive. Returns null when there is no such voucher.
    public Voucher findByCode(String code) {
        if (code == null) {
            return null;
        }
        Node<Voucher> n = vouchers.getHead();
        while (n != null) {
            if (n.getData().getVoucherCode().equalsIgnoreCase(code.trim())) {
                return n.getData();
            }
            n = n.getNext();
        }
        return null;
    }

    // Returns true only if the voucher exists and was active.
    public boolean deactivateVoucher(String code) {
        Voucher v = findByCode(code);
        if (v == null || !v.isActive()) {
            return false;
        }
        v.setActive(false);
        return true;
    }

    // Generates the next voucher code in the format VCH001, VCH002, ... (highest existing number + 1).
    // An empty list is valid and gives VCH001.
    public String generateVoucherCode() {
        int max = 0;
        Node<Voucher> node = vouchers.getHead();
        while (node != null) {
            String id = node.getData().getVoucherCode();
            if (id.startsWith(ID_PREFIX) && InputValidator.isPositiveInt(id.substring(ID_PREFIX.length()))) {
                int number = Integer.parseInt(id.substring(ID_PREFIX.length()));
                if (number > max) {
                    max = number;
                }
            }
            node = node.getNext();
        }
        return String.format("%s%03d", ID_PREFIX, max + 1);
    }
}