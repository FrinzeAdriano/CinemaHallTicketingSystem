
package utility;
import datastructures.*;
import repositories.*;
import view.*;
import controllers.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import models.*;

public class InputValidator {


    // Validation Methods

    public static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean isValidUsername(String s) {
        return s != null && s.matches("[a-zA-Z0-9]{4,20}");
    }

    public static boolean isValidPassword(String s) {
        return s != null && s.length() >= 6;
    }

    public static boolean isPositiveInt(String s) {

        if (s == null || s.trim().isEmpty()) {
            return false;
        }

        try {
            return Integer.parseInt(s.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String s) {

        if (s == null || s.trim().isEmpty()) {
            return false;
        }

        try {
            double value = Double.parseDouble(s.trim());

            return Double.isFinite(value) && value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidSeatID(String s) {
        return s != null && s.matches("[a-zA-Z][0-9]+");
    }

    public static boolean isValidDate(String s) {

        if (s == null || s.trim().isEmpty()) {
            return false;
        }

        try {
            LocalDate.parse(s.trim());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean inRange(int n, int min, int max) {
        return n >= min && n <= max;
    }

}
