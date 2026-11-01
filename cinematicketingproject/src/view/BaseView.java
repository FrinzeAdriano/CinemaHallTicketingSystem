
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
        System.out.println(msg);
    }
 
    public void showError(String msg) {
        System.out.println("[Error] " + msg);
    }
 
    public boolean confirm(String prompt) {
        while (true) {
            String input = readString(prompt + " (y/n): ");
            if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) {
                return true;
            }
            if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) {
                return false;
            }
            showError("Please answer y or n.");
        }
    }
 
 
    // Input Methods
 
    // Keeps asking until the user types something that is not blank.
    public String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (InputValidator.isNotBlank(input)) {
                return input;
            }
            showError("This field cannot be empty.");
        }
    }
 
    public int readInt(String prompt, int min, int max) {
        while (true) {
            String input = readString(prompt);
            try {
                int value = Integer.parseInt(input);
                if (InputValidator.inRange(value, min, max)) {
                    return value;
                }
                showError("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                showError("Invalid input. Please enter a whole number.");
            }
        }
    }
 
    // Keeps asking until a positive number is entered.
    public double readDouble(String prompt) {
        while (true) {
            String input = readString(prompt);
            if (InputValidator.isPositiveDouble(input)) {
                return Double.parseDouble(input);
            }
            showError("Invalid input. Please enter a number greater than 0.");
        }
    }
 
    // Keeps asking until a date in yyyy-MM-dd format is entered.
    public LocalDate readDate(String prompt) {
        while (true) {
            String input = readString(prompt);
            if (InputValidator.isValidDate(input)) {
                return LocalDate.parse(input);
            }
            showError("Invalid date. Use the format yyyy-MM-dd (e.g. 2026-12-25).");
        }
    }
}

