
package view;
import datastructures.*;
import repositories.*;
import utility.*;
import service.*;
import controllers.*;
import java.time.LocalDate;
import java.util.Scanner;


public abstract class BaseView {

  Scanner scanner = new Scanner(System.in);  
  
  
    // Message Methods

    public void showMessage(String msg) {

        }

    public void showError(String msg) {

        }

    public boolean confirm(String prompt) {
         return false;}


    // Input Methods

    public String readString(String prompt) {
         return " ";}

    public int readInt(String prompt, int min, int max) {
         return 0;}

    public double readDouble(String prompt) {
         return 0;}

    public LocalDate readDate(String prompt) {
         return null;}

}

