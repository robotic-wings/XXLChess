package XXLChess;

public class Tile {
    public static final int CELLSIZE = 48;
    private final int x;
    private final int y;
    private Piece currentPiece;
    private final Board board;


    public Tile(int x, int y, Board board) {
        this.x = x;
        this.y = y;
        // a checkerboard pattern as below with alternating black and white tiles
        this.board = board;
    }

    /**
     * @return Board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * @return int
     */
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLeft() {
        return x * CELLSIZE;
    }

    public int getTop() {
        return y * CELLSIZE;
    }

    public Piece getCurrentPiece() {
        return currentPiece;
    }

    public void setCurrentPiece(Piece currentPiece) {
        this.currentPiece = currentPiece;
    }


}
