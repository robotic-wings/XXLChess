package XXLChess;

/**
 * This class represents a report of a finished game.
 * It records the winner, the loser, and the reason why the game ended.
 */
public class GameReport {
    private final PlayerAgent winner;  // null if there is a draw
    private final PlayerAgent loser;   // null if there is a draw
    private final EndReason reasonForEnd;

    /**
     * Creates a new GameReport.
     *
     * @param ctx          the context of the game.
     * @param reasonForEnd the reason why the game ended.
     */
    public GameReport(Game ctx, EndReason reasonForEnd) {
        this.reasonForEnd = reasonForEnd;
        if (reasonForEnd == EndReason.DRAW) {
            winner = null;
            loser = null;
        } else if (reasonForEnd == EndReason.COMPUTER_TIMEOUT
                || reasonForEnd == EndReason.COMPUTER_CHECKMATED) {
            winner = ctx.getHumanAgent();
            loser = ctx.getBotAgent();
        } else {
            winner = ctx.getBotAgent();
            loser = ctx.getHumanAgent();
        }
        if (winner == ctx.getHumanAgent()) {
            SoundPlayer.playSound("win.wav");
        } else {
            SoundPlayer.playSound("lose.wav");
        }
    }

    /**
     * Returns the winner of the game.
     *
     * @return the winner of the game. If the game ended in a draw, this is null.
     */
    public PlayerAgent getWinner() {
        return winner;
    }

    /**
     * Returns the loser of the game.
     *
     * @return the loser of the game. If the game ended in a draw, this is null.
     */
    public PlayerAgent getLoser() {
        return loser;
    }

    /**
     * Returns the reason why the game ended.
     *
     * @return the reason why the game ended.
     */
    public EndReason getReasonForEnd() {
        return reasonForEnd;
    }

    /**
     * The reason why the game ended.
     */
    public enum EndReason {
        COMPUTER_TIMEOUT,
        COMPUTER_CHECKMATED,
        DRAW,
        PLAYER_TIMEOUT,
        PLAYER_RESIGNED,
        PLAYER_CHECKMATED
    }
}
