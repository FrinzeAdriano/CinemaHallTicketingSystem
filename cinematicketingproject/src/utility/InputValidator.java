
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

    // 4 to 20 letters or digits, no spaces.
    public static boolean isValidUsername(String s) {
        return s != null && s.matches("[a-zA-Z0-9]{4,20}");
    }

    // At least 6 characters and not just whitespace.
    public static boolean isValidPassword(String s) {
        return isNotBlank(s) && s.length() >= 6;
    }

    // Whole number from 1 to 999,999,999 (digits only).
    public static boolean isPositiveInt(String s) {

        if (s == null || !s.trim().matches("\\d{1,9}")) {
            return false;
        }

        return Integer.parseInt(s.trim()) > 0;
    }

    // Plain decimal digits only: rejects "10d", "5f", "1e3", "0x1p3", "NaN"
    public static boolean isPositiveDouble(String s) {

        if (s == null || !s.trim().matches("\\d+(\\.\\d+)?")) {
            return false;
        }

        double value = Double.parseDouble(s.trim());

        return Double.isFinite(value) && value > 0;
    }

    // Letter followed by a seat number from 1 to 999 (e.g. A1, B12). Rejects A0 and A01.
    public static boolean isValidSeatID(String s) {
        return s != null && s.trim().matches("[a-zA-Z][1-9][0-9]{0,2}");
    }

    // Strict yyyy-MM-dd that is also a real calendar date (rejects 2026-02-30, 2026-1-5, +10000-01-01).
    public static boolean isValidDate(String s) {

        if (s == null || !s.trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
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
