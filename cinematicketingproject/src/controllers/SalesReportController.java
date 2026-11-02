package controllers;

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
    }
}
