package controllers;

import models.Voucher;
import repositories.VoucherRepository;
import utility.LoopUtility;
import view.VoucherView;

public class VoucherController {
    private VoucherRepository voucherRepo;
    private VoucherView voucherView;

    public VoucherController(VoucherRepository voucherRepo, VoucherView voucherView) {
        this.voucherRepo = voucherRepo;
        this.voucherView = voucherView;
    }

    public void manageVouchers() {
        int choice = -1;
        while (choice != 0) {
            choice = voucherView.showVoucherMenu();
            try {
                // Everything except "Add voucher" needs at least one voucher.
                // Updated range from 2..5 to 2..3 since choices are now sequential (1, 2, 3)
                if (voucherRepo.viewVouchers().getHead() == null && choice >= 2 && choice <= 3) {
                    throw new IllegalArgumentException("Invalid input. There are no vouchers yet.");
                }
                switch (choice) {
                    case 1: {
                        // Automatically generate voucher code through the repository, just like cinemas
                        Voucher voucher = new Voucher(voucherRepo.generateVoucherCode());

                        if (voucher != null
                                && LoopUtility.retryUntilValid(voucherView::promptDescription, voucher::setDescription, voucherView::showError, "0")
                                && LoopUtility.retryUntilValid(voucherView::promptDiscountMode, voucher::setDiscountMode, voucherView::showError, "0")
                                && LoopUtility.retryUntilValid(voucherView::promptValue, voucher::setValue, voucherView::showError, "0")
                                && LoopUtility.retryUntilValid(voucherView::promptExpiryDate, voucher::setExpiryDate, voucherView::showError, "0")
                                && LoopUtility.retryUntilValid(voucherView::promptMaxUses, voucher::setMaxUses, voucherView::showError, "0")) {
                            voucher.setActive(true);
                            voucherRepo.addVoucher(voucher);
                            voucherView.showMessage("Voucher " + voucher.getVoucherCode() + " was added.");
                        }
                        break;
                    }
                    case 2:
                        voucherView.displayVouchers(voucherRepo.viewVouchers());
                        break;
                    
                    case 3: {
                        // Deactivate voucher (by code, 0 to go back).
                        voucherView.displayVouchers(voucherRepo.viewVouchers());
                        Boolean deactivated = LoopUtility.retryUntilValidResult(voucherView::promptVoucherCode,
                                code -> {
                                    Voucher found = voucherRepo.findByCode(code);
                                    if (found == null) {
                                        throw new IllegalArgumentException("Invalid input. Voucher " + code.trim() + " was not found.");
                                    }
                                    if (!found.isActive()) {
                                        throw new IllegalArgumentException("Invalid input. Voucher " + found.getVoucherCode() + " is already inactive.");
                                    }
                                    return voucherView.confirm("Deactivate voucher " + found.getVoucherCode() + "?")
                                            && voucherRepo.deactivateVoucher(code);
                                }, voucherView::showError, "0");
                        if (deactivated != null) {
                            voucherView.showMessage(deactivated ? "Voucher deactivated." : "Deactivation cancelled.");
                        }
                        break;
                    }
                    default:
                        break;   // 0 exits the loop
                }
            } catch (IllegalArgumentException e) {
                voucherView.showError(e.getMessage());
            }
        }
    }
}