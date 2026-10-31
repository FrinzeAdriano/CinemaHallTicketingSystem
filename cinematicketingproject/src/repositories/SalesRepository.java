package repositories;
import controllers.*;
import models.*;
import datastructures.*;
import java.time.LocalDate;
import view.*;
import utility.*;
import service.*;

public class SalesRepository {

    private LinkedList sales = new LinkedList();


    // Sales Management Methods

    public void recordSale(Transaction t) {

    }

    public void reverseSale(Transaction t) {

    }


    // Sales Retrieval Methods

    public Sales getSalesByDate(LocalDate date) {
         return null;}

    public LinkedList getSalesByRange(LocalDate from, LocalDate to) {
         return null;}

    public LinkedList viewSales() {
         return null;}


    // Computation Method

    public double getTotalRevenue() {
         return 0;}

}