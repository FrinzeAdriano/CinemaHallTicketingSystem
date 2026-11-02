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
                if (voucherRepo.viewVouchers().getHead() == null && choice >= 2 && choice <= 5) {
                    throw new IllegalArgumentException("Invalid input. There are no vouchers yet.");
                }
                switch (choice) {
                    case 1: {
                        // Add voucher (the code is checked first; 0 at any prompt goes back without adding).
                        // The discount type is asked before the value so a PERCENT value can be checked.
                        Voucher voucher = LoopUtility.retryUntilValidResult(voucherView::promptVoucherCode,
                                code -> {
                                    Voucher created = new Voucher(code);   // throws for a badly formatted code
                                    if (voucherRepo.findByCode(code) != null) {
                                        throw new IllegalArgumentException("Invalid input. Voucher " + created.getVoucherCode() + " already exists.");
                                    }
                                    return created;
                                }, voucherView::showError, "0");
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
                        // Edit voucher (table first, then found by code, 0 to go back)
                        voucherView.displayVouchers(voucherRepo.viewVouchers());
                        Voucher voucher = LoopUtility.retryUntilValidResult(voucherView::promptVoucherCode,
                                code -> {
                                    Voucher found = voucherRepo.findByCode(code);
                                    if (found == null) {
                                        throw new IllegalArgumentException("Invalid input. Voucher " + code.trim() + " was not found.");
                                    }
                                    return found;
                                }, voucherView::showError, "0");
                        int editChoice = (voucher == null) ? 0 : -1;
                        while (editChoice != 0) {
                            editChoice = voucherView.showEditVoucherMenu(voucher);
                            switch (editChoice) {
                                case 1:
                                    if (LoopUtility.retryUntilValid(voucherView::promptDescription, voucher::setDescription, voucherView::showError, "0")) {
                                        voucherView.showMessage("Description updated.");
                                    }
                                    break;
                                case 2:
                                    if (LoopUtility.retryUntilValid(voucherView::promptDiscountMode, voucher::setDiscountMode, voucherView::showError, "0")) {
                                        voucherView.showMessage("Discount type updated.");
                                    }
                                    break;
                                case 3:
                                    if (LoopUtility.retryUntilValid(voucherView::promptValue, voucher::setValue, voucherView::showError, "0")) {
                                        voucherView.showMessage("Value updated.");
                                    }
                                    break;
                                case 4:
                                    if (LoopUtility.retryUntilValid(voucherView::promptExpiryDate, voucher::setExpiryDate, voucherView::showError, "0")) {
                                        voucherView.showMessage("Expiry date updated.");
                                    }
                                    break;
                                case 5:
                                    if (LoopUtility.retryUntilValid(voucherView::promptMaxUses, voucher::setMaxUses, voucherView::showError, "0")) {
                                        voucherView.showMessage("Maximum uses updated.");
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    }
                    case 4: {
                        // Deactivate voucher (by code, 0 to go back). The code is checked first,
                        // and the confirmation is only asked for an active voucher.
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
                    case 5: {
                        // Remove voucher (by code, 0 to go back). The code is checked first,
                        // and the confirmation is only asked for an existing voucher.
                        voucherView.displayVouchers(voucherRepo.viewVouchers());
                        Boolean removed = LoopUtility.retryUntilValidResult(voucherView::promptVoucherCode,
                                code -> {
                                    Voucher found = voucherRepo.findByCode(code);
                                    if (found == null) {
                                        throw new IllegalArgumentException("Invalid input. Voucher " + code.trim() + " was not found.");
                                    }
                                    return voucherView.confirm("Remove voucher " + found.getVoucherCode() + "?")
                                            && voucherRepo.removeVoucher(code);
                                }, voucherView::showError, "0");
                        if (removed != null) {
                            voucherView.showMessage(removed ? "Voucher removed." : "Removal cancelled.");
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