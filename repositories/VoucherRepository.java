package repositories;

import datastructures.LinkedList;
import datastructures.Node;
import models.Voucher;
import java.security.SecureRandom;

public class VoucherRepository {

    private static final String ID_PREFIX = "VCH";
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int SUFFIX_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();
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

    // Generates a random, unique voucher code: VCH + 6 characters (e.g. VCHK7QX4M).
    // Ambiguous characters (0, O, 1, I) are left out so codes are easy to type.
    // Retries until the code is not already used. Always valid for the Voucher constructor (3-15 letters/digits).
    public String generateVoucherCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(ID_PREFIX);
            for (int i = 0; i < SUFFIX_LENGTH; i++) {
                sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (findByCode(code) != null);
        return code;
    }
}