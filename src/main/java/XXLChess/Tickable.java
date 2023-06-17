package XXLChess;

/**
 * The Tickable interface represents objects that can be updated (ticked) and can end their lifecycle.
 * Classes that implement Tickable need to provide a way to change their state on each "tick" of the game's main loop
 * and a way to check whether their lifecycle has ended.
 */
public interface Tickable {

    /**
     * Checks if the lifecycle of this Tickable object has ended.
     * This method is typically used by the game's main loop to decide whether to remove this object from the game.
     *
     * @return true if this object's lifecycle has ended, false otherwise
     */
    boolean isEnded();

    /**
     * Updates the state of this Tickable object.
     * This method is typically called on each "tick" of the game's main loop.
     *
     * @throws IllegalStateException if this method is called after isEnded() has returned true
     */
    void tick() throws IllegalStateException;

}
