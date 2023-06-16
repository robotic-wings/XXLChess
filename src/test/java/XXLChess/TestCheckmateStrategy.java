package XXLChess;

import java.util.Iterator;
import java.util.Set;

public class TestCheckmateStrategy implements BotAIStrategy {
    @Override
    public Movement choose(Set<Movement> availableMovements) {
        Iterator<Movement> iter = availableMovements.iterator();
        if (iter.hasNext())
            return iter.next();
        else
            return null;
    }
    
}