package resonant.tasks;

/**
 * Represents a task that spans a specific time period.
 * <p>
 * An {@code Event} has a description, a start time, and an end time.
 * It is displayed in the format: {@code [E][X] description (from: start to: end)}.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Constructs an {@code Event} task with the specified description, start time, and end time.
     *
     * @param description The description of the event.
     * @param from        The starting time of the event.
     * @param to          The ending time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the starting time of this event.
     *
     * @return The start time as a {@code String}.
     */
    public String from() {
        return from;
    }

    /**
     * Returns the ending time of this event.
     *
     * @return The end time as a {@code String}.
     */
    public String to() {
        return to;
    }

    /**
     * Returns the string representation of the event.
     *
     * @return A formatted string including task type, status, description, start, and end times.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + this.from + " to: " + this.to + ")";
    }
}
