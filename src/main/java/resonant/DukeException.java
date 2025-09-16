package resonant;

/** Custom exception type for Resonant/Duke-specific errors. */
public class DukeException extends Exception {
    public DukeException(String message) {
        super(message);
    }
}