package resonant.commands;

import resonant.*;

/**
 * Represents a command that marks a specified task in the {@link TaskList} as done.
 * <p>
 * When executed, this command updates the completion status of the task,
 * saves the updated task list to storage, and notifies the user through the UI.
 */
public class MarkCommand extends Command {
    private final int index1Based;

    /**
     * Constructs a {@code MarkCommand} with the specified task index.
     *
     * @param index1Based The one-based index of the task to mark as done.
     */
    public MarkCommand(int index1Based) {
        this.index1Based = index1Based;
    }

    /**
     * Executes the command by marking the specified task as done,
     * saving the updated task list to {@link Storage},
     * and displaying a confirmation message via the {@link Ui}.
     *
     * @param tasks   The current list of tasks.
     * @param ui      The user interface handler used to display messages.
     * @param storage The storage handler used to persist the task list.
     * @throws Exception If the task index is invalid or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        var t = tasks.get(index1Based);
        t.mark();
        storage.save(tasks.asList());
        ui.box(" Nice! I've marked this task as done:", "   " + t);
    }
}
