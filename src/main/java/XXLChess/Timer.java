package XXLChess;

/**
 * The Timer class represents a countdown timer that counts down frame by frame.
 * It implements the Tickable interface to allow its state to be updated on each "tick" of the game's main loop
 * and to check if the countdown has ended.
 */
public class Timer implements Tickable {

    // The number of frames remaining for the countdown
    private int remainingFrames;

    /**
     * Constructs a new Timer with a specified amount of time.
     *
     * @param remainingSecs The amount of time in seconds for the countdown
     */
    public Timer(float remainingSecs) {
        this.remainingFrames = (int) (remainingSecs * App.FPS);
    }

    /**
     * Retrieves the remaining time in seconds.
     *
     * @return The remaining time in seconds
     */
    public float getRemainingSecs() {
        return remainingFrames / App.FPS;
    }

    /**
     * Adds time to the timer.
     *
     * @param remainingSecs The amount of time in seconds to add
     */
    public void addRemainingSecs(float remainingSecs) {
        this.remainingFrames += (float) (remainingSecs * App.FPS);
    }

    /**
     * Decreases the remaining time by one frame.
     *
     * @throws IllegalStateException if the countdown has already ended
     */
    public void tick() {
        if (isEnded()) throw new IllegalStateException();
        remainingFrames -= 1;
    }

    /**
     * Checks if the countdown has ended.
     *
     * @return true if the countdown has ended, false otherwise
     */
    @Override
    public boolean isEnded() {
        return remainingFrames <= 0;
    }
}
