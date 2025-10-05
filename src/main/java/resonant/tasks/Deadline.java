package resonant.tasks;

/**
 * Represents a task with a specific deadline.
 * <p>
 * A {@code Deadline} has a description and a due date/time,
 * and is displayed with the format: {@code [D][X] description (by: date/time)}.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Constructs a {@code Deadline} task with the specified description and due date/time.
     *
     * @param description The description of the deadline task.
     * @param by          The due date/time of the task.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the due date/time of this deadline task.
     *
     * @return The due date/time as a {@code String}.
     */
    public String by() {
        return by;
    }

    /**
     * Returns the string representation of the deadline task.
     *
     * @return A formatted string including task type, status, description, and due date/time.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.by + ")";
    }
}
