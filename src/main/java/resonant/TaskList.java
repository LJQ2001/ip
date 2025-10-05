package resonant;

import resonant.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a dynamic list of {@link Task} objects managed by the user.
 * <p>
 * The {@code TaskList} class provides high-level operations for adding,
 * retrieving, removing, and searching tasks. It enforces constraints such as
 * valid index access and a maximum task count of 100.
 * </p>
 */
public class TaskList {

    /** The internal modifiable list of tasks. */
    private final List<Task> tasks;

    /**
     * Constructs an empty {@code TaskList}.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a {@code TaskList} initialized with an existing collection of tasks.
     * <p>
     * If {@code initial} is {@code null}, the list will be initialized as empty.
     * </p>
     *
     * @param initial the initial list of tasks to populate, or {@code null} for an empty list
     */
    public TaskList(List<Task> initial) {
        this.tasks = new ArrayList<>(initial == null ? List.of() : initial);
    }

    /**
     * Returns the total number of tasks currently in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Retrieves a task based on its 1-based index.
     * <p>
     * For example, {@code get(1)} retrieves the first task in the list.
     * </p>
     *
     * @param index1Based the position of the task (1-based)
     * @return the {@link Task} at the specified index
     * @throws DukeException if the index is out of range
     */
    public Task get(int index1Based) throws DukeException {
        int i = index1Based - 1;
        if (i < 0 || i >= tasks.size()) {
            throw new DukeException("Task number " + index1Based + " is out of range. You have " + size() + " task(s).");
        }
        return tasks.get(i);
    }

    /**
     * Adds a new task to the list.
     * <p>
     * The list has a hard limit of 100 tasks to prevent unbounded growth.
     * </p>
     *
     * @param t the {@link Task} to add
     * @throws DukeException if the task list already contains 100 items
     */
    public void add(Task t) throws DukeException {
        if (tasks.size() >= 100) {
            throw new DukeException("Your task list is full (100 items). Consider deleting some tasks.");
        }
        tasks.add(t);
    }

    /**
     * Removes and returns the task at the specified 1-based index.
     *
     * @param index1Based the position of the task to remove
     * @return the removed {@link Task}
     * @throws DukeException if the index is out of range
     */
    public Task remove(int index1Based) throws DukeException {
        Task t = get(index1Based);
        tasks.remove(index1Based - 1);
        return t;
    }

    /**
     * Finds all tasks that contain the given keyword (case-insensitive) in their description.
     *
     * @param keyword the search term to match against task descriptions
     * @return a list of matching {@link Task} objects; may be empty if no matches are found
     */
    public List<Task> find(String keyword) {
        String kw = keyword.toLowerCase();
        List<Task> out = new ArrayList<>();
        for (Task t : tasks) {
            if (t.description().toLowerCase().contains(kw)) {
                out.add(t);
            }
        }
        return out;
    }

    /**
     * Returns an unmodifiable view of the internal task list.
     * <p>
     * This ensures external classes cannot modify the list directly.
     * </p>
     *
     * @return an unmodifiable {@link List} of tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
