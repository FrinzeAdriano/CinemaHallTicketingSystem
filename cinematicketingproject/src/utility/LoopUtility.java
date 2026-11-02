package utility;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class LoopUtility {

  

    // Same as above, with a back option.
    // cancelInput : the input that means "go back" (e.g. "0")
    // Returns true when the setter accepted the input, false when the user typed cancelInput.
    public static boolean retryUntilValid(Supplier<String> prompt,
                                          Consumer<String> setter,
                                          Consumer<String> errorDisplay,
                                          String cancelInput) {
        while (true) {
            String input = prompt.get();
            if (input.trim().equals(cancelInput)) {
                return false;
            }
            try {
                setter.accept(input);
                return true;
            } catch (IllegalArgumentException e) {
                errorDisplay.accept(e.getMessage());
            }
        }
    }

    // Same idea for behavior methods that return a value (e.g. cinemaRepo::findCinema).
    // action      : the behavior method; it throws IllegalArgumentException for bad input
    // cancelInput : the input that means "go back"; pass null for no back option
    // Returns the action's result, or null when the user typed cancelInput.
    public static <T> T retryUntilValidResult(Supplier<String> prompt,
                                              Function<String, T> action,
                                              Consumer<String> errorDisplay,
                                              String cancelInput) {
        while (true) {
            String input = prompt.get();
            if (cancelInput != null && input.trim().equals(cancelInput)) {
                return null;
            }
            try {
                return action.apply(input);
            } catch (IllegalArgumentException e) {
                errorDisplay.accept(e.getMessage());
            }
        }
    }
}
