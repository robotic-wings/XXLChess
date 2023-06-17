package XXLChess;

import XXLChess.Piece.Color;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Board represents the game board of the game.
 * It handles the movements of pieces and the interaction between pieces and players.
 */
public class Board {
    // The width of the game board.
    public static final int BOARD_WIDTH = 14;

    // A 2D array representing the chess board, each tile may or may not hold a piece.
    private final Tile[][] board;

    // A map storing the player agents, key is the color of the pieces that the player controls.
    private final HashMap<Piece.Color, PlayerAgent> agents = new HashMap<>();

    /**
     * Constructs a new Board object.
     * Initializes the board with empty tiles, and associates the player agents with their respective colors.
     *
     * @param white the player agent who controls the white pieces.
     * @param black the player agent who controls the black pieces.
     */
    public Board(PlayerAgent white, PlayerAgent black) {
        this.board = new Tile[BOARD_WIDTH][BOARD_WIDTH];
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = new Tile(i, j, this);
            }
        }
        agents.put(Piece.Color.WHITE, white);
        agents.put(Piece.Color.BLACK, black);
    }

    /**
     * Returns the Tile object at the specified position.
     *
     * @param x the x-coordinate of the tile.
     * @param y the y-coordinate of the tile.
     * @return the Tile object at the specified position, null if the position is out of bounds.
     */
    public Tile getTile(int x, int y) {
        if (x < 0 || y < 0 || x >= BOARD_WIDTH || y >= BOARD_WIDTH) {
            return null;
        }
        return board[x][y];
    }

    /**
     * Returns the Piece object at the specified position.
     *
     * @param x the x-coordinate of the tile.
     * @param y the y-coordinate of the tile.
     * @return the Piece object at the specified position, null if there is no piece or the position is out of bounds.
     */
    public Piece getPiece(int x, int y) {
        Tile t = getTile(x, y);
        if (t == null) {
            return null;
        }
        return t.getCurrentPiece();
    }

    /**
     * Returns the PlayerAgent who owns the specified piece.
     *
     * @param piece the piece that you want to check ownership.
     * @return the PlayerAgent who owns the piece.
     */
    public PlayerAgent getPieceOwner(Piece piece) {
        return agents.get(piece.getColor());
    }

    /**
     * Returns the player agent of the specified color.
     *
     * @param color the color of the pieces that the player controls.
     * @return the PlayerAgent who controls the pieces of the specified color.
     */
    public PlayerAgent getAgentByColor(Piece.Color color) {
        return agents.get(color);
    }

    /**
     * Computes and returns the set of Tiles that can be reached by "jumping" from the source Tile in the specified directions.
     *
     * @param source     the Tile from which the moves start.
     * @param directions an array of 2-element arrays, each representing a direction in which a move can be made.
     * @return the set of Tiles that can be reached by jumping from the source Tile in the specified directions.
     */
    public Set<Tile> jumpingMove(Tile source, int[][] directions) {
        Set<Tile> possibleMoves = new HashSet<Tile>();
        int sourceX = source.getX();
        int sourceY = source.getY();
        for (int[] direction : directions) {
            int x = sourceX + direction[0];
            int y = sourceY + direction[1];
            Tile tile = getTile(x, y);
            if (tile == null) {
                continue;
            }
            Piece p = tile.getCurrentPiece();
            if (p != null
                    && p.getColor() == source.getCurrentPiece().getColor()) {
                continue;
            }
            possibleMoves.add(tile);
        }
        return possibleMoves;
    }

    /**
     * Computes and returns the set of Tiles that can be reached by moving linearly from the source Tile.
     *
     * @param sourceTile the Tile from which the moves start.
     * @param xOffset    the change in x-coordinate for each step of the move.
     * @param yOffset    the change in y-coordinate for each step of the move.
     * @return the set of Tiles that can be reached by moving linearly from the source Tile.
     */
    public Set<Tile> linearMove(Tile sourceTile, int xOffset, int yOffset) {
        Set<Tile> tiles = new HashSet<Tile>();
        int x = sourceTile.getX(), y = sourceTile.getY();
        while (true) {
            x += xOffset;
            y += yOffset;
            Tile tile = getTile(x, y);
            if (tile == null) break;
            Piece p = tile.getCurrentPiece();
            if (p != null) {
                // obstruct by enemy pieces, but can capture one in the front
                if (p.getColor() != sourceTile.getCurrentPiece().getColor()) {
                    tiles.add(tile);
                }
                break;
            }
            tiles.add(tile);
        }
        return tiles;
    }

    /**
     * Clones the current board state and returns a new Board object with the same state.
     *
     * @return a new Board object that has the same state as the current board.
     */
    public Board clone() {
        Board cloned = new Board(agents.get(Piece.Color.WHITE), agents.get(Piece.Color.BLACK));
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Piece clonedPiece;
                Piece piece = board[i][j].getCurrentPiece();
                if (piece != null) {
                    clonedPiece = Piece.createPiece(piece.getColor() == Color.BLACK ? Character.toUpperCase(piece.type) : Character.toLowerCase(piece.type));
                    Tile clonedTile = cloned.getTile(i, j);
                    clonedTile.setCurrentPiece(clonedPiece);
                    clonedPiece.setCurrentTile(clonedTile);
                }
            }
        }
        return cloned;
    }
}
