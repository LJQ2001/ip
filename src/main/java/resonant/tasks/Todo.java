package resonant.tasks;

/**
 * Represents a simple to-do task without any specific date or time attached.
 * <p>
 * A {@code Todo} is one of the basic task types supported by the program.
 * It inherits from {@link Task} and only requires a description.
 */
public class Todo extends Task {

    /**
     * Constructs a {@code Todo} task with the given description.
     *
     * @param description The description of the to-do task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a string representation of the to-do task.
     * The output includes the type indicator {@code [T]} and the base task details.
     *
     * @return A formatted string representing this to-do task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
