package XXLChess;

/**
 * This class represents an exception due to a violation of chess rules by a player.
 */
public class RuleViolationException extends Exception {
    private final PlayerAgent violator;

    /**
     * Creates a new RuleViolationException.
     *
     * @param message  The error message.
     * @param violator The player who violated the rule.
     */
    RuleViolationException(String message, PlayerAgent violator) {
        super(message);
        this.violator = violator;
    }

    /**
     * Gets the player who violated the rule.
     *
     * @return The player who violated the rule.
     */
    public PlayerAgent getViolator() {
        return violator;
    }
}

/**
 * This class represents an exception due to an invalid move by a player.
 */
class InvalidMoveException extends RuleViolationException {
    InvalidMoveException(PlayerAgent violator) {
        super("Invalid piece movement.", violator);
    }
}

/**
 * This class represents an exception due to a player leaving their King in danger.
 */
class KingInDangerException extends RuleViolationException {
    public KingInDangerException(PlayerAgent violator) {
        super("The king is in danger and must be protected!", violator);
    }
}

/**
 * This class represents an exception due to a player's time expiring.
 */
class TimeoutException extends RuleViolationException {
    TimeoutException(PlayerAgent violator) {
        super("time expired", violator);
    }
}

/**
 * This class represents an exception due to a player attempting to kill the opponent's King.
 */
class KingDignityException extends RuleViolationException {
    public KingDignityException(PlayerAgent violator) {
        super("You must not actually kill the opponent's king!", violator);
    }
}
