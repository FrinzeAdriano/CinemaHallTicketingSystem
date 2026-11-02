
package view;
import datastructures.*;
import repositories.*;
import utility.*;
import service.*;
import controllers.*;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.ArrayList;

public abstract class BaseView {

  Scanner scanner = new Scanner(System.in);  
  
  
    // Message Methods
 
    public void showMessage(String msg) {
        System.out.println(msg);
    }
 
    public void showError(String msg) {
        System.out.println(msg);
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
    // ---------- Table printing ----------

    private static final int MAX_CELL_WIDTH = 30;

    // Prints a bordered table. A cell longer than MAX_CELL_WIDTH is wrapped onto extra lines.
    public static void printTable(String[] headers, ArrayList<String[]> rows) {
        printTable(null, headers, rows);
    }

    // Same table with a title banner (=== TITLE ===) that is stretched to the width of the table.
    public static void printTable(String title, String[] headers, ArrayList<String[]> rows) {
        // Step 1: find the width of each column (the longest text in it, header included).
        int[] widths = new int[headers.length];
        for (int c = 0; c < headers.length; c++) {
            int longest = 0;
            for (String[] row : rows) {
                String cell = (row[c] == null) ? "" : row[c];
                longest = Math.max(longest, cell.length());
            }
            widths[c] = Math.max(headers[c].length(), Math.min(longest, MAX_CELL_WIDTH));
        }
        // Step 2: build the border line, for example +-------+------+
        String border = "+";
        for (int c = 0; c < headers.length; c++) {
            border += "-".repeat(widths[c] + 2) + "+";
        }
        // Step 3: print the title, the border, the headers, the border, every row, and the last border.
        if (title != null) {
            System.out.println();
            System.out.println(banner(title, Math.max(border.length(), title.length() + 10)));
        }
        System.out.println(border);
        printRow(headers, widths);
        System.out.println(border);
        for (String[] row : rows) {
            printRow(row, widths);
        }
        System.out.println(border);
    }

    // Prints one table row. Cells are wrapped, so one row can take several lines.
    private static void printRow(String[] cells, int[] widths) {
        ArrayList<ArrayList<String>> wrapped = new ArrayList<>();
        int height = 1;
        for (int c = 0; c < widths.length; c++) {
            String cell = (cells[c] == null) ? "" : cells[c];
            ArrayList<String> lines = wrap(cell, widths[c]);
            wrapped.add(lines);
            height = Math.max(height, lines.size());
        }
        for (int line = 0; line < height; line++) {
            String out = "|";
            for (int c = 0; c < widths.length; c++) {
                String text = (line < wrapped.get(c).size()) ? wrapped.get(c).get(line) : "";
                out += " " + String.format("%-" + widths[c] + "s", text) + " |";
            }
            System.out.println(out);
        }
    }

    // Splits text into lines of at most "width" characters, cutting at a space when it can.
    private static ArrayList<String> wrap(String text, int width) {
        ArrayList<String> lines = new ArrayList<>();
        String rest = text;
        while (rest.length() > width) {
            int cut = rest.lastIndexOf(' ', width);
            if (cut <= 0) cut = width;
            lines.add(rest.substring(0, cut));
            rest = rest.substring(cut).trim();
        }
        lines.add(rest);
        return lines;
    }

    // "===== TITLE =====" stretched to exactly "width" characters.
    private static String banner(String title, int width) {
        String text = " " + title + " ";
        int fill = Math.max(width - text.length(), 2);
        int left = fill / 2;
        return "=".repeat(left) + text + "=".repeat(fill - left);
    }
}

