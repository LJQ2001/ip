package resonant.commands;

import resonant.*;

/**
 * Represents a command that marks a specified task in the {@link TaskList} as not done.
 * <p>
 * When executed, this command updates the task's completion status to "not done",
 * saves the modified task list to storage, and informs the user via the {@link Ui}.
 */
public class UnmarkCommand extends Command {
    private final int index1Based;

    /**
     * Constructs an {@code UnmarkCommand} with the specified task index.
     *
     * @param index1Based The one-based index of the task to mark as not done.
     */
    public UnmarkCommand(int index1Based) {
        this.index1Based = index1Based;
    }

    /**
     * Executes the command by marking the specified task as not done,
     * saving the updated task list to {@link Storage},
     * and displaying a confirmation message through the {@link Ui}.
     *
     * @param tasks   The current list of tasks.
     * @param ui      The user interface handler used to display messages.
     * @param storage The storage handler used to persist the task list.
     * @throws Exception If the task index is invalid or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        var t = tasks.get(index1Based);
        t.unmark();
        storage.save(tasks.asList());
        ui.box(" OK, I've marked this task as not done yet:", "   " + t);
    }
}
