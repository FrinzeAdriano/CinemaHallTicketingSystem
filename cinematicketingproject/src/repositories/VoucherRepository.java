
package repositories;

import controllers.*;
import models.*;
import datastructures.*;
import java.time.LocalDate;
import view.*;
import utility.*;
import service.*;

public class VoucherRepository {

    private LinkedList vouchers = new LinkedList();


    // Voucher Management Methods

    // Fails on null or if the code already exists.
    public boolean addVoucher(Voucher v) {
        if (v == null || findByCode(v.getVoucherCode()) != null) {
            return false;
        }
        vouchers.insertAtEnd(v);
        return true;
    }

    public LinkedList viewVouchers() {
        return vouchers;}

    // Codes are case-insensitive.
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

    public boolean removeVoucher(String code) {
        Voucher v = findByCode(code);
        if (v == null) {
            return false;
        }
        return vouchers.remove(v);
    }

}
