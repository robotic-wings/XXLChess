package XXLChess;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class that represents a player in the chess game. This class implements the Tickable interface,
 * which allows the player's timer to tick (or count down) each second.
 */
public abstract class PlayerAgent implements Tickable {
    protected int timeIncrement;  // The time increment after each move
    private Piece.Color color;  // The color of the player's pieces
    private King king;  // The King piece of the player
    private PlayerAgent opponent;  // The opponent player
    private List<Piece> pieces;  // The list of all pieces that the player has
    private final Timer timer;  // The timer for the player's turns
    private Movement lastMove;  // The last move made by the player

    public PlayerAgent(Timer timer) {
        this.timer = timer;
    }

    public PlayerAgent(Piece.Color color, int remainingTime, int timeIncrement) {
        this.color = color;
        this.pieces = new ArrayList<Piece>();
        this.timer = new Timer(remainingTime);
        this.timeIncrement = timeIncrement;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public King getKing() {
        return king;
    }

    public void setKing(King king) {
        this.king = king;
    }

    public Piece.Color getColor() {
        return color;
    }

    public Tile getLastMoveSource() {
        if (lastMove == null) return null;
        return lastMove.getSourceTile();
    }

    public Tile getLastMoveTarget() {
        if (lastMove == null) return null;
        return lastMove.getTargetTile();
    }

    public void setLastMove(Movement lastMove) {
        this.lastMove = lastMove;
    }

    public int getTimeIncrement() {
        return timeIncrement;
    }

    public PlayerAgent getOpponent() {
        return opponent;
    }

    public void setOpponent(PlayerAgent opponent) {
        this.opponent = opponent;
    }

    /**
     * Increase the remaining time by the time increment.
     */
    public void increaseRemainingTime() {
        timer.addRemainingSecs(timeIncrement);
    }

    /**
     * Decrease the remaining time by 1 second.
     * Throws a TimeoutException if the remaining time is zero.
     */
    public void decreaseRemainingTime() throws TimeoutException {
        if (timer.isEnded()) {
            throw new TimeoutException(this);
        }
        timer.addRemainingSecs(-1);
    }

    /**
     * Get the remaining time for the player.
     * Returns zero if the timer has ended.
     */
    public int getRemainingTime() {
        if (timer.isEnded()) {
            return 0;
        }
        return (int) timer.getRemainingSecs();
    }

    /**
     * Tick the player's timer, decreasing the remaining time by 1 second.
     * Throws an IllegalStateException if the timer has ended.
     */
    public void tick() {
        if (isEnded()) throw new IllegalStateException();
        timer.tick();
    }

    /**
     * Check if the player's time has ended.
     * Returns true if the timer has ended, false otherwise.
     */
    public boolean isEnded() {
        return timer.isEnded();
    }
}
