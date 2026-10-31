
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

    public boolean addVoucher(Voucher v) {
         return false;}

    public LinkedList viewVouchers() {
         return null;}

    public Voucher findByCode(String code) {
         return null;}

    public boolean deactivateVoucher(String code) {
         return false;}

    public boolean removeVoucher(String code) {
         return false;}

}
