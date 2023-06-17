package XXLChess;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class Utils {

    /**
     * This method formats seconds into a string in the format "m:ss" where m is minutes and ss is seconds.
     *
     * @param seconds the number of seconds to format.
     * @return a string representation of the time in "m:ss" format.
     */
    public static String formatSeconds(int seconds) {
        if (seconds < 0) {
            throw new IllegalArgumentException("Seconds must be non-negative");
        }
        // Convert seconds into minutes and remaining seconds
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        // Format the time into "m:ss" format
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    /**
     * This method selects a random element from a set.
     *
     * @param set the set to select from.
     * @return the randomly selected element.
     */
    public static <T> T selectRandomElement(Set<T> set) {
        if (set.isEmpty()) {
            throw new IllegalArgumentException("Set is empty");
        }
        // Select a random index
        int randomIndex = new Random().nextInt(set.size());
        // Iterate over the set to the selected index
        Iterator<T> iterator = set.iterator();
        for (int i = 0; i < randomIndex; i++) {
            iterator.next();
        }
        // Return the element at the selected index
        return iterator.next();
    }
}
