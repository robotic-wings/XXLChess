package XXLChess;

import processing.core.PImage;

/**
 * This class represents the movement animation of a chess piece.
 * It calculates and updates the position of the piece frame by frame, creating a moving effect.
 */
public class AnimationVehicle implements Tickable {
    private final double k1;
    private final double k2;  // The parameters in the linear equation for piece movement.
    private final double originalLeft;
    private final double originalTop;  // The original position of the piece.
    private double left, top;  // The current position of the piece.
    private final Piece piece;  // The piece being moved.
    private final double horizontalDiff;  // The horizontal distance the piece needs to move.
    private final double verticalDiff;  // The vertical distance the piece needs to move.
    private PImage afterimage;  // The image of the piece at the target tile (if any).
    private final double targetLeft;
    private final double targetTop;  // The target position of the piece.

    /**
     * Constructor for the AnimationVehicle class.
     *
     * @param pieceMovementSpeed The speed at which the piece should move.
     * @param maxMovementTime    The maximum time the movement should take.
     * @param m                  The move that the piece is making.
     */
    public AnimationVehicle(int pieceMovementSpeed, int maxMovementTime, Movement m) {
        piece = m.getSourcePiece();
        this.originalLeft = m.getSourceLeft();
        this.originalTop = m.getSourceTop();
        if (m.getTargetPiece() != null) {
            // checking a enemy's piece
            afterimage = m.getTargetPiece().getImage();
        }
        this.targetLeft = m.getTargetLeft();
        this.targetTop = m.getTargetTop();
        horizontalDiff = targetLeft - originalLeft;
        verticalDiff = targetTop - originalTop;
        int maxDisplacement = pieceMovementSpeed * maxMovementTime * App.FPS;
        double distance = Math.sqrt(Math.pow(horizontalDiff, 2) + Math.pow(verticalDiff, 2));
        double realSpeed;
        if (distance > maxDisplacement) {
            realSpeed = distance / maxMovementTime / App.FPS;
        } else {
            realSpeed = pieceMovementSpeed;
        }
        if (horizontalDiff != 0) {
            double tangent = verticalDiff / horizontalDiff;
            k1 = (horizontalDiff / Math.abs(horizontalDiff)) * Math.sqrt(Math.pow(realSpeed, 2) / (1 + Math.pow(tangent, 2)));
            k2 = tangent * k1;
        } else {
            k1 = 0;
            k2 = (verticalDiff / Math.abs(verticalDiff)) * realSpeed;
        }
    }

    /**
     * Get the top margin of the target position.
     *
     * @return The top margin of the target position.
     */
    public double getTargetTop() {
        return targetTop;
    }

    /**
     * Get the left margin of the target position.
     *
     * @return The left margin of the target position.
     */
    public double getTargetLeft() {
        return targetLeft;
    }

    /**
     * Get the details of the movement, including the parameters in the linear equation and the current position.
     *
     * @return A string containing the details of the movement.
     */
    public String getDetails() {
        return "k1: " + k1 + " k2: " + k2 + " x: " + left + " y: " + top + " diffX: " + horizontalDiff + " diffY: " + verticalDiff;
    }

    /**
     * Get the image of the piece at the target tile.
     *
     * @return The image of the piece at the target tile, or null if the target tile is empty.
     */
    public PImage getAfterimage() {
        return afterimage;
    }

    /**
     * Update the position of the piece.
     * If the movement has ended, this method throws an IllegalStateException.
     */
    public void tick() {
        if (isEnded()) throw new IllegalStateException();
        left += k1;
        top += k2;
    }

    /**
     * Get the current left margin of the piece.
     *
     * @return The current left margin of the piece.
     */
    public double getLeft() {
        return originalLeft + left;
    }

    /**
     * Get the current top margin of the piece.
     *
     * @return The current top margin of the piece.
     */
    public double getTop() {
        return originalTop + top;
    }

    /**
     * Check if the movement has ended.
     *
     * @return True if the movement has ended, false otherwise.
     */
    public boolean isEnded() {
        return (Math.abs(left) >= Math.abs(horizontalDiff)) && (Math.abs(top) >= Math.abs(verticalDiff));
    }

    /**
     * Get the piece that is being moved.
     *
     * @return The piece that is being moved.
     */
    public Piece getMovingPiece() {
        return piece;
    }
}
