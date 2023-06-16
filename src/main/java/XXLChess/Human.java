package XXLChess;

import XXLChess.Piece.Color;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a human player in the chess game.
 * It extends the abstract PlayerAgent class and provides methods for selecting a piece and getting its available moves.
 */
public class Human extends PlayerAgent {
    private Tile selection;  // The tile currently selected by the player

    /**
     * Constructor for the Human class.
     *
     * @param color         The color of the player's pieces.
     * @param remainingTime The initial remaining time for the player's turns.
     * @param timeIncrement The time increment after each move.
     */
    public Human(Color color, int remainingTime, int timeIncrement) {
        super(color, remainingTime, timeIncrement);
    }

    /**
     * Get the currently selected tile.
     *
     * @return The currently selected tile, or null if no tile is selected.
     */
    public Tile getSelection() {
        return selection;
    }

    /**
     * Select a tile.
     *
     * @param source The tile to be selected.
     */
    public void select(Tile source) {
        this.selection = source;
    }

    /**
     * Check if a move is pending.
     *
     * @return true if no tile is currently selected, false otherwise.
     */
    public boolean isPending() {
        return this.selection == null;
    }

    /**
     * Clear the current tile selection.
     */
    public void clearSelection() {
        this.selection = null;
    }

    /**
     * Get the all target tiles for the currently selected piece, without considering the safety of the king.
     *
     * @return A set of tiles that the selected piece can move to, or null if no piece is selected.
     */
    public Set<Tile> getAllTargetTiles() {
        if (selection == null) return null;
        Piece piece = selection.getCurrentPiece();
        if (piece == null) return null;
        return piece.getPossibleTargets();
    }

    /**
     * Get the all target movements for the currently selected piece, without considering the safety of the king.
     *
     * @return A set of movements that the selected piece can do, or null if no piece is selected.
     */
    public Set<Movement> getAllMovements() {
        Set<Tile> tiles = getAllTargetTiles();
        if (tiles == null) return null;
        Set<Movement> allMoves = new HashSet<>();
        for (Tile tile : tiles) {
            Piece p = selection.getCurrentPiece();
            allMoves.add(new Movement(p, tile));
        }
        return allMoves;
    }


}
