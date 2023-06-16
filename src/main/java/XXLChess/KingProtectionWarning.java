package XXLChess;

/**
 * This class represents a warning that indicates the king is in danger.
 * The warning will be visualized by a flashing red light on the king's location on the chessboard.
 * It implements the Tickable interface because it represents a time-dependent event.
 */
public class KingProtectionWarning implements Tickable {
    private Timer timer = null;
    private int n = -1;  // nothing when n=0,2,4; red when n=1,3,5
    private boolean redLight = false;

    /**
     * Creates a new KingProtectionWarning.
     */
    public KingProtectionWarning() {
        next();
    }

    /**
     * Checks if the red light is on.
     *
     * @return true if the red light is on, false otherwise.
     */
    public boolean isRedLight() {
        return redLight;
    }

    /**
     * Checks if the warning has ended.
     *
     * @return true if the warning has ended, false otherwise.
     */
    @Override
    public boolean isEnded() {
        return n >= 6;
    }

    /**
     * Advances to the next state of the warning.
     */
    private void next() {
        timer = new Timer(0.5f);
        redLight = !redLight;
        n++;
    }

    /**
     * Updates the state of the warning.
     *
     * @throws IllegalStateException if the warning has ended.
     */
    @Override
    public void tick() throws IllegalStateException {
        if (isEnded()) throw new IllegalStateException();
        if (timer.isEnded()) {
            next();
        } else {
            timer.tick();
        }
    }
}
