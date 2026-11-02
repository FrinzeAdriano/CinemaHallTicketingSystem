package controllers;

import repositories.VoucherRepository;
import view.VoucherView;

public class VoucherController {
    private VoucherRepository voucherRepo;
    private VoucherView voucherView;

    public VoucherController(VoucherRepository voucherRepo, VoucherView voucherView) {
        this.voucherRepo = voucherRepo;
        this.voucherView = voucherView;
    }

    public void manageVouchers() {
        
    }
}
