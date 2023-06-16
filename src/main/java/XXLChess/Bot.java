package XXLChess;

import XXLChess.Piece.Color;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a computer player (bot) in the chess game.
 * It extends the abstract PlayerAgent class and provides a method for making a decision on the next move.
 */
public class Bot extends PlayerAgent {
    private BotAIStrategy strategy;

    /**
     * Constructor for the Bot class.
     *
     * @param color         The color of the bot's pieces.
     * @param remainingTime The initial remaining time for the bot's turns.
     * @param timeIncrement The time increment after each move.
     */
    public Bot(Color color, int remainingTime, int timeIncrement) {
        super(color, remainingTime, timeIncrement);
        strategy = new RandomSelectionStrategy();
    }

    /**
     * Make a decision on the next move.
     * The strategy is to choose a random move from the set of legal moves that do not lead to a threat against the king.
     * If no such moves exist, return null.
     *
     * @param ctx The current game context.
     * @return The selected move, or null if no safe moves are possible.
     */
    public Movement makeDecision(Game ctx) {
        Set<Movement> moves = ctx.getAllLegalMovements(this);  // Get all legal moves
        Set<Movement> notBadMoves = new HashSet<>();  // Store moves that don't lead to a threat against the king

        // Iterate through all moves
        for (Movement m : moves) {
            // If the move doesn't lead to a threat against the king, add it to notBadMoves
            if (ctx.predictThreats(getOpponent(), m, getKing()).size() == 0) {
                notBadMoves.add(m);
            }
        }
        return strategy.choose(notBadMoves);
    }

    public BotAIStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(BotAIStrategy strategy) {
        this.strategy = strategy;
    }

}

