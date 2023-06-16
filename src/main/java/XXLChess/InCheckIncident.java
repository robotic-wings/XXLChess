package XXLChess;

/**
 * This class represents an incident where a King piece is under threat (in check) in a game of chess.
 */
public class InCheckIncident {
    private final King threatenedKing;

    /**
     * Creates a new InCheckIncident.
     *
     * @param threatenedKing The King that is under threat.
     */
    public InCheckIncident(King threatenedKing) {
        this.threatenedKing = threatenedKing;
    }

    /**
     * Gets the King that is under threat.
     *
     * @return The King that is under threat.
     */
    public King getThreatenedKing() {
        return threatenedKing;
    }
}
