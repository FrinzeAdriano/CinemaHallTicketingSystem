package controllers;

import java.time.LocalDate;
import java.util.Locale;
import models.*;
import repositories.SalesRepository;
import utility.InputValidator;
import utility.LoopUtility;
import view.SalesView;

public class SalesReportController {
    private SalesRepository salesRepo;
    private SalesView salesView;

    public SalesReportController(SalesRepository salesRepo, SalesView salesView) {
        this.salesRepo = salesRepo;
        this.salesView = salesView;
    }

    public void viewSalesReports() {

        if (salesRepo == null || salesView == null) {
            return;
        }

        int choice = -1;
        while (choice != 0) {
            choice = salesView.showSalesMenu();

            // Every option needs at least one sales record.
            if (choice >= 1 && choice <= 4 && salesRepo.viewSales().size() == 0) {
                salesView.showError("Invalid input. There are no sales records yet.");
                continue;
            }

            switch (choice) {
                case 1:
                    salesView.displaySales(salesRepo.viewSales());
                    break;

                case 2:
                    LoopUtility.retryUntilValidResult(
                        () -> salesView.promptDateText("Date"),
                        input -> {
                            LocalDate date = parseDate(input, "Date");
                            Sales dailySales = salesRepo.getSalesByDate(date);
                            if (dailySales == null) {
                                throw new IllegalArgumentException(
                                    "Invalid input. No sales records found for " + date + ".");
                            }
                            salesView.displaySalesSummary(dailySales);
                            return true;
                        }, salesView::showError, "0");
                    break;

                case 3: {
                    LocalDate startDate = LoopUtility.retryUntilValidResult(
                        () -> salesView.promptDateText("Start date"),
                        input -> parseDate(input, "Start date"),
                        salesView::showError, "0");

                    if (startDate == null) {
                        break;
                    }

                    LocalDate endDate = LoopUtility.retryUntilValidResult(
                        () -> salesView.promptDateText("End date"),
                        input -> {
                            LocalDate parsed = parseDate(input, "End date");
                            if (parsed.isBefore(startDate)) {
                                throw new IllegalArgumentException(
                                    "Invalid input. End date cannot be earlier than start date.");
                            }
                            return parsed;
                        }, salesView::showError, "0");

                    if (endDate != null) {
                        salesView.displaySales(salesRepo.getSalesByRange(startDate, endDate));
                    }
                    break;
                }

                case 4:
                    salesView.showMessage("Total Revenue: "
                        + String.format(Locale.ROOT, "%.2f", salesRepo.getTotalRevenue()));
                    break;

                default:
                    break;   // 0 exits the loop
            }
        }
    }

    // Throws IllegalArgumentException when the text is not a real yyyy-MM-dd date.
    private LocalDate parseDate(String input, String label) {
        if (!InputValidator.isValidDate(input)) {
            throw new IllegalArgumentException(
                "Invalid input. " + label + " must be a real date as yyyy-MM-dd.");
        }
        return LocalDate.parse(input.trim());
    }
}