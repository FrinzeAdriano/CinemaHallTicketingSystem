package utility;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LoopUtility {

    // Keeps asking until the setter accepts the input.
    // prompt       : asks the view for raw input (e.g. movieView::promptDuration)
    // setter       : the model setter that validates and stores it (e.g. movie::setDuration)
    // errorDisplay : shows the setter's error message (e.g. movieView::showError)
    public static void retryUntilValid(Supplier<String> prompt,
                                       Consumer<String> setter,
                                       Consumer<String> errorDisplay) {
        boolean valid = false;
        while (!valid) {
            String input = prompt.get();
            try {
                setter.accept(input);
                valid = true;
            } catch (IllegalArgumentException e) {
                errorDisplay.accept(e.getMessage());
            }
        }
    }

}
