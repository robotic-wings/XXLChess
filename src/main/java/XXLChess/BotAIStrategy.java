package XXLChess;

import java.util.Set;

public interface BotAIStrategy {
    Movement choose(Set<Movement> availableMovements);
}

class RandomSelectionStrategy implements BotAIStrategy {
    public Movement choose(Set<Movement> availableMovements) {
        // If there are any safe moves, choose one randomly
        if (!availableMovements.isEmpty()) {
            return Utils.selectRandomElement(availableMovements);
        } else {
            // If no safe moves are possible, return null
            return null;
        }
    }
}