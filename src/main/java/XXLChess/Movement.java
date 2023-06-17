package XXLChess;

/**
 * This class represents a move in the chess game.
 * It encapsulates the source piece and the target tile.
 */
public class Movement {
    private final Piece source;  // The piece that is being moved.
    private final Tile sourceTile;  // The tile where the source piece is located.
    private final Tile target;  // The target tile to which the source piece is moving.

    /**
     * Constructor for the Movement class.
     *
     * @param source The piece that is being moved.
     * @param target The target tile to which the source piece is moving.
     */
    public Movement(Piece source, Tile target) {
        this.source = source;
        this.sourceTile = this.source.getCurrentTile();
        this.target = target;
    }

    /**
     * Get the source piece of the move.
     *
     * @return The source piece of the move.
     */
    public Piece getSourcePiece() {
        return this.source;
    }

    /**
     * Get the source tile of the move.
     *
     * @return The source tile of the move.
     */
    public Tile getSourceTile() {
        return sourceTile;
    }

    /**
     * Get the piece located at the target tile.
     *
     * @return The piece at the target tile, or null if the target tile is empty.
     */
    public Piece getTargetPiece() {
        return this.target.getCurrentPiece();
    }

    /**
     * Get the target tile of the move.
     *
     * @return The target tile of the move.
     */
    public Tile getTargetTile() {
        return this.target;
    }

    /**
     * Get the x-coordinate of the source tile.
     *
     * @return The x-coordinate of the source tile.
     */
    public int getSourceX() {
        return this.source.getX();
    }

    /**
     * Get the y-coordinate of the source tile.
     *
     * @return The y-coordinate of the source tile.
     */
    public int getSourceY() {
        return this.source.getY();
    }

    /**
     * Get the x-coordinate of the target tile.
     *
     * @return The x-coordinate of the target tile.
     */
    public int getTargetX() {
        return this.target.getX();
    }

    /**
     * Get the y-coordinate of the target tile.
     *
     * @return The y-coordinate of the target tile.
     */
    public int getTargetY() {
        return this.target.getY();
    }

    /**
     * Get the left position of the target tile.
     *
     * @return The left position of the target tile.
     */
    public int getTargetLeft() {
        return target.getLeft();
    }

    /**
     * Get the top position of the target tile.
     *
     * @return The top position of the target tile.
     */
    public int getTargetTop() {
        return target.getTop();
    }

    /**
     * Get the left position of the source tile.
     *
     * @return The left position of the source tile.
     */
    public int getSourceLeft() {
        return source.getLeft();
    }

    /**
     * Get the top position of the source tile.
     *
     * @return The top position of the source tile.
     */
    public int getSourceTop() {
        return source.getTop();
    }

    public Piece perform() {
        Piece checkedPiece = target.getCurrentPiece();
        getSourceTile().setCurrentPiece(null);
        target.setCurrentPiece(source);
        source.setCurrentTile(target);
        source.setHasMovedBefore(true);
        return checkedPiece;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Movement)) {
            return false;
        }
        Movement m = (Movement) obj;
        return this.source == m.source
        && this.target == m.target
        && this.sourceTile == m.sourceTile;
    }
}

/**
 * if (p instanceof King) {
                allMoves.addAll(((King) p).getPotentialCastlingMovements(game));
            }
 */