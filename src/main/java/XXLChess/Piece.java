package XXLChess;

import processing.core.PImage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class Piece implements Cloneable {
    public static HashMap<String, PImage> imageSources = new HashMap<>(); // A map that stores the images of the pieces
    public final char type; // The type of the piece
    protected Set<Tile> possibleTargets; // The set of possible moves for the piece
    // Fields
    private final String pieceName; // The name of the piece
    private Tile currentTile; // The current tile where the piece is located
    private final Color color; // The color of the piece
    private final Double value; // The value of the piece
    private final String imageFileName; // The file name of the image that represents the piece
    private boolean hasMovedBefore; // Indicates if the piece has moved before

    // Constructor for the Piece class
    public Piece(String pieceName, Color color, Double value, char type) {
        this.color = color;
        this.pieceName = pieceName;
        this.value = value;
        this.imageFileName = (color == Color.BLACK ? "b" : "w") + "-" + pieceName + ".png";
        this.type = type;
    }

    /**
     * Creates a new piece based on the provided character. The character represents
     * the type of the piece and its color. Uppercase characters represent black pieces
     * and lowercase characters represent white pieces.
     *
     * @param c the character that represents the piece
     * @return the new piece
     */
    public static Piece createPiece(char c) {
        // Determine the color of the piece based on the case of the character
        Color pieceColor = Character.isUpperCase(c) ? Color.BLACK : Color.WHITE;
        // Convert the character to lowercase to match the switch cases
        c = Character.toLowerCase(c);
        switch (c) {
            case 'p':
                return new Pawn(pieceColor);
            case 'r':
                return new Rook(pieceColor);
            case 'n':
                return new Knight(pieceColor);
            case 'b':
                return new Bishop(pieceColor);
            case 'h':
                return new Archbishop(pieceColor);
            case 'c':
                return new Camel(pieceColor);
            case 'g':
                return new General(pieceColor);
            case 'a':
                return new Amazon(pieceColor);
            case 'k':
                return new King(pieceColor);
            case 'e':
                return new Chancellor(pieceColor);
            case 'q':
                return new Queen(pieceColor);
            default:
                // If the character does not match any piece, return null
                return null;
        }
    }

    /**
     * Gets the possible moves of the piece.
     *
     * @return the set of possible moves
     */
    public Set<Tile> getPossibleTargets() {
        return possibleTargets;
    }

    /**
     * Gets if the piece has moved before.
     *
     * @return true if the piece has moved before, false otherwise
     */
    public boolean getHasMovedBefore() {
        return hasMovedBefore;
    }

    // Sets the flag that indicates if the piece has moved before
    public void setHasMovedBefore(boolean isMoved) {
        this.hasMovedBefore = isMoved;
    }

    // Checks if the piece is captured
    public boolean isCaptured() {
        return currentTile == null;
    }

    // Gets the x-coordinate of the piece
    public int getX() throws IllegalArgumentException {
        if (currentTile == null)
            throw new IllegalArgumentException("This piece was already captured");
        return currentTile.getX();
    }

    // Gets the y-coordinate of the piece
    public int getY() throws IllegalArgumentException {
        if (currentTile == null)
            throw new IllegalArgumentException("This piece was already captured");
        return currentTile.getY();
    }

    // Abstract method for updating possible moves
    public abstract void updatePossibleTargets(Board board) throws IllegalArgumentException;

    // Getter methods
    public String getPieceName() {
        return pieceName;
    }

    public Color getColor() {
        return color;
    }

    public Double getValue() {
        return value;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public int getLeft() throws IllegalArgumentException {
        if (currentTile == null)
            throw new IllegalArgumentException("This piece was already captured");
        return currentTile.getLeft();
    }

    public int getTop() throws IllegalArgumentException {
        if (currentTile == null)
            throw new IllegalArgumentException("This piece was already captured");
        return currentTile.getTop();
    }

    /**
     * Gets the image of the piece.
     *
     * @return the image of the piece
     */
    public PImage getImage() {
        return Piece.imageSources.get(imageFileName);
    }

    // Enum for the color of the piece
    public enum Color {
        BLACK,
        WHITE
    }
}

class Amazon extends Piece {
    /**
     * Constructs an Amazon piece with the given color.
     *
     * @param color the color of the piece
     */
    public Amazon(Color color) {
        super("amazon", color, 12.0, 'a');
    }

    /**
     * Updates the possible moves for this piece on a given board.
     * The Amazon piece can move like a Knight, Bishop, or Rook.
     *
     * @param board the current game board
     * @throws IllegalArgumentException if the current tile is null
     */
    @Override
    public void updatePossibleTargets(Board board) throws IllegalArgumentException {
        Set<Tile> tiles = new HashSet<Tile>();
        tiles.addAll(Knight.getPossibleTargetsFromTile(board, getCurrentTile()));
        tiles.addAll(Bishop.getPossibleTargetsFromTile(board, getCurrentTile()));
        tiles.addAll(Rook.getPossibleTargetsFromTile(board, getCurrentTile()));
        possibleTargets = tiles;
    }
}

class Archbishop extends Piece {
    /**
     * Constructs an Archbishop piece with the given color.
     *
     * @param pieceColor the color of the piece
     */
    public Archbishop(Color pieceColor) {
        super("archbishop", pieceColor, 7.5, 'h');
    }

    /**
     * Updates the possible moves for this piece on a given board.
     * The Archbishop piece can move like a Bishop or Knight.
     *
     * @param board the current game board
     * @throws IllegalArgumentException if the current tile is null
     */
    @Override
    public void updatePossibleTargets(Board board) throws IllegalArgumentException {
        Set<Tile> tiles = new HashSet<Tile>();
        tiles.addAll(Bishop.getPossibleTargetsFromTile(board, getCurrentTile()));
        tiles.addAll(Knight.getPossibleTargetsFromTile(board, getCurrentTile()));
        possibleTargets = tiles;
    }
}

class Bishop extends Piece {
    /**
     * Constructs a Bishop piece with the given color.
     *
     * @param pieceColor the color of the piece
     */
    public Bishop(Color pieceColor) {
        super("bishop", pieceColor, 3.625, 'b');
    }

    /**
     * Gets the possible moves for a Bishop piece from a given tile on a board.
     *
     * @param board  the current game board
     * @param source the tile from which to calculate the possible moves
     * @return the set of possible tiles the Bishop can move to
     * @throws IllegalArgumentException if the source tile is null
     */
    protected static Set<Tile> getPossibleTargetsFromTile(Board board, Tile source) throws IllegalArgumentException {
        Set<Tile> tiles = new HashSet<Tile>();
        tiles.addAll(board.linearMove(source, 1, 1));
        tiles.addAll(board.linearMove(source, 1, -1));
        tiles.addAll(board.linearMove(source, -1, -1));
        tiles.addAll(board.linearMove(source, -1, 1));
        return tiles;
    }

    /**
     * Updates the possible moves for this piece on a given board.
     * The Bishop piece can move diagonally.
     *
     * @param board the current game board
     * @throws IllegalArgumentException if the current tile is null
     */
    @Override
    public void updatePossibleTargets(Board board) throws IllegalArgumentException {
        if (getCurrentTile() == null)
            throw new IllegalArgumentException();
        possibleTargets = getPossibleTargetsFromTile(board, getCurrentTile());
    }
}

class Camel extends Piece {
    /**
     * Constructs a Camel piece with the given color.
     *
     * @param pieceColor the color of the piece
     */
    public Camel(Color pieceColor) {
        super("camel", pieceColor, 2.0, 'c');
    }

    /**
     * Returns the movement directions for the Camel piece.
     *
     * @return an array of coordinate pairs representing the movement directions
     */
    protected static int[][] getMovementDirections() {
        return new int[][]{
                {3, 1},
                {3, -1},
                {-3, 1},
                {-3, -1},
                {1, 3},
                {-1, 3},
                {1, -3},
                {-1, -3}
        };
    }

    /**
     * Updates the possible moves for this piece on a given board.
     * The Camel piece has a unique jumping move pattern.
     *
     * @param board the current game board
     * @throws IllegalArgumentException if the current tile is null
     */
    @Override
    public void updatePossibleTargets(Board board) throws IllegalArgumentException {
        possibleTargets = board.jumpingMove(getCurrentTile(), getMovementDirections());
    }
}

class Chancellor extends Piece {
    /**
     * Constructs a Chancellor piece with the given color.
     *
     * @param pieceColor the color of the piece
     */
    public Chancellor(Color pieceColor) {
        super("chancellor", pieceColor, 8.5, 'e');
    }

    /**
     * Updates the possible moves for this piece on a given board.
     * The Chancellor piece can move like a Knight or Rook.
     *
     * @param board the current game board
     * @throws IllegalArgumentException if the current tile is null
     */
    @Override
    public void updatePossibleTargets(Board board) throws IllegalArgumentException {
        Set<Tile> tiles = new HashSet<Tile>();
        tiles.addAll(Knight.getPossibleTargetsFromTile(board, getCurrentTile()));
        tiles.addAll(Rook.getPossibleTargetsFromTile(board, getCurrentTile()));
        possibleTargets = tiles;
    }
}

class General extends Piece {
    /**
     * Constructs a General piece with the given color.
     *
     * @param pieceColor the color of the piece
     */
    public General(Color pieceColor) {
        super("knight-king", pieceColor, 5.0, 'g');
    }

    /**
     * Updates the possible moves for this piece on a given board.
     * The General piece can move like a Knight or King.
     *
     * @param board the current game board
     * @throws IllegalArgumentException if the current tile is null
     */
    @Override
    public void updatePossibleTargets(Board board) throws IllegalArgumentException {
        Set<Tile> tiles = board.jumpingMove(getCurrentTile(), Knight.getDirections());
        tiles.addAll(board.jumpingMove(getCurrentTile(), King.getDirections()));
        possibleTargets = tiles;
    }
}

class King extends Piece {
    
    /**
     * Constructs a King piece with the given color.
     *
     * @param pieceColor the color of the piece
     */
    public King(Color pieceColor) {
        super("king", pieceColor, Double.POSITIVE_INFINITY, 'k');
    }

    /**
     * Returns the movement directions for the King piece.
     *
     * @return an array of coordinate pairs representing the movement directions
     */
    protected static int[][] getDirections() {
        return new int[][]{
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1},
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };
    }


    /**
     * Returns the possible moves for a King piece from a given source tile.
     *
     * @param board  the current game board
     * @param source the source tile
     * @return a set of possible destination tiles
     * @throws IllegalArgumentException if the source tile is null
     */
    protected static Set<Tile> getPossibleTargetsFromTile(Board board, Tile source) throws IllegalArgumentException {
        return board.jumpingMove(source, getDirections());
    }

    /**
     * Updates the possible moves for this piece on a given board.
     *
     * @param board the current game board
     * @throws IllegalArgumentException if the current tile is null
     */
    @Override
    public void updatePossibleTargets(Board board) throws IllegalArgumentException {
        this.possibleTargets = getPossibleTargetsFromTile(board, getCurrentTile());
    }

    public Set<Movement> getPotentialCastlingMovements(Game ctx) {
        HashSet<Movement> possibleCastlingMovements = new HashSet<>();
        Set<Tile> targets = ctx.getBoard().jumpingMove(getCurrentTile(), 
            new int[][] {
                {-2, 0},
                {2, 0}
            }
        );
        
        for (Tile t : targets) {
            Movement m = new Movement(this,t);
            if (ctx.getCastlingMovement(m) != null) {
                possibleCastlingMovements.add(m);
            }
        }
        return possibleCastlingMovements;
    }
}

class Knight extends Piece {
    /**
     * Constructs a Knight piece with the given color.
     *
     * @param pieceColor the color of the piece
     */
    public Knight(Color pieceColor) {
        super("knight", pieceColor, 2.0, 'n');
    }

    /**
     * Returns the movement directions for the Knight piece.
     *
     * @return an array of coordinate pairs representing the movement directions
     */
    public static int[][] getDirections() {
        return new int[][]{
                {-1, -2},
                {1, -2},
                {-1, 2},
                {1, 2},
                {-2, -1},
                {2, -1},
                {-2, 1},
                {2, 1}
        };
    }

    /**
     * Returns the possible moves for a Knight piece from a given source tile.
     *
     * @param board  the current game board
     * @param source the source tile
     * @return a set of possible destination tiles
     * @throws IllegalArgumentException if the source tile is null
     */
    protected static Set<Tile> getPossibleTargetsFromTile(Board board, Tile source) throws IllegalArgumentException {
        return board.jumpingMove(source, getDirections());
    }

    /**
     * Updates the possible moves for this piece on a given board.
     *
     * @param board the current game board
     * @throws IllegalArgumentException if the current tile is null
     */
    @Override
    public void updatePossibleTargets(Board board) throws IllegalArgumentException {
        possibleTargets = getPossibleTargetsFromTile(board, getCurrentTile());
    }
}

class Pawn extends Piece {
    /**
     * Constructs a Pawn piece with the given color.
     *
     * @param pieceColor the color of the piece
     */
    public Pawn(Color pieceColor) {
        super("pawn", pieceColor, 1.0, 'p');
    }

    /**
     * Returns the attack range for this Pawn on a given board.
     * The Pawn piece can attack diagonally.
     *
     * @param board the current game board
     * @return a set of possible destination tiles
     * @throws IllegalArgumentException if the current tile is null
     */
    public Set<Tile> getAttackRange(Board board) throws IllegalArgumentException {
        int[][] attackDirection;
        PlayerAgent agent = board.getAgentByColor(getColor());
        if (agent instanceof Bot) {
            attackDirection = new int[][]{
                    {-1, 1},
                    {1, 1}
            };
        } else {
            attackDirection = new int[][]{
                    {1, -1},
                    {-1, -1}
            };
        }
        Set<Tile> diagonals = board.jumpingMove(getCurrentTile(), attackDirection);
        Set<Tile> possibleTargets = new HashSet<Tile>();
        for (Tile tile : diagonals) {
            Piece piece = tile.getCurrentPiece();
            if (piece != null
                    && piece.getColor() != getColor()) {
                possibleTargets.add(tile);
            }
        }
        return possibleTargets;
    }

    /**
     * Updates the possible moves for this piece on a given board.
     * The Pawn piece can move forward one square or two squares from its initial position,
     * and can capture diagonally.
     *
     * @param board the current game board
     * @throws IllegalArgumentException if the current tile is null
     */
    @Override
    public void updatePossibleTargets(Board board) throws IllegalArgumentException {
        // Captures diagonally only.
        Set<Tile> possibleTargets = new HashSet<Tile>();
        possibleTargets.addAll(getAttackRange(board));
        // For the purposes of pawn movement, “forward” is considered going up the board for the human player, and going down the board for the computer player
        PlayerAgent agent = board.getAgentByColor(getColor());
        int unitDirection = (agent instanceof Human ? -1 : 1);
        Tile[] tiles = {board.getTile(getX(), getY() + unitDirection), null};
        // A pawn can move two squares forward if it is located on 2nd row from the top or bottom of the board (rank 2 and rank 13), and has not moved before.
        if (!getHasMovedBefore() && (getY() == 1 || getY() == 12)) {
            tiles[1] = board.getTile(getX(), getY() + unitDirection * 2);
        }
        for (Tile tile : tiles) {
            if (tile != null && tile.getCurrentPiece() == null) {
                possibleTargets.add(tile);
            }
        }
        this.possibleTargets = possibleTargets;
    }
}

class Queen extends Piece {
    /**
     * Constructs a Queen piece with the given color.
     *
     * @param pieceColor the color of the piece
     */
    public Queen(Color pieceColor) {
        super("queen", pieceColor, 9.5, 'q');
    }

    /**
     * Updates the possible moves for this piece on a given board.
     * The Queen piece can move along its rank, file, or diagonal.
     *
     * @param board the current game board
     * @throws IllegalArgumentException if the current tile is null
     */
    @Override
    public void updatePossibleTargets(Board board) throws IllegalArgumentException {
        Set<Tile> tiles = new HashSet<Tile>();
        tiles.addAll(Bishop.getPossibleTargetsFromTile(board, getCurrentTile()));
        tiles.addAll(Rook.getPossibleTargetsFromTile(board, getCurrentTile()));
        possibleTargets = tiles;
    }
}

class Rook extends Piece {
    /**
     * Constructs a Rook piece with the given color.
     *
     * @param pieceColor the color of the piece
     */
    public Rook(Color pieceColor) {
        super("rook", pieceColor, 5.25, 'r');
    }

    /**
     * Returns the possible moves for a Rook piece from a given source tile on a given board.
     *
     * @param board  the current game board
     * @param source the tile on which the Rook piece is currently located
     * @return a set of possible destination tiles
     * @throws IllegalArgumentException if the source tile is null
     */
    protected static Set<Tile> getPossibleTargetsFromTile(Board board, Tile source) throws IllegalArgumentException {
        Set<Tile> tiles = new HashSet<Tile>();
        tiles.addAll(board.linearMove(source, 0, 1));
        tiles.addAll(board.linearMove(source, 0, -1));
        tiles.addAll(board.linearMove(source, 1, 0));
        tiles.addAll(board.linearMove(source, -1, 0));
        return tiles;
    }

    /**
     * Updates the possible moves for this piece on a given board.
     * The Rook piece can move any number of squares along its rank or file.
     *
     * @param board the current game board
     * @throws IllegalArgumentException if the current tile is null
     */
    @Override
    public void updatePossibleTargets(Board board) throws IllegalArgumentException {
        if (getCurrentTile() == null)
            throw new IllegalArgumentException();
        possibleTargets = getPossibleTargetsFromTile(board, getCurrentTile());
    }
}
