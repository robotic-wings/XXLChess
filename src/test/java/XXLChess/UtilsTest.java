package XXLChess;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.HashSet;

public class UtilsTest {

    @Test
    public void testFormatSeconds() {
        assertEquals("1:40", Utils.formatSeconds(100));
        assertEquals("0:00", Utils.formatSeconds(0));
        assertThrows(IllegalArgumentException.class, () -> Utils.formatSeconds(-1));
    }

    @Test
    public void testSelectRandomElement() {
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        assertTrue(set.contains(Utils.selectRandomElement(set)));
        assertThrows(IllegalArgumentException.class, () -> Utils.selectRandomElement(new HashSet<>()));
    }
}
