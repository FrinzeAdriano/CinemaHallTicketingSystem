package controllers;

import java.time.LocalDate;
import java.util.Locale;
import models.*;
import repositories.SalesRepository;
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


        // Repeats the sales menu until the admin chooses to go back
        while (true) {

            int choice = salesView.showSalesMenu();

            switch (choice) {

                // Back
                case 0:
                    return;


                // View all sales
                case 1:

                    salesView.displaySales(
                        salesRepo.viewSales()
                    );

                    break;


                // View sales by date
                case 2: {

                    LocalDate date;


                    // Repeats the same date field until valid
                    while (true) {

                        date = salesView.promptDate();

                        if (date != null) {
                            break;
                        }

                        salesView.showError(
                            "Invalid date. Please enter the date again."
                        );
                    }


                    Sales dailySales =
                        salesRepo.getSalesByDate(date);

                    if (dailySales == null) {

                        salesView.showMessage(
                            "No sales records found for this date."
                        );

                    } else {

                        salesView.displaySalesSummary(dailySales);
                    }

                    break;
                }


                // View sales by date range
                case 3: {

                    LocalDate startDate;
                    LocalDate endDate;


                    // Repeats only the start date field if invalid
                    while (true) {

                        startDate = salesView.promptDate();

                        if (startDate != null) {
                            break;
                        }

                        salesView.showError(
                            "Invalid start date. Please try again."
                        );
                    }


                    // Repeats only the end date field if invalid
                    while (true) {

                        endDate = salesView.promptDate();

                        if (endDate == null) {

                            salesView.showError(
                                "Invalid end date. Please try again."
                            );

                            continue;
                        }


                        if (endDate.isBefore(startDate)) {

                            salesView.showError(
                                "End date cannot be earlier than start date."
                            );

                            continue;
                        }

                        break;
                    }


                    salesView.displaySales(
                        salesRepo.getSalesByRange(
                            startDate, endDate
                        )
                    );

                    break;
                }


                // View total revenue
                case 4: {

                    double totalRevenue =
                        salesRepo.getTotalRevenue();

                    salesView.showMessage(
                        "Total Revenue: "
                        + String.format(
                            Locale.ROOT, "%.2f", totalRevenue
                        )
                    );

                    break;
                }


                default:

                    salesView.showError(
                        "Invalid menu option. Please try again."
                    );
            }
        }
    }
}
