
package utility;
import java.util.function.Consumer;
import java.util.function.Supplier;
public class LoopUtility {


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
