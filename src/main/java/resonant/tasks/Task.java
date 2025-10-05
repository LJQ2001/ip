package resonant.tasks;

/**
 * Represents a general task in the task list.
 * <p>
 * A {@code Task} stores a description and a completion status.
 * Subclasses such as {@code Todo}, {@code Deadline}, and {@code Event}
 * provide more specific types of tasks.
 */
public class Task {
    /** Description of the task. */
    protected final String description;

    /** Whether the task is marked as done. */
    protected boolean isDone;

    /**
     * Constructs a {@code Task} with the specified description.
     * The task is initially not marked as done.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Returns whether this task is done.
     *
     * @return {@code true} if the task is done, otherwise {@code false}.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the description of this task.
     *
     * @return The task description as a {@code String}.
     */
    public String description() {
        return description;
    }

    /**
     * Returns the status icon of the task.
     * {@code "X"} if the task is done, otherwise a blank space.
     *
     * @return A string representing the task's completion status.
     */
    public String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns the string representation of this task,
     * showing its status and description.
     *
     * @return A formatted string representing this task.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + this.description;
    }
}
